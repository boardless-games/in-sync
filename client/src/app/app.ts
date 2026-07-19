import { Component, DOCUMENT, inject, signal } from "@angular/core";
import { BgSplash } from "./components/bg-splash/bg-splash";

@Component({
  selector: "app-root",
  imports: [BgSplash],
  templateUrl: "./app.html",
  styleUrl: "./app.css"
})
export class App {
  private readonly document = inject(DOCUMENT);

  protected readonly initialized = signal(false);

  constructor() {
    this.document.addEventListener("click", this.initialize);
  }

  private initialize = () => {
    this.initialized.set(true);
    this.document.removeEventListener("click", this.initialize);
  };
}
