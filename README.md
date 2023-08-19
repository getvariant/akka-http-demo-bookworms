# Bookworms Demo Application 
This application serves a dual purpose. It demonstrates 1) a basic web application built with Scala
and React.JS; and 2) instrumentation of an A/B test and a feature flag with 
[Variant CVM](https://getvariant.dev).

### Table of Contents

### 1. Downloading and Running

#### Clone this repository 
```shell
$ git clone git@github.com:getvariant/variant-demo-scala-akka.git
```

#### Create the Postgres Database
##### Postgres Docker container
Assuming you have docker installed (e.g. Docker Desktop for MacOS),
```shell
$ cd src/db
$ ./postgres-up.sh
$ ./schema.sh
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
$ sbt test
```

#### Deploy Experiment Schema to Variant Server
#### TODO: Containerize
Copy experiment schema to Variant server's `schemata` directory
```shell
cp bookworms.yaml <variant-server-dir>/schemata
```
Start Variant Server
```shell
$ <variant-server-dir>/bin/variant start
```
The server should come up and give you the following output:
```text
...
2023-08-16 14:46:39,483 INFO - c.v.s.schema.Schemata - [422] Deployed schema [bookworms] from file [...]
...
2023-08-16 14:46:39,488 INFO - c.v.s.impl.VariantServerImpl - [433] Variant CVM Server release 0.10.3 started on port [5377] in 1.048s
```
#### Run API Server
```shell
$ sbt run
```
This will start the Bookworms API server on `localhost:8080`.

#### Run Node Frontend
```shell
$ cd node
$ npm install
```
This will download required Node modules. You should only have to do it once on a newly cloned
repository. To start Node:
```shell
$ npm start
```
Point your browser `localhost:3000`.

