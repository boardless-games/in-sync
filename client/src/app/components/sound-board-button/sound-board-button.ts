import { Component, computed, ElementRef, inject, input, output, ViewChild } from "@angular/core";
import { SOUND } from "../../constants/Sound";
import { AudioService } from "../../services/audio/audio";
import { KeyPress } from "../../services/key-press/key-press";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { COLOR } from "../../constants/Color";
import { SoundBoardSound } from "../../interfaces/SoundBoardSound";

@Component({
  selector: "app-sound-board-button",
  imports: [],
  templateUrl: "./sound-board-button.html",
  styleUrl: "./sound-board-button.css",
  host: {
    "[style.--button-color]": "color()",
    "[style.--button-size]": "size()",
    "class": "flex-column"
  }
})
export class SoundBoardButton {
  private readonly audioService = inject(AudioService);
  private readonly keyPress = inject(KeyPress);

  sound = input<SoundBoardSound>();

  protected readonly color = computed(() => this.sound()?.color ?? COLOR.BLUE);
  protected readonly size = computed(() => this.sound()?.size ?? "100px");
  protected readonly label = computed(() => this.sound()?.label ?? "");
  protected readonly key = computed(() => this.sound()?.key ?? "");
  protected readonly played = output<void>();

  @ViewChild("button") protected button?: ElementRef<HTMLButtonElement>;

  constructor() {
    this.keyPress.keyPressed.pipe(takeUntilDestroyed()).subscribe((event: KeyboardEvent) => {
      if (this.sound()?.key === event.key) {
        this.play();
      }
    });
  }

  protected play() {
    const currentSound = this.sound();
    if (!currentSound) return;
    this.audioService.playAudioFile(currentSound.sound, { volume: currentSound.volume });
    this.button?.nativeElement?.classList.remove("beat");
    void this.button?.nativeElement?.offsetWidth;
    this.button?.nativeElement?.classList.add("beat");
    this.played.emit();
  }
}
