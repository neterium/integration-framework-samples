# Neterium SDK Samples : sdk-demo-throttling-standalone

This headless application can be used to issue a **large** number of screening requests to Neterium,
causing the throttler to activate and regulate the request flow.

It requires two parameters:

`ThrottlingApp nbRecords ratePerSecond`

- **nbRecords** : the desired total number of synthetic **records** to generate
- **ratePerSecond** : the desired issuing rate in terms of api **requests** / sec

Pay attention to the unit (_record_ vs _request_) of these parameters !
Indeed, while for name screening, counterparts are very often grouped into batches of n records
before being sent to JetScan, batching (though of course possible) is less frequent for transaction screening.

This is the reason why **batching has been disabled for transactions** in this sample application.
Each transaction will thus be sent individually inside a single-item batch.

Other parameters like

- the hit ratio (modeling the percentage of records for which we want a hit),
- the batch size
- the screening threshold
- the throttler settings

can be entirely controlled via the config file:

**application.yaml**

```yaml
neterium:

  demo-app:
    hit-ratio: 0.2  # 20%

  screening:
    batch-size: 120
    options:
      threshold: 85

  throttling:
    ...
```

## Build

Please refer to the documentation in the [parent](../../README.md) module to learn how to build the samples apps.

## Configure

- Create an `.env` file based on provided [template](../../template.env)
- Edit the file to put your credentials

## Run

```shell
# Screen 350.000 counterparts, sending 50 batches/sec
java -jar target/sdk-demo-throttling*.jar 350000 50 --spring.profiles.active=jetscan

# Screen 160.000 transactions, sending 60 records/sec
java -jar target/sdk-demo-throttling*.jar 160000 60 --spring.profiles.active=jetflow
```

Examine the traces in the console to see how the throttler component is using the measured response
times to calibrate the thread pool size.
