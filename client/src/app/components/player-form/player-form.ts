import { Component, inject, input, output, signal } from "@angular/core";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import { PlayerForm } from "../../interfaces/PlayerForm";
import { ValidationService } from "../../services/validation/validation";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { finalize } from "rxjs";
import { Router } from "@angular/router";
import { PlayerNameDto } from "../../interfaces/dtos/PlayerNameDto";

@Component({
  selector: "app-player-form",
  imports: [ReactiveFormsModule],
  templateUrl: "./player-form.html",
  styleUrl: "./player-form.css",
  host: {
    class: "flex-column"
  }
})
export class PlayerFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly validationService = inject(ValidationService);
  private readonly inSyncApi = inject(InSyncApi);
  private readonly router = inject(Router);

  private static readonly PLAYER_NAME = "playerName";

  gameCode = input("");
  submitted = output<string>();

  protected readonly loading = signal(false);

  protected readonly playerForm: FormGroup<PlayerForm> = this.formBuilder.group<PlayerForm>({
    playerName: new FormControl(localStorage.getItem(PlayerFormComponent.PLAYER_NAME) || "", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(this.validationService.playerNameRegex)]
    })
  });

  protected continue() {
    if (this.loading() || this.playerForm.invalid) {
      return;
    }
    this.loading.set(true);

    const formValue = this.playerForm.getRawValue();
    this.inSyncApi
      .newPlayer(this.gameCode(), formValue)
      .pipe(
        finalize(() => {
          this.loading.set(false);
        })
      )
      .subscribe((playerName: PlayerNameDto) => {
        localStorage.setItem(PlayerFormComponent.PLAYER_NAME, playerName.playerName);
        this.submitted.emit(playerName.playerName);
      });
  }

  protected back() {
    this.inSyncApi.deleteOrphanGame(this.gameCode()).subscribe();
    this.router.navigate([""]);
  }
}
