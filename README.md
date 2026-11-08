# Taller de automatización de pruebas en Java

**Autor:** Rolando Rivera  
**Evaluación:** Taller 1, Unidad II

Proyecto de una calculadora de enteros que integra pruebas unitarias, escenarios BDD, integración continua, reportes y una prueba breve de rendimiento sobre un servicio HTTP local.

- [Repositorio del proyecto](https://github.com/Rolando-rivera/taller-QA-java)
- [Pull request 1 integrado en main](https://github.com/Rolando-rivera/taller-QA-java/pull/1)
- [CI normal de main](https://github.com/Rolando-rivera/taller-QA-java/actions/runs/42531710756)
- [Simulación de alerta](https://github.com/Rolando-rivera/taller-QA-java/actions/runs/42531825207)

## Objetivos

- Comprobar suma y resta mediante dos pruebas unitarias atómicas e independientes.
- Definir criterios de aceptación en una sesión Three Amigos simulada y automatizarlos con Cucumber.
- Ejecutar la validación automáticamente en cada push y pull request.
- Publicar reportes funcionales y de rendimiento para su revisión.
- Comprobar la activación de una alerta ante el incumplimiento de un umbral.

## Requisitos y versiones

| Componente | Versión o configuración |
| --- | --- |
| Java | JDK 17; Temurin 17 en GitHub Actions. |
| Maven | Maven instalado y disponible como `mvn` en PATH. |
| Git | Instalado y disponible en PATH. |
| JUnit | 4.13.2, declarado en `pom.xml`. |
| Cucumber Java y JUnit | 7.20.1, declarado en `pom.xml`. |
| Maven Compiler Plugin | 3.13.0. |
| Maven Surefire Plugin | 3.5.2. |
| k6 | Instalado en PATH para uso local; imagen `grafana/k6:0.57.0` en CI. |
| Python | Python 3 para generar el resumen en el runner de CI. |
| Editor | Visual Studio Code o cualquier editor de código. |

Los comandos locales siguientes están preparados para PowerShell. Para CI se requiere un repositorio de GitHub con Actions habilitado. El flujo utiliza un runner `ubuntu-latest` y Docker para ejecutar k6; Docker no es necesario para los comandos locales que usan k6 instalado directamente.

Comprobación del entorno:

```powershell
java -version
javac -version
mvn -version
git --version
k6 version
```

`JAVA_HOME` debe apuntar a la carpeta del JDK, sin agregar `bin`. Las carpetas de ejecutables de Java, Maven, Git y k6 deben estar disponibles en PATH.

## Estructura del proyecto

| Archivo o carpeta | Responsabilidad |
| --- | --- |
| [pom.xml](pom.xml) | Dependencias de pruebas y plugins de compilación y ejecución. |
| [Calculadora.java](src/main/java/cl/taller/Calculadora.java) | Métodos `sumar` y `restar`. |
| [CalculadoraServidor.java](src/main/java/cl/taller/CalculadoraServidor.java) | Servicio HTTP de laboratorio en `127.0.0.1:8080`. |
| [CalculadoraTest.java](src/test/java/cl/taller/CalculadoraTest.java) | Dos pruebas unitarias con JUnit. |
| [CalculadoraSteps.java](src/test/java/cl/taller/bdd/CalculadoraSteps.java) | Definiciones de pasos de Cucumber. |
| [CucumberTest.java](src/test/java/cl/taller/bdd/CucumberTest.java) | Runner JUnit de Cucumber y configuración del reporte HTML. |
| [calculadora.feature](src/test/resources/features/calculadora.feature) | Escenario de suma y Scenario Outline de resta. |
| [ci.yml](.github/workflows/ci.yml) | Pipeline de compilación, pruebas, rendimiento, dashboard y alertas. |
| [suma.js](performance/suma.js) | Carga y umbrales de rendimiento con k6. |
| [resumen.py](scripts/resumen.py) | Resumen del job a partir de Surefire y la salida de k6. |
| [.gitignore](.gitignore) | Exclusión de archivos generados y configuración local. |

La carpeta `target/` contiene productos de compilación y reportes generados. Se excluye del repositorio junto con `.idea/`, `.vscode/`, `*.iml` y `*.class`. Los reportes de CI se distribuyen como artefactos.

## Instalación y ejecución de pruebas

Para obtener una copia nueva:

```powershell
git clone https://github.com/Rolando-rivera/taller-QA-java.git
cd taller-QA-java
mvn clean verify
```

Si el proyecto ya está descargado, abrir una terminal en su carpeta raíz, donde se encuentra `pom.xml`, y ejecutar solamente `mvn clean verify`.

El resultado esperado del proyecto completo es:

| Suite | Casos | Comportamiento |
| --- | ---: | --- |
| `cl.taller.CalculadoraTest` | 2 | Suma y resta unitarias. |
| `cl.taller.bdd.CucumberTest` | 4 | Un escenario de suma y tres ejemplos de resta. |
| **Total** | **6** | Sin fallos, errores ni casos omitidos cuando la validación es satisfactoria. |

Las pruebas iniciales de la actividad 1, antes de incorporar BDD, ejecutaban solamente los dos casos JUnit.

Para ejecutar cada suite por separado:

```powershell
mvn -Dtest=CalculadoraTest test
mvn -Dtest=CucumberTest test
```

Para abrir el reporte navegable después de ejecutar Cucumber:

```powershell
Invoke-Item ./target/cucumber.html
```

Surefire genera los archivos TXT y XML en `target/surefire-reports/`. La ausencia de fallos se comprueba en los conteos de las suites, además del mensaje `BUILD SUCCESS`.

## Decisiones sobre las pruebas unitarias

Cada prueba verifica una sola operación, utiliza entradas fijas y crea su propia instancia de `Calculadora`. Un caso no consume resultados del otro ni depende del orden de ejecución. La clase de dominio no necesita archivos, red ni estado compartido.

Se eligieron pruebas unitarias porque los requisitos iniciales corresponden a lógica aritmética aislada. El servidor HTTP se mantiene separado de esa lógica. El alcance utiliza `int` de Java; los ejemplos no producen desbordamiento y no se evalúan entradas de una interfaz de usuario.

## Three Amigos simulado

La sesión representa las perspectivas de negocio, desarrollo y QA; no se presenta como una reunión real con otros participantes.

| Rol | Aporte y acuerdo |
| --- | --- |
| Negocio | Solicita sumar y restar enteros y acepta resultados cero y negativos. |
| Desarrollo | Aclara que `restar(a, b)` devuelve `a - b` y conserva el orden de los operandos. |
| QA | Propone ejemplos positivos, cero y negativos, y exige casos independientes. |

Criterios de aceptación:

- **CA1:** la suma devuelve el resultado correcto para los ejemplos del alcance.
- **CA2:** la resta sustrae el segundo operando del primero y admite cero y resultados negativos.
- **CA3:** los escenarios se ejecutan de forma independiente y el reporte identifica sus pasos y resultados.

QA plantea `3 - 5` para aclarar el comportamiento de una resta con resultado negativo. Desarrollo confirma el orden y negocio acepta `-2`. Se acuerda agregar cero y un operando negativo a los ejemplos de resta.

| Operación | Resultado esperado |
| --- | ---: |
| `4 + 3` | 7 |
| `3 - 5` | -2 |
| `0 - 0` | 0 |
| `-2 - 3` | -5 |

## Automatización BDD

El archivo Gherkin contiene un `Scenario` para la suma y un `Scenario Outline` con tres filas de `Examples` para la resta. De este modo se ejecutan cuatro casos BDD.

- `Given` establece los operandos.
- `When` ejecuta la operación.
- `Then` compara el resultado con el valor esperado mediante una aserción.

Las definiciones de pasos reutilizan la API de `Calculadora`. Cucumber crea una instancia de los pasos por escenario y sus campos no son estáticos, evitando compartir estado entre casos. El runner termina en `Test` para que Surefire lo encuentre y utiliza el plugin `html:target/cucumber.html` para generar el reporte.

## Prueba de rendimiento local

El servicio expone `http://127.0.0.1:8080/suma`, realiza la operación fija `4 + 3` y devuelve el texto `7`. La prueba verifica HTTP 200 y ese cuerpo de respuesta.

En la **primera terminal**, desde la raíz del proyecto:

```powershell
mvn -B -DskipTests package
java -cp target/classes cl.taller.CalculadoraServidor
```

Mantener el servidor activo. En la **segunda terminal**, también desde la raíz:

```powershell
Invoke-RestMethod http://127.0.0.1:8080/suma
k6 run performance/suma.js
```

Al terminar, detener el servidor con `Ctrl+C` en la primera terminal. El comando con `-DskipTests` prepara la aplicación para este ensayo; la validación funcional completa se realiza con `mvn clean verify`.

### Carga e indicadores

El ejecutor `constant-arrival-rate` programa 10 iteraciones por segundo durante 15 segundos. Cada iteración realiza una solicitud. Se reservan 5 usuarios virtuales y se permite llegar a 20. El objetivo nominal es iniciar 150 solicitudes.

| Indicador | Criterio | Interpretación |
| --- | --- | --- |
| Solicitudes por segundo (`http_reqs`) | Objetivo de 10/s. | Tasa observada de solicitudes; una petición por iteración. |
| Latencia p95 (`http_req_duration`) | Menor que 200 ms. | El 95 % de las duraciones queda en el percentil indicado o por debajo. |
| Errores HTTP (`http_req_failed`) | Menores al 1 %. | Proporción de solicitudes fallidas según k6. |
| Checks funcionales (`checks`) | 100 %. | Respuestas con HTTP 200 y cuerpo `7`. |
| Iteraciones omitidas (`dropped_iterations`) | 0. | Carga programada que no pudo iniciarse. |

Los umbrales son criterios de este laboratorio. El ensayo es breve y el generador y el servidor se ejecutan en el mismo equipo o runner; no demuestra capacidad de producción, comportamiento con usuarios remotos ni estabilidad bajo carga prolongada.

## Git y colaboración

La rama `main` integra los cambios. La actividad 1 utilizó `feature/pruebas-unitarias` y la actividad 2 utilizó `feature/bdd`.

Entre los commits del historial se encuentran:

- `Implementa calculadora y pruebas unitarias`.
- `Automatiza pruebas con GitHub Actions`.
- `Configura pipeline Maven y publica reportes de pruebas`.
- `Automatiza criterios de aceptación con Cucumber`.
- `Integra rendimiento dashboard y alertas de calidad`.

El [pull request 1](https://github.com/Rolando-rivera/taller-QA-java/pull/1) integró BDD, rendimiento, dashboard y alertas en `main`. El commit de integración es `7d95d688ec15bce077ed624aed2ae04d36d48c9e`.

Para consultar el historial local:

```powershell
git log --oneline --graph --decorate --all
```

Para futuros cambios, crear una rama, registrar commits con mensajes que describan el cambio y revisar `Files changed` y `Checks` en el pull request antes de integrar.

## Pipeline de integración continua

El workflow `Taller QA` se activa con `push`, `pull_request` y ejecución manual mediante `workflow_dispatch`.

1. Descarga el código y configura Temurin 17 con caché Maven.
2. Ejecuta `mvn -B clean verify` para compilar y correr JUnit y Cucumber.
3. Inicia el servidor HTTP y espera su respuesta con `curl`.
4. Ejecuta k6 en Docker con los umbrales configurados.
5. Publica el dashboard en el resumen del job.
6. Si existe un fallo, ejecuta `Registrar alerta de calidad`.
7. Intenta publicar los reportes tanto en éxito como en fallo.

El contenedor usa `--network host` para alcanzar el servicio del runner Linux. `trap` detiene el servidor al finalizar el paso. La opción `pipefail` conserva el error de k6 aunque `tee` guarde su salida en `target/k6.txt`.

Si Maven falla, la prueba de rendimiento se omite. Los pasos con `if: always()` intentan publicar los datos disponibles y el dashboard informa si faltan resultados. Un error de compilación debe resolverse antes de interpretar resultados de pruebas que no llegaron a ejecutarse.

## Reportes y dashboard

En **Actions**, abrir una ejecución de `Taller QA`:

- El resumen del job presenta una tabla con suite, tests, fallos, errores y omitidos.
- La sección de rendimiento muestra los indicadores extraídos de la salida de k6.
- El artefacto **`reportes-pruebas`** permite descargar las evidencias generadas.

El artefacto está configurado para incluir:

| Ruta | Contenido |
| --- | --- |
| `target/surefire-reports/` | TXT y XML de las suites. |
| `target/cucumber.html` | Reporte BDD navegable por escenarios y pasos. |
| `target/k6.txt` | Salida de k6 y resultados de umbrales. |
| `target/servidor.log` | Salida del servidor HTTP. |

Descargar el artefacto desde el resumen de la ejecución, descomprimirlo y abrir `cucumber.html` en el navegador. Los TXT de Surefire permiten comprobar el nombre de la suite y sus conteos.

El dashboard muestra un ensayo por ejecución. Para disponer de tendencias históricas sería necesario conservar y consolidar las métricas de distintos builds.

## Alertas y simulación

Un test fallido o un umbral de k6 incumplido deja el job en estado fallido. El paso `Registrar alerta de calidad`, condicionado con `if: failure()`, publica la anotación `Control de calidad fallido` y orienta a revisar el primer paso fallido y sus reportes.

Para reproducir la simulación en GitHub:

1. Abrir **Actions** y seleccionar **Taller QA**.
2. Pulsar **Run workflow** y seleccionar la rama `main`.
3. Activar `simular_alerta` y ejecutar el flujo.
4. Revisar el fallo del paso `Ejecutar rendimiento`, el paso de alerta y los reportes publicados.

La opción establece `SIMULAR_ALERTA=1` y cambia el umbral normal `p(95)<200` por `p(95)<0`. El límite forzado provoca un fallo controlado; no representa una degradación real del servicio. Para una ejecución manual normal, dejar desactivada esa opción.

Como ampliación, se propone enviar al canal del equipo el repositorio, commit, paso fallido y enlace del job. Los fallos funcionales se derivarían a QA y desarrollo; una degradación de rendimiento podría escalarse después de dos ejecuciones consecutivas fuera de umbral. Ese envío externo es una propuesta y no está implementado en este workflow.

## Resultados y trazabilidad

Resultados verificados el **8 de noviembre de 2026**. Las métricas pertenecen a las ejecuciones enlazadas y pueden variar al repetir el ensayo.

### Ejecución normal con métricas

[Resultados del pull request — ejecución 42531407762](https://github.com/Rolando-rivera/taller-QA-java/actions/runs/42531407762)

| Resultado | Valor observado |
| --- | --- |
| Pruebas unitarias | 2 aprobadas; 0 fallos, errores u omitidas. |
| Casos BDD | 4 aprobados; 0 fallos, errores u omitidos. |
| Solicitudes HTTP | 151. |
| Solicitudes por segundo | 10,065662/s, aproximadamente 10,07/s. |
| Latencia p95 | 1,05 ms. |
| Errores HTTP | 0 %: 0 de 151 solicitudes. |
| Checks | 100 %: 151 de 151. |
| Iteraciones omitidas | 0. |
| Estado del job | Satisfactorio. |

La tasa observada se aproxima al objetivo de 10 solicitudes/s y se cumplen los umbrales. La captura registra 151 solicitudes frente al objetivo nominal de 150; se conserva el valor medido sin ajustarlo.

### Validación de main después de integrar

[CI de main — ejecución 42531710756](https://github.com/Rolando-rivera/taller-QA-java/actions/runs/42531710756)

La ejecución activada por el push del commit de integración `7d95d68` terminó correctamente. Los pasos de pruebas, rendimiento, dashboard y publicación de reportes fueron satisfactorios. El paso de alerta se omitió, conforme a su condición de ejecución. Se publicó el artefacto `reportes-pruebas`.

### Simulación de alerta

[Simulación — ejecución 42531825207](https://github.com/Rolando-rivera/taller-QA-java/actions/runs/42531825207)

Las dos pruebas unitarias y los cuatro casos BDD aprobaron. El paso de rendimiento falló por el umbral forzado `p(95)<0`; la latencia p95 medida fue 758,91 µs, equivalente a 0,75891 ms. Se registraron 151 solicitudes, 0 errores HTTP y 100 % de checks.

El mensaje de k6 fue `thresholds on metrics 'http_req_duration' have been crossed`. Los pasos de dashboard, alerta de calidad y publicación de reportes finalizaron correctamente. Este resultado demuestra la activación del control ante un umbral incumplido y debe distinguirse de una ejecución normal fallida por un defecto real.

## Referencias

- [Cucumber: pasos, ejecución y plugins](https://cucumber.io/docs/cucumber/api/).
- [GitHub: publicación de artefactos](https://github.com/actions/upload-artifact).
- [GitHub: comandos de workflow y resúmenes](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-commands).
- [Grafana k6: constant arrival rate](https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/constant-arrival-rate/).
- [Grafana k6: métricas integradas](https://grafana.com/docs/k6/latest/using-k6/metrics/reference/).
