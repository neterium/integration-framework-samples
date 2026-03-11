# Neterium SDK Samples : sdk-demo-minimal

Bare minimalist application which is

- producing a random set of synthetic data
    - fake counterparts, when run with `jetscan` Spring profile
    - fake transactions, when run with `jetflow` Spring profile
- using the SDK components to (efficiently) screen these records
- printing to the console the records for which a match was found

## Build

Please refer to the documentation in the [parent](../README.md) module to learn how to build the samples apps.

## Configure

- Create an `.env` file based on provided [template](../template.env)
- Edit the file to put your credentials

## Run

```shell
# Name screening
java -jar target/sdk-demo-minimal*.jar --spring.profiles.active=jetscan

# Transaction screening
java -jar target/sdk-demo-minimal*.jar --spring.profiles.active=jetflow
```

Examine the traces to understand the throttling mechanism.
