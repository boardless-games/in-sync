export enum SongType {
  RANDOM = "RANDOM"
}

export const SongTypeDescriptions: {
  [Value in (typeof SongType)[keyof typeof SongType]]: string;
} = {
  RANDOM: "A randomly generated rhythm."
};
