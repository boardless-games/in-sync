import { Service } from "@angular/core";

@Service()
export class Init {
  private initialized = false;

  public isInitialized(): boolean {
    return this.initialized;
  }
}
