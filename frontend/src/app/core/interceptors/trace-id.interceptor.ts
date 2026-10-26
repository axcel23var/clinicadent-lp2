import { HttpInterceptorFn } from '@angular/common/http';

export const traceIdInterceptor: HttpInterceptorFn = (request, next) => {
  const traceId = crypto.randomUUID();
  return next(request.clone({ headers: request.headers.set('X-Trace-ID', traceId) }));
};
