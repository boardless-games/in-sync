import { Component, computed, inject, input, OnDestroy, signal } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { PlayerFormComponent } from "../player-form/player-form";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";
import { GameStatus } from "../../constants/GameStatus";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { MessageDto } from "../../interfaces/dtos/MessageDto";
import { MessageTopic } from "../../constants/MessageTopic";
import { IconButton } from "../icon-button/icon-button";
import { Icon } from "../../constants/Icon";
import { AlertService } from "../../services/alert/alert";
import { environment } from "../../../environments/environment";
import { AudioService } from "../../services/audio/audio";
import { AudioFile } from "../../constants/AudioFile";
import { Router } from "@angular/router";
import { PlayerNameDto } from "../../interfaces/dtos/PlayerNameDto";
import { timestamp } from "rxjs";

@Component({
  selector: "app-game",
  imports: [ReactiveFormsModule, PlayerFormComponent, IconButton],
  templateUrl: "./game.html",
  styleUrl: "./game.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Game implements OnDestroy {
  private readonly alertService = inject(AlertService);
  private readonly inSyncApi = inject(InSyncApi);
  private readonly wsService = inject(InSyncWs);
  private readonly audioService = inject(AudioService);
  private readonly router = inject(Router);

  private static readonly LOBBY_RHYTHM_LOOP = 96000;
  private static readonly LOBBY_RHYTHMS = [
    AudioFile.LOBBY_RHYTHM_1,
    AudioFile.LOBBY_RHYTHM_2,
    AudioFile.LOBBY_RHYTHM_3,
    AudioFile.LOBBY_RHYTHM_4,
    AudioFile.LOBBY_RHYTHM_5
  ];

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
  private currentAnimationFrame = 0;

  constructor() {
    this.wsService.connected.pipe(takeUntilDestroyed()).subscribe((connected) => {
      this.connected.set(connected);
      if (connected) {
        if (this.status() === GameStatus.PLAYER_FORM) {
          this.status.set(GameStatus.LOBBY);
          this.alertService.alert("Sound on!");
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
    this.currentAnimationFrame = requestAnimationFrame(this.animationFrame);
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.currentAnimationFrame);
  }

  protected playerJoined(playerName: string) {
    this.playerName.set(playerName);
    this.wsService.connect(this.gameCode(), this.playerName());
    this.audioService.playAudioFile(AudioFile.KICK, { volume: 0 });
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

  protected leave() {
    this.wsService.disconnect();
    this.router.navigate([""]);
  }

  private previousTimeStamp?: number;
  private lobbyRhythmDelta = Game.LOBBY_RHYTHM_LOOP / 2;
  private animationFrame = (timeStamp: number) => {
    const delta = timeStamp - (this.previousTimeStamp ?? timeStamp);
    if (this.status() === GameStatus.LOBBY) {
      if (this.isAdmin()) {
        this.lobbyRhythmDelta += delta;
        if (this.lobbyRhythmDelta >= Game.LOBBY_RHYTHM_LOOP) {
          this.lobbyRhythmDelta = 0;
          const nextLobbyRhythm = Math.floor(Math.random() * Game.LOBBY_RHYTHMS.length);
          console.log(nextLobbyRhythm);
          this.audioService.playAudioFile(Game.LOBBY_RHYTHMS[nextLobbyRhythm], {
            volume: 0.5,
            loop: true,
            duration: Game.LOBBY_RHYTHM_LOOP / 2000,
            fadeIn: Game.LOBBY_RHYTHM_LOOP * 0.0001,
            fadeOut: Game.LOBBY_RHYTHM_LOOP * 0.0001
          });
        }
      }
    }
    this.previousTimeStamp = timeStamp;
    this.currentAnimationFrame = requestAnimationFrame(this.animationFrame);
  };
}
