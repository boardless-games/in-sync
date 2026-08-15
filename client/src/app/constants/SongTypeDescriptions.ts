import { SongType } from "./SongType";

export const SongTypeDescriptions: Record<(typeof SongType)[keyof typeof SongType], string> = {
  RANDOM: "A randomly generated rhythm."
};
