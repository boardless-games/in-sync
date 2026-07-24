import { Component, inject, signal } from "@angular/core";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { finalize } from "rxjs";
import { GameCodeDto } from "../../dtos/GameCodeDto";
import { JoinGameForm } from "../../models/JoinGameForm";
import { Alert } from "../../services/alert/alert";
import { AudioService } from "../../services/audio/audio";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";

@Component({
  selector: "app-menu",
  imports: [ReactiveFormsModule],
  templateUrl: "./menu.html",
  styleUrl: "./menu.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Menu {
  private readonly inSyncApi = inject(InSyncApi);
  private readonly alertService = inject(Alert);
  private readonly formBuilder = inject(FormBuilder);
  protected readonly audioService = inject(AudioService);
  protected readonly joinGameForm = this.formBuilder.group<JoinGameForm>({
    gameCode: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9]{6}$/)]
    })
  });
  protected readonly loading = signal(false);

  protected newGame() {
    if (this.loading()) {
      return;
    }
    this.loading.set(true);
    this.inSyncApi
      .newGame()
      .pipe(
        finalize(() => {
          this.loading.set(false);
        })
      )
      .subscribe((response: GameCodeDto) => {
        console.log(response.gameCode);
      });
  }

  protected joinGame() {
    if (this.loading()) {
      return;
    }
    this.loading.set(true);
  }
}
