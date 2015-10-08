# hyppo-test-integration
An example of a hyppo integration that generates it's own data internally. Particularly useful for testing.

## Overview

Here are the classes involved in retrieving, processing, persisting data.  This can serve as a starting point for a "real" integration of an ingestion source.

1. DemoTaskCreator -- split up a job into multiple tasks.
2. DemoDataFetcher -- should collect data from an API or some other source.  The demo integration simply creates a list of numbers in the `value` field.
3. DemoDataProcessor -- should turn data from the fetcher into processed Avro recordds
4. DemoDataPersister -- should upload the data (but the demo integration just logs the values).

Also needed:

* JSON schema 
* Avro schema


## Test run

1. Create the artefact (in `target/scala-2.<version>/<integration name>-<version>.jar`)
```shell
sbt clean compile assembly
```
2. Upload the jar file to S3
3. Add an ingestion source along with this integration in the integration manager
3.1. For the source, use a config like this one:
```JSON
{
    "firstValue" : 1,
    "lastValue" : 20,
    "chunkSize" : 10,
    "jdbcUrl" : "DSN"
}
```
3.2. For the integration, enter `com.harrys.demo.DemoIntegration` as the class name and add the S3 bucket and object key information based on the upload step.
4 Now submit a job to test it.

Hint: Killing the executor process will dump the logging to STDOUT.
