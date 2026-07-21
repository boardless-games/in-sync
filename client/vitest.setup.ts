import { vi } from "vitest";

vi.stubGlobal("AudioContext", vi.fn(class {}));
