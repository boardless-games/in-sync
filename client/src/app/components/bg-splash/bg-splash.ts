import { Component, DOCUMENT, EventEmitter, inject, OnInit, Output } from "@angular/core";

@Component({
  selector: "app-bg-splash",
  imports: [],
  templateUrl: "./bg-splash.html",
  styleUrl: "./bg-splash.css",
  host: {
    class: "flex-column"
  }
})
export class BgSplash implements OnInit {
  @Output() clicked: EventEmitter<void> = new EventEmitter();

  private readonly document = inject(DOCUMENT);

  ngOnInit(): void {
    this.document.addEventListener("click", this.emitClick);
  }

  private emitClick() {
    this.document.removeEventListener("click", this.emitClick);
    this.clicked.emit();
  }
}
