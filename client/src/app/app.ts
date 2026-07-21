import { Component, inject, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { AudioService } from "./services/audio/audio";
import { AudioFile } from "./constants/AudioFile";

@Component({
  selector: "app-root",
  imports: [BgSplash],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
  host: {
    class: "full-size flex-column"
  }
})
export class App {
  protected readonly audioService = inject(AudioService);
  // protected readonly initialized = signal(!environment.production);
  protected readonly initialized = signal(false);

  protected continue() {
    this.initialized.set(true);
    this.audioService.playAudioFile(AudioFile.INFLATE_BALLOON, {
      playbackRate: 1.63,
      volume: 0.25
    });
    setTimeout(() => {
      this.audioService.playAudioFile(AudioFile.INFLATE_BALLOON, {
        playbackRate: 1.63,
        volume: 0.25
      });
    }, 1250);
  }
}
