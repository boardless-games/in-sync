import { AbstractControl } from "@angular/forms";

export type FormValues<T> = {
  [K in keyof T]: T[K] extends AbstractControl<infer V> ? V : never;
};
