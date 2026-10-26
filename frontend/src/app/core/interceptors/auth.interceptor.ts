import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, from, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  if (!request.url.startsWith(environment.apiBaseUrl)) {
    return next(request);
  }

  const withToken = (token: string | null) =>
    token ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : request;

  return next(withToken(auth.accessToken())).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || !auth.authenticated()) {
        return throwError(() => error);
      }

      return from(auth.refreshToken()).pipe(
        switchMap((refreshed) => {
          if (!refreshed) {
            void auth.login().catch(() => undefined);
            return throwError(() => error);
          }
          return next(withToken(auth.accessToken()));
        }),
      );
    }),
  );
};
