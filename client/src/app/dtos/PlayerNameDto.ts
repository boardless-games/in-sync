export class PlayerNameDto {
  private readonly _playerName: string;

  constructor(playerName: string) {
    this._playerName = playerName;
    Object.freeze(this);
  }

  public get playerName() {
    return this._playerName;
  }
}
