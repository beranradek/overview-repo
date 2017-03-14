# ETN-overview

Common classes for loading overview data - data with filtering, ordering and pagination settings.
Contains also full-featured abstract repository which can load objects using this filtering, ordering and pagination settings,
create, update and delete database entities. Library uses plain JDBC and has no other dependencies.
Various repository implementations can be introduced.

Basic repository implementation uses an entity mapper which serves all entity attributes metadata, so the repository is able to construct all queries
based on this metadata. Entity mappers can be possibly used also for other transformations of data objects.  

## Running tests

gradlew clean test

## Publishing artifact

gradlew clean uploadArchives

## Releasing artifact

* Increase version in gradle.properties
* gradlew clean release
