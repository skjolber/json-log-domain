# Elasticsearch example
This example contains three YAML definition files, from which elastic configuration is generated. The example utility simply scans the classpath for mapping files and combines these into a single mapping (message type).

The result might be used to configure the mappings of some index using an HTTP call, for example for configuration of the search index in the deploy pipeline.