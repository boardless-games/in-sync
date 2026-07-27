import { Component, inject, input, signal } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { PlayerFormComponent } from "../player-form/player-form";
import { FormValues } from "../../types/FormValues";
import { PlayerForm } from "../../interfaces/PlayerForm";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";

@Component({
  selector: "app-game",
  imports: [ReactiveFormsModule, PlayerFormComponent],
  templateUrl: "./game.html",
  styleUrl: "./game.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Game {
  private readonly inSyncApi = inject(InSyncApi);
  private readonly wsService = inject(InSyncWs);

  gameCode = input("");

  protected readonly playerName = signal("");

  protected playerFormSubmitted(playerForm: FormValues<PlayerForm>) {
    this.playerName.set(playerForm.playerName);
    this.wsService.connect(this.gameCode(), this.playerName());
  }
}
