export class ErrorDto {
  constructor(private _error: string) {
    Object.freeze(this);
  }

  public get error() {
    return this._error;
  }
}
