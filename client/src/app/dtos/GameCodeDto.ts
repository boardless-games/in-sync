export class GameCodeDto {
  private readonly _gameCode: string;

  constructor(gameCode: string) {
    this._gameCode = gameCode;
    Object.freeze(this);
  }

  public get gameCode() {
    return this._gameCode;
  }
}
