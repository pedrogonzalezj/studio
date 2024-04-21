# Classes app

Classes app is a simple example of a studio class and booking java application using spring boot.

## Use cases.

### Create class.

### Book class.

## Requirements.
1. java 21 or higher.
2. Docker.
3. Internet access.

## Installation.

1. Download app source code from github.
2. run ```./gradlew clean build``` at the project source directory.

## Running the app.

1. Compile source code following the [previous installation section](#installation).
2. Update [application configuration](src/main/resources/application-development.properties) and [docker-compose](compose.yaml) files as needed
   (**updating database host, username, password, etc properties at compose file will require to update application configuration file**).
3. Start local dependencies (database) using docker compose at the project root directory:
    ```shell
    docker-compose up &
    ```
4. Once local dependencies are running start the banking application with the following command:
    ```shell
   ./gradlew bootRun --args='--spring.profiles.active=development'
   ```
   or
    ```shell
   java -jar -Dspring.profiles.active=development studio-0.0.1-SNAPSHOT.jar
   ```
   **[WARNING] Note that is important to use spring boot development profile in order to run the application at the development environment
   (there are only application configuration files for [development](src/main/resources/application-development.properties) and [it](src/test/resources/application-it.properties) environments)**
5. Once local dependencies are running you can access mariadb and query it. <br>

## Running tests.

run ```./gradlew clean test``` at the project source directory.

## Running integration tests.

Integration tests uses [test containers](https://testcontainers.com/) for booting app dependencies, then in order to
run integration tests docker is required. <br>
run ```./gradlew clean test``` at the project source directory.

## Testing the application with postman ui.

1. run the application following ["Running the app" section steps](#running-the-app).
2. use postman collection at: [postman](./postman/studio.postman_collection.json)

## Contributing.

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License.

[the unlicense](LICENSE)
