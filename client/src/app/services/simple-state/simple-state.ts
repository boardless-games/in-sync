import { Service } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { SetStateCallback } from "../../types/SetStateCallback";
import { State } from "../../types/State";

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
      set: (callback: SetStateCallback<Key>) => void;
    };
  };

  constructor() {
    this.state = Object.keys(this._state).map((key) => {});
    const keys = Object.keys<State>(this._state);
    for (const key in this._state) {
      publicState[key as keyof State] = {
        get: this.getStateGetter(key),
        set: this.getStateSetter(key)
      };
    }
    this.state = {
      keyboardEnabled: {
        get: this.getStateGetter("keyboardEnabled"),
        set: this.getStateSetter("keyboardEnabled")
      }
    };
  }

  private getStateGetter<K extends keyof State>(key: K): Observable<State[K]> {
    return this._state[key].asObservable();
  }

  private getStateSetter<K extends keyof State>(key: K) {
    return (callback: SetStateCallback<K>) => {
      this._state[key].next(callback(this._state[key].value));
    };
  }
}
