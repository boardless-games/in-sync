import { TestBed } from "@angular/core/testing";

import { SimpleState } from "./simple-state";

describe("SimpleState", () => {
  let service: SimpleState;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SimpleState);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
