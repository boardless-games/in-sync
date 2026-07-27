import { EventEmitter, Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Service()
export class InSyncWs {
  private readonly BASE_PATH = "/in-sync-ws";
  private webSocket?: WebSocket;
  private readonly _open = new BehaviorSubject(false);
  public readonly open = this._open.asObservable();
  private readonly _message = new EventEmitter<string>();
  public readonly message = this._message.asObservable();

  public connect(gameCode: string, playerName: string) {
    if (
      this.webSocket?.readyState === WebSocket.OPEN ||
      this.webSocket?.readyState === WebSocket.CONNECTING
    ) {
      return;
    }

    this.webSocket = new WebSocket(
      `${this.BASE_PATH}?gameCode=${gameCode}&playerName=${playerName}`
    );

    this.webSocket.addEventListener("close", this.closeHandler);
    this.webSocket.addEventListener("error", this.errorHandler);
    this.webSocket.addEventListener("message", this.messageHandler);
    this.webSocket.addEventListener("open", this.openHandler);
  }

  public disconnect() {
    this.webSocket?.close();
  }

  public send(message: string) {
    if (this.webSocket?.readyState !== WebSocket.OPEN) {
      return;
    }
    this.webSocket.send(message);
  }

  private closeHandler = (event: CloseEvent) => {
    console.log("Web socket connection closed.", event);
    this._open.next(false);
  };

  private errorHandler = (event: Event) => {
    console.log("Web socket connection error.", event);
  };

  private messageHandler = (event: MessageEvent) => {
    console.log("Web socket connection message.", event);
    this._message.emit(event.data);
  };

  private openHandler = (event: Event) => {
    console.log("Web socket connection opened.", event);
    this._open.next(true);
  };
}
