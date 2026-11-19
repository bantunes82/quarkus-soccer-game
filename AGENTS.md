# Repository guidance

## Toolchain and layout

- Use SDKMAN: `.sdkmanrc` pins `java=11.0.32+1.1-librca` and `maven=3.9.16`. Agent shells are non-interactive, so `sdkman_auto_env` does not fire; prefix every Maven command with `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env && ` from the repository root (where `.sdkmanrc` lives). If a version is missing, run `sdk env install`.
- JDK 11 is required: the project pins Quarkus 1.11.3 and Lombok 1.18.16; compilation fails on JDK 17+ with a `LombokProcessor` module-access error even though the POM targets Java 11. The SDKMAN default (`current`) may not be 11, so do not skip `sdk env`.
- There is no Maven wrapper. The root POM is only an aggregator/parent; the sole runnable module is `rest-team`.
- Run module commands from the repository root with `-pl rest-team`. If you work from `rest-team/`, run `sdk env` from the root first (that directory has no `.sdkmanrc`).

## Commands

All commands below assume the SDKMAN prefix above has been applied in the same shell invocation.

- Unit suite: `mvn -pl rest-team clean test`. The default Surefire execution excludes tests tagged `integration`.
- One unit class: `mvn -pl rest-team clean -Dtest=TeamServiceTest test`.
- Full JVM verification: `mvn -pl rest-team clean verify`. This also runs the `integration`-tagged tests and requires a working Docker daemon for PostgreSQL and Keycloak Testcontainers; the external Compose stack is not the test fixture.
- One integration class: `mvn -pl rest-team clean -Dtest=TeamResourceIntegrationTest verify`. Use `verify`, not `integration-test`, so JaCoCo's post-integration restore/report phases run.
- Testcontainers must stay ≥ 1.21.x (Docker 29 rejects the API 1.32 used by older versions), and the root POM pins JNA 5.13.0 over the Quarkus BOM's 5.3.1, which has no macOS arm64 native library. The Keycloak test container listens on and exposes `8082`; keep `jboss.http.port` and `withExposedPorts` in sync. If Keycloak fails with `team-realm.json (Permission denied)`, the realm file needs to be world-readable (`chmod -R go+rX rest-team/src/test/resources/keycloak`).
- Native suite: `mvn -pl rest-team clean verify -Pnative`; add `-Dquarkus.native.container-build=true` when GraalVM is unavailable.
- Dev mode: first start dependencies with `docker-compose -f infrastructure/docker-compose-linux.yaml up`, then run `mvn -pl rest-team compile quarkus:dev`. The API is at `http://localhost:8081/rest-team`.
- JaCoCo uses offline instrumentation. If Maven is interrupted or fails after instrumentation, the next run may report “Cannot process instrumented class”; rerun with `clean`.
- No lint or formatter task is configured; Maven compilation/tests are the repository's executable checks.

## Runtime and code flow

- `application.properties` points to local PostgreSQL on `5432` and Keycloak on `8080`. Non-production startup uses Hibernate `drop-and-create` and loads `src/main/resources/import.sql`, so it destroys and reseeds the configured database.
- The request flow is `TeamResource` (REST/auth/validation) -> `TeamService` (transactions/domain behavior) -> Panache repositories/entities. MapStruct bridges DTOs and entities; implementations under `target/generated-sources/annotations` are generated and must not be edited.
- Mutations require the Keycloak role `team`; reads are public. Deletes are soft deletes, and level updates average the submitted level with the stored level rather than replacing it.

## Test conventions

- `src/test/resources/import.sql` deliberately shadows the main seed data and creates only the records expected by integration tests.
- `TeamResourceIntegrationTest` is ordered and stateful: later cases depend on updates/deletes made by earlier cases. Preserve its ordering unless the fixture strategy is changed.
- Native test classes inherit the JVM integration tests and are selected by the `native` tag/profile; do not duplicate test bodies in them.
