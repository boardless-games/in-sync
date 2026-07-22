import { Component, DOCUMENT, inject } from "@angular/core";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { AudioService } from "../../services/audio/audio";
import { JoinGameForm } from "../../models/JoinGameForm";

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
  private readonly document = inject(DOCUMENT);
  private readonly formBuilder = inject(FormBuilder);
  protected readonly audioService = inject(AudioService);
  protected readonly joinGameForm = this.formBuilder.group<JoinGameForm>({
    gameCode: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9]{6}$/)]
    })
  });

  protected joinGame() {
    console.log(this.joinGameForm.getRawValue());
  }

  protected boardlessGames() {
    this.document.location.assign("https://boardless.games");
  }
}
