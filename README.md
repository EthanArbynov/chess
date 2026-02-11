# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

Phase 2: Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmJ43h+P40DsIyMQinAEbSHACgwAAMhAWSFFhzBOtQ-rNG0XS9AY6j5GgBGKsRpHfL8-yAqhaGgYKQH+uB5aqah6n6H8Ow0Wh+nwr6zpdjACDCeKGJCSJBJEmApLboYu40vuDJMlOKlcjefl3kuwowGKEpuh6mAqsGGpvDAaAQMwABmvgStA2reA4MAaTsoW8uFElUEiKW9v2b4+WV-rMtMl7QEgABeKAMbpSaVGJaZOAAjAROaqHm8yLEWJb1D4jV6s1bW7HRTbFQuApdR25Uuhu7pbjVgrDvy9SHnIKDPvEGIndee2LpUy4BmuAaXq+dnttZ9SueKGSqABmDWSB1QGYRRnzCRJmwZeVH1sDtHofCybIKmMC4fhowA+8kGQ2RYwUeDyHow29GMV4vgBF4KDoDEcSJCTZOub4WBiYKoH1A00gRgJEbtBG3Q9PJqiKcMWOIeg0N6Y8cL1ALSHfaLGFlRVjn2LT55g4LaBeTtvklftMCMmAJ1K-BKtzmFDoRfU0VPg9W7xaqGrilQJpIOuEtC5dK2yxtPZ9t5v2SaWDUUbN7XC6tPUwOmA3I0NI1QWM43QJN03xIH82NgxS37j7632ed21PRry31BwKDcMel765Rhvp+F12RRkMwQDQ90vrnnbPdLpaffuUuwjLdVgVcnWwyUYA4XhWYLWnTFE-4KLrv42DihqAlojAADiSoaPTfdM6v7Nc-YSr88rkudZUL3GsfQs-e79nIDk685uX2Nq3nVK3lrOt687qtVybNdm+KC2zd5CmASuqGAdsHZO0voUV2mcKqe2qnnbekpE7J2DkPeG4dBr8mjpDOOpYpoBygK1dqeNFpwNWjVeoOcQHqzfsbekDk0QPzUBiI2msrpCnqKvJklYEBryVI9Vuq1z7L3vhvD6X1r4oPGAfHMCwGguGUZ0Aef0YbdThthBGY9kbyLUIo5RLhVGpwJsxAIHAADsbgnAoCcDECMwQ4DcQAGzwAnIYVhRQtHiVsr7JmrQOj70PonbGWZ9EADklRqN9iLHupZv5rEiUqbuX4GZ2QOh41hT8VZJKVFEuYL8REMM4aObWTIv4wI4cte8kVzZNyvFbMBts0D22QNAg2ktKE327FVb2VD1GujQSQuaGDNHD16hHbMuD8z4OLPHBUwzSEp3xr-RcPSGnCKzrtd+TDDromyawi6uyVr-1uuuI5LdtmiPbgyJUABJaQqTgIDP8TAORDzpAoT6uEYIgQYmJkwdoxGBF9GPO+b8-5pjTCExYpYYujlNjkyQAkMA8K+wQCRQAKQgOKQRcwYjJFAGqbxw90lvOaMyWSPR9FH06egLM2AEDAHhVAOAEBHJQBWGC6QAKZZn1uRfelaA1g4vFKw9yahALt3JVneoAArXFaBsmJJgGK5VSpJUklMPQmQJyAq6zLt-apGdTZRUAZsppNsIGtKgUKiuXSTnwI9n09WKD-ZNRGUHQe4ysH9RwbmWZ0ECEJ2Ics8hadul+LlZauhr89WMLKTrbJPKTXV24eaiUlyQHW0StrCsqUMpZQYlGtaCDXXIOjf6AAQiGLVWAfXwB8b1JG0zA1A2DfM0seh1wokJFKiNphS3UPxSgLZg4SkFwVNgLQBylQYmzcANNf8M3MhnUdUd473wCvifUTuI5nm9yrf3MZTaJk6NbRPMx08vAsuRai298pEDBlgMAbATLCB5HjFvY9TMWZsw5lzYwwcd1fgOtwPAMA+0eUPTZMtG0QAQagOwnV8bXbgZfShyhZzpDFyZIYE0AjZ3AH6R+QV+7+SwczgZPlGiz3wxBaMK9QA
