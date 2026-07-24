export class GameCodeDto {
  constructor(private _gameCode: string) {
    Object.freeze(this);
  }

  public get gameCode() {
    return this._gameCode;
  }
}
