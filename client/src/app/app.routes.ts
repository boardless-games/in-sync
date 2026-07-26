import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, RedirectCommand, Router, Routes } from "@angular/router";
import { Game } from "./components/game/game";
import { Menu } from "./components/menu/menu";
import { GameCodeDto } from "./dtos/GameCodeDto";
import { Alert } from "./services/alert/alert";
import { InSyncApi } from "./services/in-sync-api/in-sync-api";
import { Validation } from "./services/validation/validation";

export const routes: Routes = [
  {
    path: "",
    component: Menu
  },
  {
    path: "game/:gameCode",
    component: Game,
    canActivate: [
      (route: ActivatedRouteSnapshot) => {
        const validationService = inject(Validation);
        const alertService = inject(Alert);
        const router = inject(Router);

        const redirect = new RedirectCommand(router.parseUrl(""));
        const gameCode = route.params["gameCode"];
        if (typeof gameCode !== "string" || !validationService.validateGameCode(gameCode)) {
          alertService.alert("Invalid game code.");
          return redirect;
        }
        return new Promise((resolve) => {
          const inSyncApi = inject(InSyncApi);
          inSyncApi.getGame(gameCode).subscribe({
            next: (response: GameCodeDto) => {
              resolve(response.gameCode === gameCode ? true : redirect);
            },
            complete: () => {
              resolve(redirect);
            }
          });
        });
      }
    ]
  },
  {
    path: "**",
    redirectTo: ""
  }
];
