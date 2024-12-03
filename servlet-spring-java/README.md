# Petclinic Demo Application 
#### Version 1.4.0

Demonstrates instrumentation of an A/B test and a feature flag with 
[Variant Experiment Server](https://getvariant.com) on the servlet stack. The demo application is a fork of the 
Spring Framework's [Petclinic demo application](https://github.com/spring-projects/spring-petclinic) 
augmented with one feature flag and one experiment that demonstrate the use of the Variant Java SDK 
to communicate with Variant server. Java 17 or higher is required.

Complete Docs at https://getvariant.com

Directories:
* `api` Bookworms' backend API written in Scala with Akka HTTP. This is the module that uses Variant
  Java SDK to communicate with Variant server. (Scala is a JVM language fully capabale of consuming Java services.)
* `node` Bookworms' frontend written in NodeJS.
* `spi` Java sources for the server-side extensions used by this demo.

#### Download and Deploy Variant Experiment Server
* Follow [Variant Experiment Server Installation and Configuration Guide](https://getvariant.com/documentation/server/variant-experiment-server-installation-and-configuration-guide/)
  to download and deploy Variant server.

* Copy the experiment schema file `bookworms.yaml` to the server's `schemata` directory:
```shell
cp petclinic.yaml <server-directory>/schemata
```
* Edit the Variant config file `<server-directory>/conf/variant.conf`. Uncomment
  and change the value of the parameter `event.writer.max.delay` which forces the flushing of the event
  buffers to disk:
```text
 event.writer.max.delay = 1
```
This enables you to see Variant trace events written out to disk as soon as they are generated.

* Build the server-side extensions and copy them to where the running server will be able to find them:
```shell
cd spi
mvn clean package
cp target/variant-petclinic-spi-1.4.0.jar <server-directory>/spi
```

* Start Variant server:
```shell
<server-directory>/bin/variant start
```
This will start Variant server in the foreground and you should see the following output:
```text
...
2024-12-03 15:28:34,314 INFO - c.v.s.schema.ServerFlusherService - [431] Initialized flusher [TraceEventFlusherCsv] for schema [petclinic]
2024-12-03 15:28:34,320 INFO - c.v.s.schema.Schemata - [422] Deployed schema [petclinic] from file [petclinic.yaml]
2024-12-03 15:28:34,329 INFO - c.v.s.impl.VariantServerImpl - [410] Variant Experiment Server release 1.4.0 started on port [5377] in 1.521s
```

Note that the Bookworms experiment schema uses the trace event flusher that writes trace events into the local
comma-separated values (CSV) file `<server-directory>/log/trace-events.csv`
