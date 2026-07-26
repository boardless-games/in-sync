import { Component, inject, input, signal } from "@angular/core";
import { InSyncApi } from "../../services/in-sync-api/in-sync-api";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import { PlayerNameForm } from "../../models/PlayerNameForm";

@Component({
  selector: "app-game",
  imports: [ReactiveFormsModule],
  templateUrl: "./game.html",
  styleUrl: "./game.css",
  host: {
    class: "full-size flex-column overflow"
  }
})
export class Game {
  private readonly inSyncApi = inject(InSyncApi);
  private readonly formBuilder = inject(FormBuilder);
  protected readonly playerNameForm: FormGroup<PlayerNameForm> =
    this.formBuilder.group<PlayerNameForm>({
      playerName: new FormControl("", {
        nonNullable: true,
        validators: Validators.required
      })
    });

  gameCode = input("");

  protected readonly playerName = signal("");
}
