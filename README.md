# gateway-service
## Environment variables
Running the **gateway-service** microservice requires setting the below environment variables.
- SPRING_PROFILES_ACTIVE: This environment variable should contain the Spring profiles to activate.
- SPRING_CLOUD_CONFIG_URI: This environment variable should contain the URI of the config service.
- EUREKA_URI: This environment variable should contain the URI of the Eureka service discovery endpoint for the default availability zone.
- JWT_ISSUER_URI: This environment variable should contain the URI of the JWT issuer.
## Sample Environment Variable File
A sample environment variable file that can be used to run the service on a local development environment could contain the below content:
```
#!/bin/bash

export SPRING_PROFILES_ACTIVE=default
export SPRING_CLOUD_CONFIG_URI=http://localhost:9001
export EUREKA_URI=http://localhost:9002/eureka
export JWT_ISSUER_URI=http://localhost:9004
```
## Running on a local development terminal
Assuming the sample environment variable file is named **.gateway-service**, the below commands can be used to run the microservice on a local development terminal
```
source .gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9003
```
## Running a mock JWT issuer
A mock JWT issuer can be run on a local development environment using the below command:
```
npx oauth2-mock-server -a localhost -p 9004
```
