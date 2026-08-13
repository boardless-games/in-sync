import { TestBed } from "@angular/core/testing";

import { KeyPress } from "./key-press";

describe("KeyPress", () => {
  let service: KeyPress;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KeyPress);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
