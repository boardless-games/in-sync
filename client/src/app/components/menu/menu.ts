import { Component, inject, signal } from "@angular/core";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { finalize, from, switchMap } from "rxjs";
import { GameCodeDto } from "../../interfaces/dtos/GameCodeDto";
import { JoinGameForm } from "../../interfaces/JoinGameForm";
import { AudioService } from "../../services/audio/audio";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { Validation } from "../../services/validation/validation";

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
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly validationService = inject(Validation);
  protected readonly audioService = inject(AudioService);
  protected readonly joinGameForm = this.formBuilder.group<JoinGameForm>({
    gameCode: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(this.validationService.gameCodeRegex)]
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
        switchMap((response: GameCodeDto) =>
          from(this.router.navigate(["game", response.gameCode]))
        ),
        finalize(() => {
          this.loading.set(false);
        })
      )
      .subscribe();
  }

  protected joinGame() {
    if (this.loading() || this.joinGameForm.invalid) {
      return;
    }
    this.loading.set(true);
    this.router.navigate(["game", this.joinGameForm.controls.gameCode.value]).finally(() => {
      this.loading.set(false);
    });
  }
}
