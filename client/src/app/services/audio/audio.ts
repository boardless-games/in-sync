import { Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { AudioFile } from "../../constants/AudioFile";

@Service()
export class AudioService {
  private static readonly PLAY_AUDIO_DELAY = 0.01;
  private static readonly AUDIO_FILE_PREFIX = "/audio/";

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
        const fetchResponse = await fetch(`${AudioService.AUDIO_FILE_PREFIX}${file}`);
        const arrayBuffer = await fetchResponse.arrayBuffer();

        const audioBuffer = await this.ctx.decodeAudioData(arrayBuffer);

        this.audioBuffers.set(file, audioBuffer);
      } catch (error) {
        console.error(error);
      }
    }
    this._ready.next(true);
  }

  /**
   * All time options should be given in seconds.
   */
  public async playAudioFile(
    file: AudioFile,
    options?: {
      when?: number;
      offset?: number;
      duration?: number;
      playbackRate?: number;
      volume?: number;
      fadeIn?: number;
      fadeOut?: number;
      loop?: boolean;
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

      if (options?.playbackRate) {
        audioSourceNode.playbackRate.value = options.playbackRate;
      }

      if (options?.loop) {
        audioSourceNode.loop = options.loop;
      }

      const gainNode = this.ctx.createGain();
      let defaultVolume = gainNode.gain.defaultValue;
      if (options?.volume !== undefined) {
        gainNode.gain.value = options.volume;
        defaultVolume = options.volume;
      }

      const startTime = this.ctx.currentTime + (options?.when || 0) + AudioService.PLAY_AUDIO_DELAY;
      if (options?.fadeIn !== undefined) {
        gainNode.gain.setValueCurveAtTime([0, defaultVolume], startTime, options.fadeIn);
      }

      if (options?.fadeOut !== undefined) {
        gainNode.gain.setValueCurveAtTime(
          [defaultVolume, 0],
          startTime +
            (options?.duration ?? audioBuffer.duration) -
            options.fadeOut +
            AudioService.PLAY_AUDIO_DELAY,
          options.fadeOut
        );
      }
      audioSourceNode.connect(gainNode);
      gainNode.connect(this.ctx.destination);

      audioSourceNode.start(startTime, options?.offset ?? 0, options?.duration);
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
