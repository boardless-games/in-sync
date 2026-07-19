import { ComponentFixture, TestBed } from "@angular/core/testing";

import { BgSplash } from "./bg-splash";

describe("BgSplash", () => {
  let component: BgSplash;
  let fixture: ComponentFixture<BgSplash>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BgSplash]
    }).compileComponents();

    fixture = TestBed.createComponent(BgSplash);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
