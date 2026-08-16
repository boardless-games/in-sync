import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SoundBoardButton } from "./sound-board-button";

describe("SoundboardButton", () => {
  let component: SoundBoardButton;
  let fixture: ComponentFixture<SoundBoardButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoundBoardButton]
    }).compileComponents();

    fixture = TestBed.createComponent(SoundBoardButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
