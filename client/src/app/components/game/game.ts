import {
  Component,
  computed,
  inject,
  input,
  OnDestroy,
  Signal,
  signal,
  WritableSignal
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormBuilder, FormControl, ReactiveFormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { debounceTime } from "rxjs";
import { environment } from "../../../environments/environment";
import { AudioFile } from "../../constants/AudioFile";
import { GameStatus } from "../../constants/GameStatus";
import { Icon } from "../../constants/Icon";
import { MessageTopic } from "../../constants/MessageTopic";
import { MessageDto } from "../../interfaces/dtos/MessageDto";
import { GameSettingsForm } from "../../interfaces/GameSettingsForm";
import { AlertService } from "../../services/alert/alert";
import { AudioService } from "../../services/audio/audio";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";
import { IconButton } from "../icon-button/icon-button";
import { PlayerFormComponent } from "../player-form/player-form";
import { SongType, SongTypeDescriptions } from "../../constants/SongType";
import { SongTempo } from "../../constants/SongTempo";
import { SongDuration } from "../../constants/SongDuration";
import { LobbySettingsForm } from "../../interfaces/LobbySettingsForm";
import { Option } from "../../types/Option";
import { NgClass } from "../../../../node_modules/@angular/common/types/_common_module-chunk";

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
  private readonly formBuilder = inject(FormBuilder);

  private static readonly LOBBY_SETTINGS_FORM = "lobbySettings";
  private static readonly GAME_SETTINGS_FORM = "gameSettings";
  private static readonly LOBBY_RHYTHM_LOOP = 96000;
  private static readonly LOBBY_RHYTHMS = [
    AudioFile.LOBBY_RHYTHM_1,
    AudioFile.LOBBY_RHYTHM_2,
    AudioFile.LOBBY_RHYTHM_3,
    AudioFile.LOBBY_RHYTHM_4,
    AudioFile.LOBBY_RHYTHM_5
  ];

  gameCode = input("");

  private currentAnimationFrame = 0;
  private currentLobbyRhythm?: AudioBufferSourceNode;
  protected readonly GameStatus = GameStatus;
  protected readonly Icon = Icon;
  protected readonly songTypeOptions: Signal<Option[]> = signal(
    this.getEnumOptions(SongType).map((option) => ({
      ...option,
      text: `${option.text}: ${SongTypeDescriptions[option.value as SongType]}`
    }))
  );
  protected readonly songTempoOptions: Signal<Option[]> = signal(this.getEnumOptions(SongTempo));
  protected readonly songDurationOptions: Signal<Option[]> = signal(
    this.getEnumOptions(SongDuration)
  );
  protected readonly status = signal(GameStatus.PLAYER_FORM);
  protected readonly playerName = signal("");
  protected readonly connected = signal(false);
  protected readonly players = signal<string[]>([]);
  protected readonly isAdmin = computed(() => {
    const currentPlayers = this.players();
    return currentPlayers.length > 0 && currentPlayers[0] === this.playerName();
  });
  protected readonly showSettings = signal(false);
  protected readonly lobbySettingsForm;
  protected readonly gameSettingsForm;

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

    const storedLobbySettings = localStorage.getItem(Game.LOBBY_SETTINGS_FORM);
    const initialLobbySettings: Partial<LobbySettingsForm> = storedLobbySettings
      ? JSON.parse(storedLobbySettings)
      : {};
    this.lobbySettingsForm = this.formBuilder.group<LobbySettingsForm>({
      playLobbyRhythms: new FormControl(initialLobbySettings?.playLobbyRhythms ?? true, {
        nonNullable: true
      })
    });
    this.lobbySettingsForm.valueChanges
      .pipe(takeUntilDestroyed(), debounceTime(100))
      .subscribe((changes) => {
        if (changes.playLobbyRhythms === false && this.currentLobbyRhythm) {
          this.audioService.stop(this.currentLobbyRhythm);
        }
        localStorage.setItem(
          Game.LOBBY_SETTINGS_FORM,
          JSON.stringify(this.lobbySettingsForm.getRawValue())
        );
      });

    const storedGameSettings = localStorage.getItem(Game.GAME_SETTINGS_FORM);
    const initialGameSettings: Partial<GameSettingsForm> = storedGameSettings
      ? JSON.parse(storedGameSettings)
      : {};
    this.gameSettingsForm = this.formBuilder.group<GameSettingsForm>({
      songType: new FormControl(initialGameSettings?.songType ?? SongType.RANDOM, {
        nonNullable: true
      }),
      songTempo: new FormControl(initialGameSettings?.songTempo ?? SongTempo.LARGO, {
        nonNullable: true
      }),
      songDuration: new FormControl(initialGameSettings?.songDuration ?? SongDuration.X_SHORT, {
        nonNullable: true
      }),
      randomPlayerOrder: new FormControl(initialGameSettings?.randomPlayerOrder ?? false, {
        nonNullable: true
      })
    });
    this.gameSettingsForm.valueChanges
      .pipe(takeUntilDestroyed(), debounceTime(100))
      .subscribe((changes) => {
        localStorage.setItem(
          Game.GAME_SETTINGS_FORM,
          JSON.stringify(this.gameSettingsForm.getRawValue())
        );
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
    if (!navigator?.clipboard?.writeText) {
      this.alertService.alert("Clipboard not supported on this device.");
      return;
    }
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

  protected start() {}

  protected toggleSettings() {
    this.showSettings.update((current) => !current);
  }

  private previousTimeStamp?: number;
  private lobbyRhythmDelta = Game.LOBBY_RHYTHM_LOOP / 2;
  private animationFrame = (timeStamp: number) => {
    const delta = timeStamp - (this.previousTimeStamp ?? timeStamp);
    if (this.status() === GameStatus.LOBBY) {
      if (this.isAdmin() && this.lobbySettingsForm.controls.playLobbyRhythms.value) {
        this.lobbyRhythmDelta += delta;
        if (this.lobbyRhythmDelta >= Game.LOBBY_RHYTHM_LOOP) {
          this.lobbyRhythmDelta = 0;
          const nextLobbyRhythm = Math.floor(Math.random() * Game.LOBBY_RHYTHMS.length);
          this.audioService
            .playAudioFile(Game.LOBBY_RHYTHMS[nextLobbyRhythm], {
              volume: 0.2,
              loop: true,
              duration: Game.LOBBY_RHYTHM_LOOP / 2000,
              fadeIn: Game.LOBBY_RHYTHM_LOOP * 0.0001,
              fadeOut: Game.LOBBY_RHYTHM_LOOP * 0.0001
            })
            .then((result) => {
              this.currentLobbyRhythm = result;
            });
        }
      }
    }
    this.previousTimeStamp = timeStamp;
    this.currentAnimationFrame = requestAnimationFrame(this.animationFrame);
  };

  private getEnumOptions(anyEnum: Record<string | number, string | number>): Option[] {
    return (Object.keys(anyEnum) as (keyof typeof anyEnum)[])
      .filter((key) => isNaN(Number(key)))
      .map((key) => ({
        value: anyEnum[key],
        text: key as string
      }));
  }
}
