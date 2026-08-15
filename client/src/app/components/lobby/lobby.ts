import { Component, inject, input, output, Signal, signal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormBuilder, FormControl, ReactiveFormsModule } from "@angular/forms";
import { debounceTime } from "rxjs";
import { environment } from "../../../environments/environment";
import { Icon } from "../../constants/Icon";
import { SongDuration } from "../../constants/SongDuration";
import { SongTempo } from "../../constants/SongTempo";
import { SongType } from "../../constants/SongType";
import { SongTypeDescriptions } from "../../constants/SongTypeDescriptions";
import { SOUND } from "../../constants/Sound";
import { SoundBoardSounds } from "../../constants/SoundBoardSounds";
import { MessageDto } from "../../interfaces/dtos/MessageDto";
import { GameSettingsForm } from "../../interfaces/GameSettingsForm";
import { LobbySettingsForm } from "../../interfaces/LobbySettingsForm";
import { SoundBoardSound } from "../../interfaces/SoundBoardSound";
import { AlertService } from "../../services/alert/alert";
import { AudioService } from "../../services/audio/audio";
import { InSyncWs } from "../../services/in-sync-ws/in-sync-ws";
import { Option } from "../../types/Option";
import { IconButton } from "../icon-button/icon-button";
import { SoundBoardButton } from "../sound-board-button/sound-board-button";
import { PlayerSettingsForm } from "../../interfaces/PlayerSettingsForm";

@Component({
  selector: "app-lobby",
  imports: [IconButton, ReactiveFormsModule, SoundBoardButton],
  templateUrl: "./lobby.html",
  styleUrl: "./lobby.css",
  host: {
    class: "flex-column"
  }
})
export class Lobby {
  private static readonly LOBBY_SETTINGS_FORM = "lobbySettings";
  private static readonly GAME_SETTINGS_FORM = "gameSettings";
  private static readonly PLAYER_SETTINGS_FORM = "playerSettings";
  private static readonly LOBBY_RHYTHM_LOOP = 96000;
  private static readonly LOBBY_RHYTHM_VOLUME = 0.5;
  private static readonly LOBBY_RHYTHM_FADE = Lobby.LOBBY_RHYTHM_LOOP * 0.0001;
  private static readonly LOBBY_RHYTHM_DURATION = Lobby.LOBBY_RHYTHM_LOOP / 2000;
  private static readonly LOBBY_RHYTHMS = [
    SOUND.LOBBY_RHYTHM_1,
    SOUND.LOBBY_RHYTHM_2,
    SOUND.LOBBY_RHYTHM_3,
    SOUND.LOBBY_RHYTHM_4,
    SOUND.LOBBY_RHYTHM_5
  ];
  private static readonly SOUND_BOARD_SAYINGS = [
    "Rock on!",
    "Nice!",
    "Groovy Mama!",
    "Let's dance!",
    "That's it!",
    "Now you're getting the hang of it!",
    "Keep going!",
    "Don't stop now!",
    "You call that a beat?",
    "C'mon now!",
    "Let's get it!",
    "DJ drums over here!",
    "I could get used to this!",
    "I hear that!",
    "Add a little something more!",
    "Get crazy now!",
    "This all you got?",
    "I'd call this a warmup!",
    "You make it look easy!",
    "Maybe this isn't for you?"
  ];

  private readonly alertService = inject(AlertService);
  private readonly wsService = inject(InSyncWs);
  private readonly audioService = inject(AudioService);
  private readonly formBuilder = inject(FormBuilder);

  gameCode = input("");
  isAdmin = input<boolean>(false);
  players = input<string[]>([]);
  started = output<void>();
  left = output<void>();

  private currentAnimationFrame = 0;
  private currentLobbyRhythm?: AudioBufferSourceNode;
  protected readonly Icon = Icon;
  protected readonly lobbySoundBoardSounds: Signal<SoundBoardSound[][]> = signal(
    SoundBoardSounds.LOBBY
  );
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
  protected readonly showSettings = signal(false);
  protected readonly playerSettingsForm;
  protected readonly lobbySettingsForm;
  protected readonly gameSettingsForm;

