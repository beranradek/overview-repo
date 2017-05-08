# Changelog

## [1.0.14] - 2017-x-y
- ?

## [1.0.13] - 2017-05-08
- maxLength property of Attribute, usage in MySqlSchemaBuilder.
- update method for selected attributes.

## [1.0.12] - 2017-04-23
- General agg method in repository.
- Inner/left/right outer joins implemented as compositions of mappers to allow fetching of related entities as one composed (pageable) entity, with almost no effort (only simple join specification is needed when composing the mappers).

## [1.0.11] - 2017-04-06
- Method for partial update.

## [1.0.10] - 2017-04-05
- Support for composite primary keys.

## [1.0.9] - 2017-04-04
- Simple builder of base of MySQL create table command.

## [1.0.8] - 2017-04-03
- sumByFilter method in repository.

## [1.0.7] - 2017-04-03
- Additional attribute construction methods.

## [1.0.6] - 2017-04-03
- Common filter conditions.
- nameFull property of attributes containing entity table prefix.

## [1.0.5] - 2017-03-31
- deleteByFilter implemented in InMemoryRepository

## [1.0.4] - 2017-03-31
- DynamicEntityMapper without static methods (other classes can be injected to implement its logic).
- Removed redundant countByOverview, added deleteByFilter.

## [1.0.3] - 2017-03-24
- Default implementation of createInternal and updateInternal in AbstractRepositoryImpl.
- DynamicEntityMapper and Attr as Attribute builder/attribute API entry point.
- Tests

## [1.0.2] - 2017-03-14
- Simplified AttributeMapping contract

## [1.0.1] - 2017-03-14
- Fixed library group

## [1.0.0] - 2017-03-14
- Initial version
