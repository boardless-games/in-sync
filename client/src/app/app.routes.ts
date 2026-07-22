import { Routes } from "@angular/router";
import { Game } from "./components/game/game";
import { Menu } from "./components/menu/menu";

export const routes: Routes = [
  {
    path: "",
    component: Menu
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
