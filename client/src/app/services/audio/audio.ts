import { Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { AudioFile } from "../../constants/AudioFile";

@Service()
export class AudioService {
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

  public play(
    file: AudioFile,
    options?: {
      when?: number;
      offset?: number;
      duration?: number;
      playbackRate?: number;
      volume?: number;
    }
  ): AudioBufferSourceNode | undefined {
    try {
      if (this.ctx.state === "suspended") {
        this.ctx.resume();
      }

      const audioBuffer = this.audioBuffers.get(file);
      if (audioBuffer === undefined) {
        return undefined;
      }

      const sourceNode = this.ctx.createBufferSource();
      sourceNode.buffer = audioBuffer;

      if (options?.playbackRate) {
        sourceNode.playbackRate.value = options.playbackRate;
      }

      if (options?.volume) {
        const gainNode = this.ctx.createGain();
        gainNode.gain.value = options.volume;
        sourceNode.connect(gainNode);
        gainNode.connect(this.ctx.destination);
      } else {
        sourceNode.connect(this.ctx.destination);
      }

      sourceNode.start(options?.when, options?.offset, options?.duration);
      return sourceNode;
    } catch (error) {
      console.error(error);
      return undefined;
    }
  }

  public pause(sourceNode: AudioBufferSourceNode, when?: number) {
    try {
      if (this.ctx.state === "suspended") {
        this.ctx.resume();
      }

      sourceNode.stop(when);
    } catch (error) {
      console.error(error);
    }
  }
}
