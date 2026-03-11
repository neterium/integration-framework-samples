This is the directory that will be polled by the application in order to import CSV files.

See `application.yaml`:

```yaml
demo:
  polling:
    directory: ./polling
    interval: 30s
```

Ready-to-use sample files can be found in the `sample-files` directory.
Pick some files, drop them in this directory. 
