import { Component, inject, signal, WritableSignal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { RouterOutlet } from "@angular/router";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { Alert } from "./services/alert/alert";
import { AudioService } from "./services/audio/audio";

@Component({
  selector: "app-root",
  imports: [BgSplash, RouterOutlet],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
  host: {
    class: "full-size"
  }
})
export class App {
  protected readonly audioService = inject(AudioService);
  private readonly alertService = inject(Alert);
  protected readonly initialized = signal(false);

  private readonly alerts: string[] = [];
  protected alert: WritableSignal<string | null> = signal(null);

  constructor() {
    this.alertService.alerts.pipe(takeUntilDestroyed()).subscribe((alert: string) => {
      this.alerts.push(alert);
      if (this.alert() === null) {
        this.alertTimeoutHandler();
      }
    });
  }

  private alertTimeoutHandler = () => {
    const nextAlert = this.alerts.shift();
    if (nextAlert === undefined) {
      this.alert.set(null);
    } else {
      this.alert.set(nextAlert);
      setTimeout(this.alertTimeoutHandler, 5000);
    }
  };

  protected continue() {
    this.initialized.set(true);
  }
}