  constructor() {
    this.wsService.messaged.pipe(takeUntilDestroyed()).subscribe((message: MessageDto) => {
      console.log("New message: ", message);
    });

    const storedPlayerSettings = localStorage.getItem(Lobby.PLAYER_SETTINGS_FORM);
    const initialPlayerSettings: Partial<PlayerSettingsForm> = storedPlayerSettings
      ? JSON.parse(storedPlayerSettings)
      : {};
    this.playerSettingsForm = this.formBuilder.group<PlayerSettingsForm>({
      enableKeyboard: new FormControl(initialPlayerSettings?.enableKeyboard ?? false, {
        nonNullable: true
      })
    });
    this.playerSettingsForm.valueChanges
      .pipe(takeUntilDestroyed(), debounceTime(100))
      .subscribe(() => {
        localStorage.setItem(
          Lobby.PLAYER_SETTINGS_FORM,
          JSON.stringify(this.playerSettingsForm.getRawValue())
        );
      });

    const storedLobbySettings = localStorage.getItem(Lobby.LOBBY_SETTINGS_FORM);
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
          Lobby.LOBBY_SETTINGS_FORM,
          JSON.stringify(this.lobbySettingsForm.getRawValue())
        );
      });

    const storedGameSettings = localStorage.getItem(Lobby.GAME_SETTINGS_FORM);
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
      .subscribe(() => {
        localStorage.setItem(
          Lobby.GAME_SETTINGS_FORM,
          JSON.stringify(this.gameSettingsForm.getRawValue())
        );
      });
    this.currentAnimationFrame = requestAnimationFrame(this.animationFrame);
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.currentAnimationFrame);
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
    this.left.emit();
  }

  protected start() {
    this.started.emit();
  }

  protected toggleSettings() {
    this.showSettings.update((current) => !current);
  }

  protected soundBoardPlayed() {
    if (Math.random() <= 0.05) {
      const randomSaying = Math.floor(Math.random() * Lobby.SOUND_BOARD_SAYINGS.length);
      this.alertService.alert(Lobby.SOUND_BOARD_SAYINGS[randomSaying], 1000);
    }
  }

  private getEnumOptions(anyEnum: Record<string | number, string | number>): Option[] {
    return (Object.keys(anyEnum) as (keyof typeof anyEnum)[])
      .filter((key) => isNaN(Number(key)))
      .map((key) => ({
        value: anyEnum[key],
        text: key as string
      }));
  }

  private previousTimeStamp?: number;
  private lobbyRhythmDelta = Lobby.LOBBY_RHYTHM_LOOP / 2;
  private animationFrame = (timeStamp: number) => {
    const delta = timeStamp - (this.previousTimeStamp ?? timeStamp);
    if (this.isAdmin() && this.lobbySettingsForm.controls.playLobbyRhythms.value) {
      this.lobbyRhythmDelta += delta;
      if (this.lobbyRhythmDelta >= Lobby.LOBBY_RHYTHM_LOOP) {
        this.lobbyRhythmDelta = 0;
        const nextLobbyRhythm = Math.floor(Math.random() * Lobby.LOBBY_RHYTHMS.length);
        this.audioService
          .playAudioFile(Lobby.LOBBY_RHYTHMS[nextLobbyRhythm], {
            volume: Lobby.LOBBY_RHYTHM_VOLUME,
            loop: true,
            duration: Lobby.LOBBY_RHYTHM_DURATION,
            fadeIn: Lobby.LOBBY_RHYTHM_FADE,
            fadeOut: Lobby.LOBBY_RHYTHM_FADE
          })
          .then((result) => {
            this.currentLobbyRhythm = result;
          });
      }
    }
    this.previousTimeStamp = timeStamp;
    this.currentAnimationFrame = requestAnimationFrame(this.animationFrame);
  };
}
