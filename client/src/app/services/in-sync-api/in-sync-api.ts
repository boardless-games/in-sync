import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { inject, Service } from "@angular/core";
import { GameCodeDto } from "../../interfaces/dtos/GameCodeDto";
import { AlertService } from "../alert/alert";
import { ErrorDto } from "../../interfaces/dtos/ErrorDto";
import { catchError, EMPTY, Observable } from "rxjs";
import { PlayerNameDto } from "../../interfaces/dtos/PlayerNameDto";

@Service()
export class InSyncApi {
  private readonly alertService = inject(AlertService);
  private readonly http = inject(HttpClient);
  private readonly BASE_PATH = "/in-sync-api";

  public genericCatchError = (errorResponse: HttpErrorResponse) => {
    let message: string;
    if (errorResponse.status !== 0) {
      message =
        (errorResponse?.error as ErrorDto)?.error ||
        "Unknown server error. Please try again later.";
    } else {
      message = "Unknown client error. Check device connection.";
    }
    this.alertService.alert(message);
    return EMPTY;
  };

  private applyGenericCatchError<T>(request: Observable<T>, apply = true) {
    return apply ? request.pipe(catchError(this.genericCatchError)) : request;
  }

  public newGame(useGenericCatchError = true) {
    return this.applyGenericCatchError(
      this.http.post<GameCodeDto>(`${this.BASE_PATH}/game`, null),
      useGenericCatchError
    );
  }

  public deleteOrphanGame(gameCode: string, useGenericCatchError = true) {
    return this.applyGenericCatchError(
      this.http.delete<void>(`${this.BASE_PATH}/game/${gameCode}`),
      useGenericCatchError
    );
  }

  public getGame(gameCode: string, useGenericCatchError = true) {
    return this.applyGenericCatchError(
      this.http.get<GameCodeDto>(`${this.BASE_PATH}/game/${gameCode}`),
      useGenericCatchError
    );
  }

  public newPlayer(gameCode: string, playerName: PlayerNameDto, useGenericCatchError = true) {
    return this.applyGenericCatchError(
      this.http.post<PlayerNameDto>(`${this.BASE_PATH}/game/${gameCode}/player`, playerName),
      useGenericCatchError
    );
  }
}
