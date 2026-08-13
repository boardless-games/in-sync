import { FormControl } from "@angular/forms";
import { SoundBoard } from "../constants/SoundBoard";

export interface LobbySoundBoardSettingsForm {
  soundBoard: FormControl<SoundBoard>;
}
