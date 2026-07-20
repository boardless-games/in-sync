import { Component, DOCUMENT, EventEmitter, inject, OnInit, Output, signal } from "@angular/core";
import { filter, map, merge, take } from "rxjs";
import { AudioService } from "../../services/audio/audio";

@Component({
  selector: "app-bg-splash",
  templateUrl: "./bg-splash.html",
  styleUrl: "./bg-splash.css"
})
export class BgSplash implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly audioService = inject(AudioService);

  @Output() clicked = new EventEmitter<void>();

  protected readonly ready = signal(false);

  ngOnInit(): void {
    merge(
      this.audioService.ready.pipe(
        filter((ready) => ready),
        map(() => {}),
        take(1)
      )
    )
      .pipe(take(1))
      .subscribe(() => {
        this.document.addEventListener("click", this.emitClick);
        this.ready.set(true);
      });
  }

  private emitClick = () => {
    this.document.removeEventListener("click", this.emitClick);
    this.clicked.emit();
  };
}
