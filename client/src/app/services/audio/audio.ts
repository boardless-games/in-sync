import { Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { AudioFile } from "../../constants/AudioFile";

@Service()
export class Audio {
  private readonly ctx = new AudioContext();
  private readonly nodes = new Map<AudioFile, AudioBufferSourceNode>();
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

        const buffer = await this.ctx.decodeAudioData(arrayBuffer);
        const node = this.ctx.createBufferSource();
        node.buffer = buffer;
        node.connect(this.ctx.destination);
        this.nodes.set(file, node);
      } catch (error) {
        console.error(error);
      }
    }
    this._ready.next(true);
  }

  public play(file: AudioFile, when?: number, offset?: number, duration?: number) {
    try {
      if (this.ctx.state === "suspended") {
        this.ctx.resume();
      }

      const node = this.nodes.get(file);
      if (node === undefined) {
        return;
      }

      node.start(when, offset, duration);
    } catch (error) {
      console.error(error);
    }
  }

  public pause(file: AudioFile, when?: number) {
    try {
      if (this.ctx.state === "suspended") {
        this.ctx.resume();
      }

      const node = this.nodes.get(file);
      if (node === undefined) {
        return;
      }

      node.stop(when);
    } catch (error) {
      console.error(error);
    }
  }
}
