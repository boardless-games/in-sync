import { Service } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { SetStateCallback } from "../../types/SetStateCallback";
import { State } from "../../types/State";

@Service()
export class SimpleState {
  private readonly _state: {
    [Key in keyof State]: BehaviorSubject<State[Key]>;
  } = {
    keyboardEnabled: new BehaviorSubject(false),
    darkMode: new BehaviorSubject("")
  };

  public readonly state: {
    [Key in keyof State]: {
      get: Observable<State[Key]>;
      set: (callback: SetStateCallback<Key>) => void;
    };
  };

  constructor() {
    const partialState: Partial<typeof this.state> = {};
    (Object.keys(this._state) as Array<keyof State>).forEach((key) => {
      partialState[key] = {
        get: this._state[key].asObservable(),
        set: (callback: SetStateCallback<typeof key>) => {
          this._state[key].next(callback(this._state[key].value));
        }
      };
    });
    this.state = partialState as typeof this.state;
  }
}
