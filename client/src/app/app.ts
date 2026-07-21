import { Component, inject, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { AudioService } from "./services/audio/audio";
import { AudioFile } from "./constants/AudioFile";
import { environment } from "../environments/environment";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { JoinGameForm } from "./models/JoinGameForm";
import packageJson from "../../package.json";

@Component({
  selector: "app-root",
  imports: [BgSplash, ReactiveFormsModule],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class App {
  private readonly formBuilder = inject(FormBuilder);
  protected readonly audioService = inject(AudioService);
  protected readonly initialized = signal(!environment.production);
  protected readonly joinGameForm = this.formBuilder.group<JoinGameForm>({
    gameCode: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9]{6}$/)]
    })
  });
  protected readonly version = signal(packageJson.version);

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

  protected joinGame() {
    console.log(this.joinGameForm.getRawValue());
  }
}
