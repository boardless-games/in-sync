import { SongType } from "./SongType";

export const SongTypeDescriptions: {
  [Value in (typeof SongType)[keyof typeof SongType]]: string;
} = {
  RANDOM: "A randomly generated rhythm."
};
