# rest-team project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

This API allows CRUD operations on a soccer team

## Before running the application

Before start running the application we need to start the containers
that this application depends on, for that you need to have docker and
docker-compose installed in your local computer. This is the command
that you have to run:

```shell script
/quarkus-soccer-game/infrastructure$ docker-compose -f docker-compose-linux.yaml up
```

This command it will start the containers below:
-  Postgres
-  Keycloak
-  Prometheus
-  Grafana

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `rest-team-1.0.0-SNAPSHOT-runner.jar` file in the
`/target` directory. Be aware that it is not an `--uber-jar` as the
dependencies are copied into the `target/lib` directory. If you want to
build an `--uber-jar`_, just add the `--uber-jar` option to the command
line:

```shell script
mvn package -PuberJar
```

The application is now runnable using `java -jar
target/rest-team-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native
executable build in a container using:

```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with:
`./target/rest-team-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please
consult https://quarkus.io/guides/maven-tooling.html.

## Overall Comments

**Final Solution**

Once executing the final solution and accessing the swagger
[page](http://localhost:8081/rest-team/swagger-ui/), you can see the
concern in respecting Restful endpoints (as well as the Http Status
codes).

**Swagger - How to call the endpoints via swagger-ui**

- For the Soccer team application endpoints
    - Inside the the **Servers** combo box you have to select the value: ***http://localhost:8081***
    - You have to set the access token for the applications endpoints that have the **LOCKER** signal, to set the access token you have put the value inside the **"Authorize"** field in left side of the page.

      To retrieve the access token for user **teamuser** that belong to the **"team"** role from Keycloak server, you have to run the command below:
        ```shell script
          curl -X POST http://localhost:8082/auth/realms/team-realm/protocol/openid-connect/token  --user team-client:6fe5572d-d0f7-4121-8fc4-d2768bf82836 -H 'content-type: application/x-www-form-urlencoded' -d 'username=teamuser&password=teamuser&grant_type=password'
        ```
      To retrieve the access token for user **test** that does not belong to the **"team"** role from Keycloak server, you have to run the command below:
        ```shell script
          curl -X POST http://localhost:8082/auth/realms/team-realm/protocol/openid-connect/token  --user team-client:6fe5572d-d0f7-4121-8fc4-d2768bf82836 -H 'content-type: application/x-www-form-urlencoded' -d 'username=test&password=test&grant_type=password'
        ```

- For the MicroProfile Health endpoints
  - Inside the the **Servers** combo box you have to select the value: ***http://localhost:8081/rest-team***

 ![Swagger](https://i.ibb.co/1MfXfk0/swagger-ui.png "Swagger Endpoints")

**Prometheus**

This is the url to access the metrics from [prometheus](http://localhost:9090/graph).

**Grafana**

This is the url to access the dashboard from Soccer Team application [grafana DashBoard](http://localhost:3000).

**Keycloak**

This is the url to access the console configuration from [Keycloak console](http://localhost:8082/auth/).
The username and password is "admin"

**Testing**

The test strategy adopted was based on the test pyramid where in the base
we have more Unit and towards the top, we have the integrations and
UI/functional.

A caveat to be mentioned specially when it comes to the Integration
Tests: as they were pretty complex, to speed up the integration tests,
the test containers are created before the execution of the test methods of the class and they are
destroyed after the execution of them.

The coverage is pretty good and you can take a look at the Jacoco plugin
reports available in the IDE/command line.

![Jacoco](https://i.ibb.co/tKLpft3/jacoco.png
"Jacoco Execution")

---

**Static Analyzing (SonarQube)**

Running Sonar as Docker container
```shell script
sudo sysctl -w vm.max_map_count=262144
/quarkus-soccer-game/infrastructure$ docker-compose -f sonarqube-docker-compose.yaml up
```
This is the url to access the [SonarQube](http://localhost:9000/projects?sort=-analysis_date).


In case you have a Sonar instance running locally (or a Docker
Container), you can execute the command `mvn clean install
sonar:sonar` in order to observe the potential Bugs, Code smells,
Technical Debt, etc. The results will be similar to this one:

![SonarQube](https://i.ibb.co/mzz8SrJ/sonar.png
"Sonar Execution")

---




