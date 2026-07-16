import { Component, DOCUMENT, inject, OnInit, signal } from "@angular/core";

@Component({
  selector: "app-root",
  imports: [],
  templateUrl: "./app.html",
  styleUrl: "./app.css"
})
export class App implements OnInit {
  private readonly document = inject(DOCUMENT);

  protected readonly initialized = signal(false);

  constructor() {
    this.document.addEventListener("click", this.initialize);
  }

  ngOnInit(): void {}

  private initialize = () => {
    this.initialized.set(true);
    this.document.removeEventListener("click", this.initialize);
  };
}
