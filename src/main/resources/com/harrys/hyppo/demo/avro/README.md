Creating Java classes for the Avro schema
===

Note that you can install the `avro-tools` using `homebrew`.

Compile the schema from the rescources directory into the current directory using:
```shell
avro-tools compile schema src/main/resources/com/harrys/hyppo/demo/avro/DemoAvroRecord.avsc src/main/java
```
