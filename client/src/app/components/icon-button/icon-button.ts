import { Component, input, output } from "@angular/core";
import { Icon } from "../../constants/Icon";

@Component({
  selector: "app-icon-button",
  imports: [],
  templateUrl: "./icon-button.html",
  styleUrl: "./icon-button.css",
  host: {
    "[style.--button-color]": "buttonColor()"
  }
})
export class IconButton {
  icon = input<Icon | null>(null);
  size = input("24px");
  iconColor = input("#FFFFFF");
  buttonColor = input("var(--blue)");
  clicked = output();

  protected readonly icons = Icon;
}
