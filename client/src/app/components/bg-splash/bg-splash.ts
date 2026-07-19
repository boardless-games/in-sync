import { Component, DOCUMENT, EventEmitter, inject, OnInit, Output } from "@angular/core";

@Component({
  selector: "app-bg-splash",
  imports: [],
  templateUrl: "./bg-splash.html",
  styleUrl: "./bg-splash.css",
  host: {
    class: "flex-column full-size"
  }
})
export class BgSplash implements OnInit {
  private readonly document = inject(DOCUMENT);

  @Output() clicked = new EventEmitter<void>();

  ngOnInit(): void {
    this.document.addEventListener("click", this.emitClick);
  }

  private emitClick = () => {
    this.document.removeEventListener("click", this.emitClick);
    this.clicked.emit();
  };
}
