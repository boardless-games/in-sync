import { TestBed } from "@angular/core/testing";

import { AudioService } from "./audio";

describe.skip("AudioService", () => {
  let service: AudioService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AudioService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
