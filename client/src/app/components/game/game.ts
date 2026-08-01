import { Component, computed, inject, input, signal } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { PlayerFormComponent } from "../player-form/player-form";
import { FormValues } from "../../types/FormValues";
import { PlayerForm } from "../../interfaces/PlayerForm";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";
import { GameStatus } from "../../constants/GameStatus";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { MessageDto } from "../../interfaces/dtos/MessageDto";
import { MessageTopic } from "../../constants/MessageTopic";
import { IconButton } from "../icon-button/icon-button";
import { Icon } from "../../constants/Icon";
import { Alert } from "../../services/alert/alert";
import { environment } from "../../../environments/environment";

@Component({
  selector: "app-game",
  imports: [ReactiveFormsModule, PlayerFormComponent, IconButton],
  templateUrl: "./game.html",
  styleUrl: "./game.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Game {
  private readonly alertService = inject(Alert);
  private readonly inSyncApi = inject(InSyncApi);
  private readonly wsService = inject(InSyncWs);

  gameCode = input("");

  protected readonly GameStatus = GameStatus;
  protected readonly Icon = Icon;
  protected readonly status = signal(GameStatus.PLAYER_FORM);
  protected readonly playerName = signal("");
  protected readonly connected = signal(false);
  protected readonly players = signal<string[]>([]);
  protected readonly isAdmin = computed(() => {
    const currentPlayers = this.players();
    return currentPlayers.length > 0 && currentPlayers[0] === this.playerName();
  });

  constructor() {
    this.wsService.connected.pipe(takeUntilDestroyed()).subscribe((connected) => {
      this.connected.set(connected);
      if (this.status() === GameStatus.PLAYER_FORM && connected) {
        this.status.set(GameStatus.LOBBY);
      }
    });
    this.wsService.messaged.pipe(takeUntilDestroyed()).subscribe((message: MessageDto) => {
      console.log("New message: ", message);
      if (message.topic === MessageTopic.LOBBY) {
        this.players.set(message.data as string[]);
      }
    });
  }

  protected playerFormSubmitted(playerForm: FormValues<PlayerForm>) {
    this.playerName.set(playerForm.playerName);
    this.wsService.connect(this.gameCode(), this.playerName());
  }

  protected shareGameCode() {
    navigator.clipboard
      .writeText(`${environment.clientUrl}/InSync/game/${this.gameCode()}`)
      .then(() => {
        this.alertService.alert("Copied game link.");
      })
      .catch(() => {
        this.alertService.alert("Failed to copy game link.");
      });
  }
}
