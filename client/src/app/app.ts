import { Component, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";

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
  protected readonly initialized = signal(false);

  protected splashClicked() {
    this.initialized.set(true);
  }
}
