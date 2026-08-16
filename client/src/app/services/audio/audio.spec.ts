import { TestBed } from "@angular/core/testing";
import { Mock } from "vitest";
import { SOUND } from "../../constants/Sound";
import { AudioService } from "./audio";

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
    const sounds = Object.values(SOUND);
    for (let i = 0; i < sounds.length; ++i) {
      expect(mockFetch).toHaveBeenNthCalledWith(i + 1, `/audio/${sounds[i]}`);
    }
  });
});
