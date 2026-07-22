import {
  ActivatedRouteSnapshot,
  GuardResult,
  MaybeAsync,
  RouterStateSnapshot
} from "@angular/router";

export function InitGuard(
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): MaybeAsync<GuardResult> {
  return false;
}
