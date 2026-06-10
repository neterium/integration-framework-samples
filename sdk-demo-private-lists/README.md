# Neterium SDK Samples : sdk-demo-private-lists

Sample application showing how to use SDK built-in `PrivateListTemplate` for

- **regular** private lists (of entities)
- **custom** private lists (risk countries, risk regions, custom ids, etc...)

## Build

Please refer to the documentation in the [parent](../README.md) module to learn how to build the samples apps.

## Configure

- Create an `.env` file based on provided [template](../template.env), and place into project **working dir**
  (`sdk-demo-private-lists`)

```
- integration-framework-samples
  |- sdk-demo-private-lists
     |- .env
```

- Edit the file to put your credentials

```properties
SDK_USER=...
SDK_PWD=...
```

## Run

```shell
java -jar target/sdk-demo-private-lists*.jar
```