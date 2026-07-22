import { EventEmitter, Service } from "@angular/core";

@Service()
export class Alert {
  private readonly _alerts = new EventEmitter<string>();
  public readonly alerts = this._alerts.asObservable();

  public alert(alert: string) {
    this._alerts.emit(alert);
  }
}
