import { TestBed } from "@angular/core/testing";

import { SimpleStateService } from "./simple-state";

describe("SimpleStateService", () => {
  let service: SimpleStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SimpleStateService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
