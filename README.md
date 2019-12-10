# TransferProcessor
Transfer processor is an implementation for the test task.
The main architectural idea was to implement DB lock-less access.
For this all requests for balance change for the same Account goes through the same Worker.
For this all Command requests are sharded by Account id, or a Transaction id (for parent transaction processing).
ConcurrentLinkedDeque used for support inter-process communication (emulates messaging solution for simplicity).
Implemented FSM to support transactions processing (Commands+Processors). FSM restore is not implemented for simplicity.  


## To build.
Run command from parent directory:
```bash
./gradlew clean shadowJar
```

## To run:
```bash
java -jar build/libs/TransferProcessor-all.jar
```                           

## Used frameworks:
- [Guice](https://github.com/google/guice/wiki/Motivation) - for DI
- [Apache JAX-Rs](http://cxf.apache.org/docs/jax-rs.html) - for REST support
- [Mapstruct](https://mapstruct.org) - for auto-mapping generation
- [Lombok](https://projectlombok.org) - to reduce code boilerplate and implement immutability of Entitis
- [Groovy](https://groovy-lang.org) + [Spock](http://spockframework.org) for tests

Code is compatible with java 1.10+

## Useful links:
[API description](doc/API.md)

[Examples of execution supported API commands](src/test/groovy/com/fan/transfer/integrational/RestEndpointsSpec.groovy)

[Simple load test](src/test/groovy/com/fan/transfer/integrational/ConcurentTransferSpec.groovy)