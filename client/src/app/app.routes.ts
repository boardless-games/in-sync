import { Routes } from "@angular/router";
import { Game } from "./components/game/game";
import { GameGuard } from "./components/game/game.guard";
import { Menu } from "./components/menu/menu";

export const routes: Routes = [
  {
    path: "",
    component: Menu
  },
  {
    path: "game/:gameCode",
    component: Game,
    canActivate: [GameGuard]
  },
  {
    path: "**",
    redirectTo: ""
  }
];
