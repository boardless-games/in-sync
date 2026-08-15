import { State } from "./State";

export type SetStateCallback<K extends keyof State> = (state: State[K]) => State[K];
