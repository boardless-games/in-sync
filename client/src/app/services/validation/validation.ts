import { Service } from "@angular/core";

@Service()
export class Validation {
  public gameCodeRegex = new RegExp(/^[0-9]{6}$/);
  public playerNameRegex = new RegExp(/^[a-zA-Z0-9]{2,15}$/);

  public validateGameCode(gameCode: string): boolean {
    return this.gameCodeRegex.test(gameCode);
  }

  public validatePlayerName(playerName: string): boolean {
    return this.playerNameRegex.test(playerName);
  }
}
