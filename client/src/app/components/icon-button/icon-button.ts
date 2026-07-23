import { Component, input, output } from "@angular/core";
import { Icon } from "../../constants/Icon";

@Component({
  selector: "app-icon-button",
  imports: [],
  templateUrl: "./icon-button.html",
  styleUrl: "./icon-button.css",
  host: {
    "[style.--button-size]": 'size() + "px"',
    "[style.--icon-color]": "color()"
  }
})
export class IconButton {
  icon = input<Icon | null>(null);
  size = input(24);
  color = input("#FFFFFF");
  clicked = output();
}
