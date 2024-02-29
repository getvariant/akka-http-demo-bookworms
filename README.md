# Bookworms Demo Application 
#### Version 1.2

Demonstrates instrumentation of an A/B test and a feature flag with 
[Variant CVM Server](https://getvariant.com). 

### 1. Downloading and Configuring

#### Clone this repository 
```shell
% git clone git@github.com:getvariant/variant-demo-jvm-bookworms.git
```

#### Create the Postgres Database

Assuming you have docker installed (e.g. Docker Desktop for MacOS),
```shell
% cd src/db
% ./postgres-up.sh
% ./schema.sh
```
This will:
* Deploy Postgres 13 in a Docker container with the root user `postgres` listening on port `5432`;
* Create user `bookworms` with password `bookworms`;
* Create database `bookworms` owned by user `bookworms`;
* Create application schema in the database `bookworms`.

If you change any of these setting, be sure to update `bookworms/src/main/resources/application.conf`

##### Local Postgres Database
If you'd rather run with a locally installed Postgres, any recent version should do. It should be
easy to adopt `postgres-up.sh` and `schema.sh` for the local case.

#### Run Unit Tests on API Server (Optional)
```shell
% mvn test
```

#### Build and Deploy the SPI Extensions
Ensure the local SPI jar is the same as the one that came with the
Variant server with which you'll be running. The jar is zipped
alongside the server in variant-server*.zip archive and should be 
copied locally to `variant/lib`.

Optionally, run Unit Tests on API Server (no test ATTOTW).
```shell
% mvn test
```
Build SPI extensions
```shell
% mvn package
```
This will generate the server-side extensions used by this project locally
in `target/variant-demo-jdk-bookworjs-spi-*.jar`. Deploy it to the server:
```shell
cp target/variant-demo-jdk-bookworjs-spi-*.jar <variant-server-dir>/spi
```

#### Deploy Experiment Schema to Variant Server
#### TODO: Containerize
Uncomment the config param `event.writer.max.delay` in file
`<variant-server-dir>/conf/variant.conf` and set it to 1 to avoid delays in trace logging.

Copy experiment schema to Variant server's `schemata` directory
```shell
cp bookworms.yaml <variant-server-dir>/schemata
```
Start Variant Server
```shell
% <variant-server-dir>/bin/variant start
```
The server should come up and give you the following output:
```text
...
2023-08-16 14:46:39,483 INFO - c.v.s.schema.Schemata - [422] Deployed schema [bookworms] from file [bookworms.yaml]
...
2023-08-16 14:46:39,488 INFO - c.v.s.impl.VariantServerImpl - [410] Variant CVM Server release 0.1.1 started on port [5377] in 1.048s
```
#### Run Bookworms API Server
```shell
% cd api
% sbt run
```
This will start the Bookworms API server on `localhost:8080`.

#### Run Node Frontend
```shell
% cd node
% npm install
```
This will download required Node modules. You should only have to do it once on a newly cloned
repository. To start Node:
```shell
% npm start
```
Point your browser `localhost:3000`.

