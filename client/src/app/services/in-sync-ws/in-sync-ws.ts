import { EventEmitter, Service } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { MessageDto } from "../../interfaces/dtos/MessageDto";

@Service()
export class InSyncWs {
  private readonly BASE_PATH = "/in-sync-ws";
  private webSocket?: WebSocket;
  private readonly _connected = new BehaviorSubject(false);
  public readonly connected = this._connected.asObservable();
  private readonly _messaged = new EventEmitter<MessageDto>();
  public readonly messaged = this._messaged.asObservable();

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
    this._connected.next(false);
  };

  private errorHandler = (event: Event) => {
    console.log("Web socket connection error.", event);
  };

  private messageHandler = (event: MessageEvent) => {
    console.log("Web socket connection message.", event);
    try {
      this._messaged.emit(JSON.parse(event.data));
    } catch (error) {
      console.error("Invalid message received: ", event.data, error);
    }
  };

  private openHandler = (event: Event) => {
    console.log("Web socket connection opened.", event);
    this._connected.next(true);
  };
}
