import { inject, Service } from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  RedirectCommand,
  Router
} from "@angular/router";
import { catchError } from "rxjs";
import { GameCodeDto } from "../../interfaces/dtos/GameCodeDto";
import { AlertService } from "../../services/alert/alert";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { Validation } from "../../services/validation/validation";

@Service()
export class GameGuard implements CanActivate {
  private readonly router = inject(Router);
  private readonly validationService = inject(Validation);
  private readonly alertService = inject(AlertService);
  private readonly inSyncApi = inject(InSyncApi);
  private readonly redirect = new RedirectCommand(this.router.parseUrl(""));

  canActivate(route: ActivatedRouteSnapshot): MaybeAsync<GuardResult> {
    const gameCode = route.params["gameCode"];
    if (typeof gameCode !== "string" || !this.validationService.validateGameCode(gameCode)) {
      this.alertService.alert("Invalid game code.");
      return this.redirect;
    }

    return new Promise((resolve) => {
      this.inSyncApi
        .getGame(gameCode, false)
        .pipe(
          catchError((error) => {
            resolve(this.redirect);
            throw error;
          }),
          catchError(this.inSyncApi.genericCatchError)
        )
        .subscribe((response: GameCodeDto) => {
          resolve(response.gameCode === gameCode ? true : this.redirect);
        });
    });
  }
}
