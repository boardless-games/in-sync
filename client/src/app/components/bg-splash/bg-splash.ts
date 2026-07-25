import { Component, DOCUMENT, inject, input, OnInit, output, signal } from "@angular/core";
import { filter, map, merge, Observable, take } from "rxjs";

@Component({
  selector: "app-bg-splash",
  templateUrl: "./bg-splash.html",
  styleUrl: "./bg-splash.css"
})
export class BgSplash implements OnInit {
  private static readonly MIN_LOADING_TIME = 2000; // Millis
  private readonly document = inject(DOCUMENT);
  protected readonly loading = signal(true);
  ready = input(new Array<Observable<boolean>>());
  continue = output();

  ngOnInit(): void {
    const start = Date.now();
    merge(
      ...this.ready().map((o) =>
        o.pipe(
          filter((ready) => ready),
          map(() => undefined),
          take(1)
        )
      )
    ).subscribe({
      complete: () => {
        const elapsed = Date.now() - start;
        setTimeout(
          () => {
            this.loading.set(false);
            this.document.addEventListener("click", this.clickHandler);
          },
          elapsed >= BgSplash.MIN_LOADING_TIME ? 0 : BgSplash.MIN_LOADING_TIME - elapsed
        );
      }
    });
  }

  private clickHandler = () => {
    this.document.removeEventListener("click", this.clickHandler);
    this.continue.emit();
  };
}
