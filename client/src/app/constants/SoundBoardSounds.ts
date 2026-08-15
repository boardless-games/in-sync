import { SoundBoardSound } from "../interfaces/SoundBoardSound";
import { COLOR } from "./Color";
import { SOUND } from "./Sound";
import { SoundBoard } from "./SoundBoard";

export const SoundBoardSounds: {
  [Value in (typeof SoundBoard)[keyof typeof SoundBoard]]: SoundBoardSound[][];
} = {
  LOBBY: [
    [
      {
        sound: SOUND.HIHAT_CLOSED,
        color: COLOR.BLUE,
        key: "q",
        label: "hihat"
      },
      {
        sound: SOUND.CLAP,
        color: COLOR.GREEN,
        key: "w",
        label: "clap"
      }
    ],
    [
      {
        sound: SOUND.SNARE_ACOUSTIC,
        color: COLOR.RED,
        key: "a",
        label: "snare 1"
      },
      {
        sound: SOUND.SNARE_ELECTRIC,
        color: COLOR.PINK,
        key: "s",
        label: "snare 2"
      }
    ],
    [
      {
        sound: SOUND.KICK,
        color: COLOR.YELLOW,
        key: "z",
        label: "kick"
      },
      {
        sound: SOUND.BASSDRUM,
        color: COLOR.ORANGE,
        key: "x",
        label: "bassdrum"
      }
    ]
  ],
  ACOUSTIC: [
    [
      {
        sound: SOUND.HIHAT_CLOSED,
        color: COLOR.RED,
        key: "q",
        label: "hihat 1"
      },
      {
        sound: SOUND.HIHAT_OPENED,
        color: COLOR.ORANGE,
        key: "w",
        label: "hihat 2"
      }
    ],
    [
      {
        sound: SOUND.BASSDRUM,
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
        sound: SOUND.HIHAT_CLOSED,
        color: COLOR.GREEN,
        key: "q",
        label: "hihat"
      },
      {
        sound: SOUND.CLAP,
        color: COLOR.RED,
        key: "w",
        label: "clap"
      }
    ],
    [
      {
        sound: SOUND.KICK,
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
