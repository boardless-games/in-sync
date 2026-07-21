import { TestBed } from "@angular/core/testing";

import { AudioService } from "./audio";
import { Mock } from "vitest";
import { AudioFile } from "../../constants/AudioFile";

describe("AudioService", () => {
  let service: AudioService;
  let mockFetch: Mock<typeof fetch>;

  beforeEach(() => {
    mockFetch = vi
      .spyOn(globalThis, "fetch")
      .mockImplementation(() => Promise.resolve(new Response()));
    TestBed.configureTestingModule({});
    service = TestBed.inject(AudioService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
    const audioFiles = Object.values(AudioFile);
    for (let i = 0; i < audioFiles.length; ++i) {
      expect(mockFetch).toHaveBeenNthCalledWith(i + 1, audioFiles[i]);
    }
  });
});
