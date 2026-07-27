import { TestBed } from "@angular/core/testing";

import { InSyncWs } from "./in-sync-ws";

describe("InSyncWs", () => {
  let service: InSyncWs;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InSyncWs);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
