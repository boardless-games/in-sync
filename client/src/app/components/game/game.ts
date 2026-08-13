import { Component, computed, inject, input, signal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { ReactiveFormsModule } from "@angular/forms";
import { SOUND } from "../../constants/Sound";
import { GameStatus } from "../../constants/GameStatus";
import { Icon } from "../../constants/Icon";
import { MessageTopic } from "../../constants/MessageTopic";
import { MessageDto } from "../../interfaces/dtos/MessageDto";
import { AlertService } from "../../services/alert/alert";
import { AudioService } from "../../services/audio/audio";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";
import { Lobby } from "../lobby/lobby";
import { PlayerFormComponent } from "../player-form/player-form";
import { Router } from "@angular/router";

@Component({
  selector: "app-game",
  imports: [ReactiveFormsModule, PlayerFormComponent, Lobby],
  templateUrl: "./game.html",
  styleUrl: "./game.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Game {
  private readonly alertService = inject(AlertService);
  private readonly wsService = inject(InSyncWs);
  private readonly audioService = inject(AudioService);
  private readonly router = inject(Router);

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
      if (connected) {
        if (this.status() === GameStatus.PLAYER_FORM) {
          this.status.set(GameStatus.LOBBY);
          this.alertService.alert("Turn volume up!");
        }
      } else {
      }
    });
    this.wsService.messaged.pipe(takeUntilDestroyed()).subscribe((message: MessageDto) => {
      console.log("New message: ", message);
      if (message.topic === MessageTopic.LOBBY) {
        this.players.set(message.data as string[]);
      }
    });
  }

  protected playerJoined(playerName: string) {
    this.playerName.set(playerName);
    this.wsService.connect(this.gameCode(), this.playerName());
    this.audioService.playAudioFile(SOUND.BASSDRUM_ACOUSTIC, { volume: 0 });
  }

  protected leave() {
    this.wsService.disconnect();
    this.router.navigate([""]);
  }

  protected start() {}
}
