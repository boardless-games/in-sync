import { TestBed } from "@angular/core/testing";

import { Init } from "./init";

describe("Init", () => {
  let service: Init;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Init);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
