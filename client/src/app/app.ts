import { Component, DOCUMENT, inject, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { AudioService } from "./services/audio/audio";
import { AudioFile } from "./constants/AudioFile";
import { environment } from "../environments/environment";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { JoinGameForm } from "./models/JoinGameForm";
import { RouterOutlet } from "../../node_modules/@angular/router/types/_router_module-chunk";

@Component({
  selector: "app-root",
  imports: [BgSplash, ReactiveFormsModule, RouterOutlet],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
  host: {
    class: "full-size"
  }
})
export class App {
  protected readonly audioService = inject(AudioService);
  protected readonly initialized = signal(false);

  protected continue() {
    this.initialized.set(true);
  }
}
