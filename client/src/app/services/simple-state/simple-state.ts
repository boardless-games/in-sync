import { Service } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Service()
export class SimpleState {
  private readonly _state = {
    keyboardEnabled: new BehaviorSubject(false)
  };

  public readonly state: {
    [Key in keyof typeof this._state]: (typeof this._state)[Key] extends BehaviorSubject<infer T>
      ? {
          get: Observable<T>;
          set: (callback: (currentState: T) => T) => void;
        }
      : never;
  };

  constructor() {
    type StateValues = {
      [Key in keyof typeof this._state]: (typeof this._state)[Key] extends BehaviorSubject<infer T>
        ? T
        : never;
    }[keyof typeof this._state];
    this.state = Object.fromEntries(
      (Object.keys(this._state) as (keyof typeof this._state)[]).map((key) => [
        key,
        {
          get: this._state[key].asObservable(),
          set: (callback: (currentState: StateValues) => StateValues): void => {
            (this._state[key] as BehaviorSubject<StateValues>).next(
              callback(this._state[key].value)
            );
          }
        }
      ])
    ) as typeof this.state;
  }
}
