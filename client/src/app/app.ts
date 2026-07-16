import { Component, DOCUMENT, inject, signal } from "@angular/core";

@Component({
  selector: "app-root",
  imports: [],
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
