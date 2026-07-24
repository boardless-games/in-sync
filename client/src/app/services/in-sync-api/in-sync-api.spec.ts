import { TestBed } from "@angular/core/testing";

import { InSyncApi } from "./in-sync-api";

describe("InSync", () => {
  let service: InSyncApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InSyncApi);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
