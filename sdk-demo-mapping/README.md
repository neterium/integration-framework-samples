# Neterium SDK Samples : sdk-demo-mapping

Sample application showing how to use SDK built-in converters for transaction files in

- PACS ISO 20022 XML format
- Swift FIN MT-103 format

The output of the conversion process may be requested either as **JSON** payloads or as **Java** payloads.

## Build

Please refer to the documentation in the [parent](../README.md) module to learn how to build the samples apps.

## Run

```shell

# PACS-008 format, json payloads
java -jar target/sdk-demo-mapping*.jar --spring.profiles.active=pacs-json

# PACS-008 format, java payloads
java -jar target/sdk-demo-mapping*.jar --spring.profiles.active=pacs-java

# FIN format, json payloads
java -jar target/sdk-demo-mapping*.jar --spring.profiles.active=fin
```
