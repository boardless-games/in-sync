import { EventEmitter, Service } from "@angular/core";
import { Alert } from "../../interfaces/Alert";

@Service()
export class AlertService {
  private readonly _alerts = new EventEmitter<Alert>();
  public readonly alerts = this._alerts.asObservable();

  public alert(alert: string, duration?: number) {
    this._alerts.emit({ alert: alert, duration: duration ?? 10_000 });
  }
}
