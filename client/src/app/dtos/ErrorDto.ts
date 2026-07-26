export class ErrorDto {
  private readonly _error: string;

  constructor(error: string) {
    this._error = error;
    Object.freeze(this);
  }

  public get error() {
    return this._error;
  }
}
