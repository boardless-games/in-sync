import { Component, inject, signal, WritableSignal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { RouterOutlet } from "@angular/router";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { IconButton } from "./components/icon-button/icon-button";
import { Icon } from "./constants/Icon";
import { Alert } from "./services/alert/alert";
import { AudioService } from "./services/audio/audio";
import { environment } from "../environments/environment";

@Component({
  selector: "app-root",
  imports: [BgSplash, RouterOutlet, IconButton],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
  host: {
    class: "full-size"
  }
})
export class App {
  protected readonly audioService = inject(AudioService);
  private readonly alertService = inject(Alert);
  protected readonly initialized = signal(!environment.production);

  private readonly alerts: string[] = [];
  protected alert: WritableSignal<string> = signal("");
  private alertTimeout: number | undefined = undefined;

  protected readonly icons = Icon;

  constructor() {
    this.alertService.alerts.pipe(takeUntilDestroyed()).subscribe((alert: string) => {
      this.alerts.push(alert);
      if (this.alertTimeout === undefined) {
        this.showNextAlert();
      }
    });
  }

  protected showNextAlert = () => {
    clearTimeout(this.alertTimeout);
    const nextAlert = this.alerts.shift();
    if (nextAlert === undefined) {
      this.alert.set("");
      this.alertTimeout = undefined;
    } else {
      this.alert.set(nextAlert);
      this.alertTimeout = setTimeout(this.showNextAlert, 10000);
    }
  };

  protected continue() {
    this.initialized.set(true);
  }
}
