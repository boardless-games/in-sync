import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SoundboardButton } from "./sound-board-button";

describe("SoundboardButton", () => {
  let component: SoundboardButton;
  let fixture: ComponentFixture<SoundboardButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoundboardButton]
    }).compileComponents();

    fixture = TestBed.createComponent(SoundboardButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
