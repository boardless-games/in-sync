import { Component, inject, input, output, signal } from "@angular/core";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import { PlayerForm } from "../../interfaces/PlayerForm";
import { Validation } from "../../services/validation/validation";
import { FormValues } from "../../types/FormValues";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import { finalize } from "rxjs";
import { Router } from "@angular/router";

@Component({
  selector: "app-player-form",
  imports: [ReactiveFormsModule],
  templateUrl: "./player-form.html",
  styleUrl: "./player-form.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class PlayerFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly validationService = inject(Validation);
  private readonly inSyncApi = inject(InSyncApi);
  private readonly router = inject(Router);

  gameCode = input("");
  submitted = output<FormValues<PlayerForm>>();

  protected readonly loading = signal(false);

  protected readonly playerForm: FormGroup<PlayerForm> = this.formBuilder.group<PlayerForm>({
    playerName: new FormControl("", {
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
      .subscribe(() => {
        this.submitted.emit(formValue);
      });
  }

  protected back() {
    this.inSyncApi.deleteOrphanGame(this.gameCode()).subscribe();
    this.router.navigate([""]);
  }
}
