import { Component, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { environment } from "../environments/environment";

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
  protected readonly initialized = signal(!environment.production);

  protected splashClicked() {
    this.initialized.set(true);
  }
}
