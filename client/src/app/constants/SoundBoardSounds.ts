import { SoundBoardSound } from "../interfaces/SoundBoardSound";
import { COLOR } from "./Color";
import { SOUND } from "./Sound";
import { SoundBoard } from "./SoundBoard";

export const SoundBoardSounds: {
  [Value in (typeof SoundBoard)[keyof typeof SoundBoard]]: SoundBoardSound[][];
} = {
  ACOUSTIC: [
    [
      {
        sound: SOUND.HIHAT_ACOUSTIC,
        color: COLOR.RED,
        key: "q",
        label: "hihat"
      },
      {
        sound: SOUND.CLAP_ACOUSTIC,
        color: COLOR.ORANGE,
        key: "w",
        label: "clap"
      }
    ],
    [
      {
        sound: SOUND.BASSDRUM_ACOUSTIC,
        color: COLOR.PINK,
        key: "a",
        label: "bassdrum"
      },
      {
        sound: SOUND.SNARE_ACOUSTIC,
        color: COLOR.YELLOW,
        key: "s",
        label: "snare"
      }
    ]
  ],
  ELECTRIC: [
    [
      {
        sound: SOUND.ZAP_ELECTRIC,
        color: COLOR.GREEN,
        key: "q",
        label: "zap"
      },
      {
        sound: SOUND.CLAP_ELECTRIC,
        color: COLOR.RED,
        key: "w",
        label: "clap"
      }
    ],
    [
      {
        sound: SOUND.KICK_ELECTRIC,
        color: COLOR.BLUE,
        key: "a",
        label: "kick"
      },
      {
        sound: SOUND.SNARE_ELECTRIC,
        color: COLOR.YELLOW,
        key: "s",
        label: "snare"
      }
    ]
  ]
};
