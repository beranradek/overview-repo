# ETN-overview

The library ensures **loading of data with filtering, ordering and pagination settings**.
It contains **full-featured repository** which can load objects using this filtering, ordering and pagination settings,
create, update and delete database entities, only with metadata provided by an entity mapper. Library uses plain JDBC and has no dependencies except the logging api.
Various repository implementations can be introduced.

Basic repository implementation uses an **entity mapper** which serves all entity attributes metadata, so the repository is able to construct all the queries
based on this metadata. Entity mappers can possibly be used also for other transformations of data objects, outside the scope of this library.  

## Quick start example

TODO

For now, just see:
 * [VoucherMapper](https://git.etnetera.cz/etn-libs/etn-overview/blob/master/src/test/java/cz/etn/overview/repo/VoucherMapper.java)
 * [VoucherRepository](https://git.etnetera.cz/etn-libs/etn-overview/blob/master/src/test/java/cz/etn/overview/repo/VoucherRepository.java)
 * [VoucherRepositoryImpl](https://git.etnetera.cz/etn-libs/etn-overview/blob/master/src/test/java/cz/etn/overview/repo/VoucherRepositoryImpl.java)
 * Methods in repository that you will gain implemented (repo interface): [Repository](https://git.etnetera.cz/etn-libs/etn-overview/blob/master/src/main/java/cz/etn/overview/repo/Repository.java)

## Planned features
 * Composition of mappers (joins) to allow fetching of 1:0..1 or 1:1 related entities to one composed pageable entity, with almost no effort from user (can be automatized).
 * Storage of new immutable versions of entity instead of updates, using version flag on an attribute.
 * "CREATE TABLE" commands generated from an entity mapper.
 * Cooperation with another data processing libraries that can leverage general, yet simple definition of attributes (generated forms, CSV/XLS exports from database, ...).

## Running tests

gradlew clean test

## Publishing artifact

gradlew clean uploadArchives

## Releasing artifact

* Increase version in gradle.properties
* gradlew clean release
