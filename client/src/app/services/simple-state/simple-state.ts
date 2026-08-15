import { Service } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { State } from "../../interfaces/State";

@Service()
export class SimpleState {
  private readonly _state: {
    [Key in keyof State]: BehaviorSubject<State[Key]>;
  } = {
    keyboardEnabled: new BehaviorSubject(false)
  };

  public readonly state: {
    [Key in keyof State]: {
      get: Observable<State[Key]>;
      set: (callback: (state: State[Key]) => State[Key]) => void;
    };
  };

  constructor() {
    const createPublicState = <K extends keyof State>(key: K) => ({
      get: this._state[key].asObservable(),
      set: (callback: (state: State[K]) => State[K]) => {
        this._state[key].next(callback(this._state[key].value));
      }
    });
    this.state = Object.fromEntries(
      (Object.keys(this._state) as Array<keyof State>).map((key) => {
        return [key, createPublicState(key)];
      })
    ) as typeof this.state;
  }
}
