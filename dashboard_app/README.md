# Dashboard

## Installation

You will need:

- JDK 8
- node/npm

Install libraries:

1. `./gradlew build`
1. `cd src/main/resources/assets`
1. `npm install`

## Running

1. `./run.sh`
1. Open another process/terminal
1. `cd src/main/resources/assets`
1. `./node_modules/.bin/webpack --watch`

NOTE: Automatic hotloading for static assets [doesn't work if you're using IntelliJ](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html). Hit
`CMD+F8` when you want the server to reload.

## Testinge

- `brew install chromedriver`
- `./gradlew test`
- Disable caching tests: `./gradlew clean test`
- Run different sets: 
    - One test: `./gradlew test -D single.test=MainPageTest`
    - All tests: `./gradlew test`
    - Javascript tests: `./gradlew jsTest`
- More verbosity: 
    - `./gradlew test -i` or 
    - `./gradlew test -D verboseTests`
- Run just the javascript tests: 
    - `./gradlew jsTest` or
    - `cd src/main/resources/assets && npm test` or
    - `cd src/main/resources/assets && ./node_modules/.bin/karma start`