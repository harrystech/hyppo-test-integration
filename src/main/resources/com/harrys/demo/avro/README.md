Creating Java classes for the Avro schema
===

Note that you can install the `avro-tools` using `homebrew`.

1. Step into the root directory of the java source, e.g.
```
cd src/main/java
```

2. Compile the schema from the rescources directory into the current directory, e.g.
```
avro-tools compile schema ../resources/com/harrys/demo/avro/DemoAvroRecord.avsc .
```
