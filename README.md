# overview-repository

The library ensures **loading of data with filtering, ordering and pagination settings**.
It contains **full-featured repository** which can load objects using this filtering, ordering and pagination settings,
create, update, delete and join database entities, only with metadata provided by an entity mapper. Library uses plain JDBC and has no dependencies except the logging api.
Various repository implementations can be introduced.

Basic repository implementation uses an **entity mapper** which serves all entity attributes metadata, so the repository is able to construct all the queries
based on this metadata. Entity mappers can possibly be used also for other transformations of data objects, outside the scope of this library.  

## Implemented features
 * Inner/left/right outer joins implemented as compositions of mappers to allow fetching of related entities as one composed (pageable) entity, with almost no effort (only simple join specification is needed when composing the mappers).
 * Partial update only of some subset of attributes (defined using specified attributes with values or using an update lambda).
 * Support for composite primary keys.
 * "CREATE TABLE" SQL string generated from an entity mapper as an useful start to write DB schema (for now, MySQL only).
 * Optional maximum length property on attributes.

## Quick start example

For now, just see:
 * [VoucherMapper](blob/master/src/test/java/cz/etn/overview/repo/VoucherMapper.java)
 * [VoucherRepository](blob/master/src/test/java/cz/etn/overview/repo/VoucherRepository.java)
 * [VoucherRepositoryImpl](blob/master/src/test/java/cz/etn/overview/repo/VoucherRepositoryImpl.java)
 * Methods in repository that you will gain implemented (repo interface): [Repository](blob/master/src/main/java/cz/etn/overview/repo/Repository.java)
 * Just add attributes and implement composeFilterConditions method in your entity mapper.

## Planned features
 * Storage of new immutable versions of entity instead of updates, using supported version flag on an attribute (for now, this can be implemented in a custom way using for e.g. (entityId, version) composite key).
 * Minimal fetch levels configurable on attributes so not every attribute must be loaded (e.g. info, detail).
 * Derived library for Mongo DB with the same repository interface.
 * Cooperation with another data processing libraries that can leverage general, yet simple definition of attributes (generated forms, CSV/XLS exports from database, ...).

## Running tests

gradlew clean test

## Publishing artifact

gradlew clean uploadArchives

## Releasing artifact

* gradlew clean release
* Increase version in gradle.properties to next version
