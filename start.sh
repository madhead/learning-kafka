#!/usr/bin/env sh

./gradlew clean install shadowJar

rm -rf kafka/connectors
rm -rf kafka/libs
mkdir -p kafka/connectors
mkdir -p kafka/libs

cp kafka/wikipedia-source-connector/build/libs/*.jar kafka/connectors
cp kafka/postgres-sink-connector/kafka-connect-jdbc-10.5.1.jar kafka/connectors
cp kafka/postgres-sink-connector/postgresql-42.3.3.jar kafka/libs

docker-compose down -v
docker-compose up -d --always-recreate-deps --force-recreate --build --remove-orphans --renew-anon-volumes
