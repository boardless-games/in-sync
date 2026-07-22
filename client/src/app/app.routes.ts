import { Routes } from "@angular/router";
import { Game } from "./components/game/game";
import { Menu } from "./components/menu/menu";
import { BgSplash } from "./components/bg-splash/bg-splash";
import { InitGuard } from "./services/init/init.guard";

export const routes: Routes = [
  {
    path: "",
    component: Menu,
    canActivate: [InitGuard]
  },
  {
    path: "init",
    component: BgSplash
  },
  {
    path: "game/:gameCode",
    component: Game
  },
  {
    path: "**",
    redirectTo: ""
  }
];
