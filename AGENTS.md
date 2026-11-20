# Repository guidance

## Toolchain and layout

- Use SDKMAN: `.sdkmanrc` pins `java=11.0.32+1.1-librca` and `maven=3.9.16`. Agent shells are non-interactive, so `sdkman_auto_env` does not fire; prefix every Maven command with `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env && ` from the repository root (where `.sdkmanrc` lives). If a version is missing, run `sdk env install`.
- JDK 11 is required: the project pins Quarkus 1.11.3 and Lombok 1.18.16; compilation fails on JDK 17+ with a `LombokProcessor` module-access error even though the POM targets Java 11. The SDKMAN default (`current`) may not be 11, so do not skip `sdk env`.
- There is no Maven wrapper. The root POM is only an aggregator/parent; the sole runnable module is `rest-team`.
- Run module commands from the repository root with `-pl rest-team`. If you work from `rest-team/`, run `sdk env` from the root first (that directory has no `.sdkmanrc`). After `sdk env`, a `cd` outside the repository makes SDKMAN restore its default Java; use the absolute JDK 11 path (`$HOME/.sdkman/candidates/java/11.0.32+1.1-librca/bin/java`) when running jars from elsewhere.

## Commands

All commands below assume the SDKMAN prefix above has been applied in the same shell invocation.

- Unit suite: `mvn -pl rest-team clean test`. The default Surefire execution excludes tests tagged `integration`.
- One unit class: `mvn -pl rest-team clean -Dtest=TeamServiceTest test`.
- Full JVM verification: `mvn -pl rest-team clean verify`. This also runs the `integration`-tagged tests and requires a working Docker daemon for PostgreSQL and Keycloak Testcontainers; the external Compose stack is not the test fixture.
- One integration class: `mvn -pl rest-team clean -Dtest=TeamResourceIntegrationTest verify`. Use `verify`, not `integration-test`, so JaCoCo's post-integration restore/report phases run.
- Testcontainers must stay ≥ 1.21.x (Docker 29 rejects the API 1.32 used by older versions), and the root POM pins JNA 5.13.0 over the Quarkus BOM's 5.3.1, which has no macOS arm64 native library. The Keycloak test container listens on and exposes `8082`; keep `jboss.http.port` and `withExposedPorts` in sync. The realm JSON is copied into it with explicit mode `0644` because the local checkout's file mode is `700` and the container user cannot read a bind/copy that keeps it.
- Native suite: `mvn -pl rest-team clean verify -Pnative`; add `-Dquarkus.native.container-build=true` when GraalVM is unavailable (Quarkus 1.11 needs GraalVM 20.3, not the installed 21+/25). The container build outputs a linux/amd64 binary that cannot run directly on macOS; run it in a `--platform linux/amd64 --network host` container (e.g. `ubi8/ubi-minimal`) with the binary `docker cp`'d in.
- Uber-jar: use `mvn -pl rest-team package -Dquarkus.package.type=uber-jar` (there is no `uberJar` Maven profile).
- Dev mode: first start dependencies with `docker-compose -f infrastructure/docker-compose-linux.yaml up`, then run `mvn -pl rest-team clean compile quarkus:dev` (`clean` avoids JaCoCo-instrumented classes left by a previous `test` run, which fail with `NoClassDefFoundError: org/jacoco/agent/...`). The API is at `http://localhost:8081/rest-team`; health, metrics and openapi live under `/rest-team/q/...` (the un-prefixed paths 301-redirect).
- Compose (Linux and macOS): both files set a top-level `name:` (`quarkus-soccer-game`, `quarkus-soccer-game-sonar`) so their volumes cannot collide with other repos that also have an `infrastructure/` directory. Services use the default bridge network; do not reintroduce `network_mode: host`, which breaks on Docker Desktop/Colima. Prometheus scrapes the host-run app at `host.docker.internal:8081` via `extra_hosts: host-gateway`, which is why `quarkus.http.host=0.0.0.0` is set (dev mode would otherwise bind only to localhost, unreachable from the Linux bridge). Grafana's datasource uses `http://prometheus:9090` in proxy mode. The dashboard filters on the `env` label added in `prometheus.yml` and on metric names derived from `resource.TeamResource`; renaming that class or package breaks every panel. Keycloak 12 is amd64-only (`platform: linux/amd64`).
- SonarQube: `sonar-maven-plugin` is declared only in `rest-team/pom.xml`, so run `mvn clean install sonar:sonar` from `rest-team/` (the `sonar:` prefix fails from the root). The compose uses multi-arch `sonarqube:10.3.0-community` with `SONAR_ES_BOOTSTRAP_CHECKS_DISABLE`, so no `sysctl` is needed. The POM expects `admin`/`sonar`; change the `admin`/`admin` password of a fresh instance (`api/users/change_password`) before analysing.
- JaCoCo uses offline instrumentation. If Maven is interrupted or fails after instrumentation, the next run may report “Cannot process instrumented class”; rerun with `clean`.
- No lint or formatter task is configured; Maven compilation/tests are the repository's executable checks.

## Runtime and code flow

- `application.properties` points to local PostgreSQL on `5432` and Keycloak on `8080`. Non-production startup uses Hibernate `drop-and-create` and loads `src/main/resources/import.sql`, so it destroys and reseeds the configured database.
- The request flow is `TeamResource` (REST/auth/validation) -> `TeamService` (transactions/domain behavior) -> Panache repositories/entities. MapStruct bridges DTOs and entities; implementations under `target/generated-sources/annotations` are generated and must not be edited.
- Mutations require the Keycloak role `team`; reads are public. Deletes are soft deletes, and level updates average the submitted level with the stored level rather than replacing it.
- The `prod` profile uses Hibernate `update` and does not seed data.
- `(name, country)` is unique even for soft-deleted teams (`uc_team`). `TeamService` checks this before create/update (excluding the team's own id on update) and throws `EntityAlreadyExistsException`, which `EntityAlreadyExistsExceptionHandler` maps to 409. Checked domain exceptions plus `ExceptionMapper`s in `handler/` are the error-handling pattern.

## Test conventions

- `src/test/resources/import.sql` deliberately shadows the main seed data and creates only the records expected by integration tests.
- `TeamResourceIntegrationTest` is ordered and stateful: later cases depend on updates/deletes made by earlier cases. Preserve its ordering unless the fixture strategy is changed.
- Native test classes inherit the JVM integration tests and are selected by the `native` tag/profile; do not duplicate test bodies in them.
