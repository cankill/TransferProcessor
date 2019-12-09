# TransferProcessor
Transfer processor is an implementation for the test task.

To build from parent directory:

```bash
./gradlew clean shadowJar
```

To run:
```bash
java -jar build/libs/TransferProcessor-all.jar
```                           

Used frameworks:
- Guice - for DI
- Apache JAX-Rs - for REST support
- Mapstruct - for auto-mapping generation
- Lombok - to reduce code boilerplate and implement immutability of Entitis
- Groovy+Spock for tests

Code is compatible with java 1.10+

The main architectural idea was to implement DB lockless access.
For this all requests for balance change for the same Account goes through the same Worker.
For this all Command requests are sharded by Account id, or a Transaction id (for parent transaction processing).
ConcurrentLinkedDeque used for support inter-process communication (emulates messaging solution for simplicity).
Implemented FSM to support transactions processing (Commands+Processors).  