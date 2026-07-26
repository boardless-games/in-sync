import { Component, inject, input, signal } from "@angular/core";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";

@Component({
  selector: "app-game",
  imports: [],
  templateUrl: "./game.html",
  styleUrl: "./game.css"
})
export class Game {
  private readonly inSyncApi = inject(InSyncApi);

  gameCode = input("");

  protected readonly playerName = signal("");
}
