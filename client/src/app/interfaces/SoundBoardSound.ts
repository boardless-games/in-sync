import { COLOR } from "../constants/Color";
import { SOUND } from "../constants/Sound";

export interface SoundBoardSound {
  sound: SOUND;
  volume?: number;
  color?: COLOR;
  key?: string;
  label?: string;
  size?: string;
}
