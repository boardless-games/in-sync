import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { inject, Service } from "@angular/core";
import { GameCodeDto } from "../../dtos/GameCodeDto";
import { Alert } from "../alert/alert";
import { ErrorDto } from "../../dtos/ErrorDto";
import { catchError, EMPTY } from "rxjs";

@Service()
export class InSyncApi {
  private readonly alertService = inject(Alert);
  private readonly http = inject(HttpClient);
  private static readonly BASE_PATH = "/in-sync-api";

  public genericCatchError = (errorResponse: HttpErrorResponse) => {
    let message: string;
    if (errorResponse.status !== 0) {
      message = (errorResponse.error as ErrorDto).error;
    } else {
      message = "Error: Check device connection.";
    }
    this.alertService.alert(message);
    return EMPTY;
  };

  public newGame() {
    return this.http.post<GameCodeDto>(`${InSyncApi.BASE_PATH}/game`, null);
  }

  public getGame(gameCode: string) {
    return this.http.get<GameCodeDto>(`${InSyncApi.BASE_PATH}/game/${gameCode}`);
  }
}
