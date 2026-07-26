import { Service } from "@angular/core";

@Service()
export class Validation {
  private gameCodeRegex = new RegExp(/^[0-9]{6}$/);

  public validateGameCode(gameCode: string): boolean {
    return this.gameCodeRegex.test(gameCode);
  }
}
