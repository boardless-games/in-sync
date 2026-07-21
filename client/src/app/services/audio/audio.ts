import { Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { AudioFile } from "../../constants/AudioFile";

@Service()
export class AudioService {
  private static readonly PLAY_AUDIO_DELAY = 0.01;

  private readonly ctx = new AudioContext();
  private readonly audioBuffers = new Map<AudioFile, AudioBuffer>();
  private readonly _ready = new BehaviorSubject<boolean>(false);
  public readonly ready = this._ready.asObservable();

  constructor() {
    this.init();
  }

  private async init() {
    const files = Object.values(AudioFile);
    for (const file of files) {
      try {
        const fetchResponse = await fetch(file);
        const arrayBuffer = await fetchResponse.arrayBuffer();

        const audioBuffer = await this.ctx.decodeAudioData(arrayBuffer);

        this.audioBuffers.set(file, audioBuffer);
      } catch (error) {
        console.error(error);
      }
    }
    this._ready.next(true);
  }

  public async playAudioFile(
    file: AudioFile,
    options?: {
      when?: number;
      offset?: number;
      duration?: number;
      playbackRate?: number;
      volume?: number;
    }
  ): Promise<AudioBufferSourceNode | undefined> {
    try {
      if (!this._ready.value) {
        return undefined;
      }

      if (this.ctx.state === "suspended") {
        await this.ctx.resume();
      }

      const audioBuffer = this.audioBuffers.get(file);
      if (audioBuffer === undefined) {
        return undefined;
      }

      const audioSourceNode = this.ctx.createBufferSource();
      audioSourceNode.buffer = audioBuffer;
      let audioNode: AudioNode = audioSourceNode;

      if (options?.playbackRate) {
        audioSourceNode.playbackRate.value = options.playbackRate;
      }

      if (options?.volume !== undefined) {
        const gainNode = this.ctx.createGain();
        gainNode.gain.value = options.volume;
        audioSourceNode.connect(gainNode);
        audioNode = gainNode;
      }

      audioNode.connect(this.ctx.destination);
      audioSourceNode.start(
        this.ctx.currentTime + (options?.when || 0) + AudioService.PLAY_AUDIO_DELAY,
        options?.offset,
        options?.duration
      );
      return audioSourceNode;
    } catch (error) {
      console.error(error);
      return undefined;
    }
  }

  public async pause(sourceNode: AudioBufferSourceNode, when?: number) {
    try {
      if (!this._ready.value) {
        return undefined;
      }

      if (this.ctx.state === "suspended") {
        await this.ctx.resume();
      }

      sourceNode.stop(this.ctx.currentTime + (when || 0) + AudioService.PLAY_AUDIO_DELAY);
    } catch (error) {
      console.error(error);
    }
  }
}
