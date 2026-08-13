import { DOCUMENT, EventEmitter, inject, Service } from "@angular/core";

@Service()
export class KeyPress {
  private readonly document = inject(DOCUMENT);
  private readonly _keyPressed = new EventEmitter<KeyboardEvent>();
  public readonly keyPressed = this._keyPressed.asObservable();
  constructor() {
    this.document.addEventListener("keydown", (event: KeyboardEvent) => {
      this._keyPressed.emit(event);
    });
  }
}
