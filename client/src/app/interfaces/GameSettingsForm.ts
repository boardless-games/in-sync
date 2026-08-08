import { FormControl } from "@angular/forms";
import { SongType } from "../constants/SongType";
import { SongTempo } from "../constants/SongTempo";
import { SongDuration } from "../constants/SongDuration";

export interface GameSettingsForm {
  songType: FormControl<SongType>;
  songTempo: FormControl<SongTempo>;
  songDuration: FormControl<SongDuration>;
  randomPlayerOrder: FormControl<boolean>;
}
