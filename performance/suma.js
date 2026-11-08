import http from 'k6/http';
import { check } from 'k6';
export const options = {
  scenarios: {
    suma: {
      executor: 'constant-arrival-rate',
      rate: 10,
      timeUnit: '1s',
      duration: '15s',
      preAllocatedVUs: 5,
      maxVUs: 20,
    },
  },
  thresholds: {
    http_req_duration: [
      __ENV.SIMULAR_ALERTA === '1' ? 'p(95)<0' : 'p(95)<200',
    ],
    http_req_failed: ['rate<0.01'],
    checks: ['rate==1'],
    dropped_iterations: ['count==0'],
  },
};
export default function () {
  const respuesta = http.get('http://127.0.0.1:8080/suma');
  check(respuesta, {
    'HTTP 200 y resultado 7': (r) =>
      r.status === 200 && r.body === '7',
  });
}
