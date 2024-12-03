#!/bin/bash
#
# Build server extension API standard library.
#

version=1.0.0
root_dir=$(cd $(dirname $0)/..; pwd)

cd $root_dir

# Build and package.
mvn clean package

# Un-package the JAR and add the /db directory to it. /db directory contains DB related SQL and shell
# scripts that are used by tests.
jar -uf target/variant-spi-stdlib-${version}.jar $(find db)

# Copy the new build into the server's distribution directory. Assuming the location of the variant
# local repo.
cp target/variant-spi-stdlib-${version}.jar ../variant/SERVER/src/universal/spi
cp target/variant-spi-stdlib-${version}.jar ../variant/SERVER/lib