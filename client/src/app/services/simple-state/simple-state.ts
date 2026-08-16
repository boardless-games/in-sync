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
      (Object.keys(this._state) as (keyof State)[]).map((key) => {
        return [key, createPublicState(key)];
      })
    ) as typeof this.state;
  }
}

/*
import { Service } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Service()
export class SimpleState {
  private readonly _state = {
    keyboardEnabled: new BehaviorSubject(false),
    darkMode: new BehaviorSubject("")
  };

  public readonly state: {
    [Key in keyof typeof this._state]: (typeof this._state)[Key] extends BehaviorSubject<infer T>
      ? {
          get: Observable<T>;
          set: (callback: (state: T) => T) => void;
        }
      : never;
  };

  constructor() {
    const createPublicState = <K extends keyof typeof this._state>(key: K) => ({
      get: this._state[key].asObservable(),
      set: (callback: (state: (typeof this._state)[K] extends BehaviorSubject<infer T> ? T : never) => ((typeof this._state)[K] extends BehaviorSubject<infer T> ? T : never) => {
        this._state[key].next(callback(this._state[key].value));
      }
    });
    this.state = Object.fromEntries(
      (Object.keys(this._state) as (keyof typeof this._state)[]).map((key) => {
        return [key, createPublicState(key)];
      })
    ) as typeof this.state;
  }
}


*/
