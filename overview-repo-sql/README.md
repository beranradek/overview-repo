# overview-repo-sql

JDBC implementation of rich generic SQL repository with overview (filtering, grouping, ordering and pagination) settings, 
including support for one-to-one or one-to-many joins which can be declared in simple functional manner.

Joins are implemented using composition of entity mappers and work well with provided pagination settings. 
Only simple reusable join specification is needed when composing the mappers.

## Additional features
  * "CREATE TABLE" SQL string can be generated from an entity mapper as an useful start to write DB schema (for now, only MySQL syntax is supported).

## Quick start example

Let's assume we have simple database with customers and their vouchers. Voucher can be assigned to a customer as a discount for services 
taken on one or more supply points of the customer. Customer can have at most one voucher assigned.
There can be customers without any discount - without any voucher.

### Voucher entity

First, we will create a voucher entity:

```java
/**
 * Discount voucher.
 */
public class Voucher {

	private Instant creationTime;
	private String code;
	private BigDecimal discountPrice;
	private Instant validFrom;
	private Instant validTo;
	private Instant redemptionTime;
	private String redeemedBy;
	private String reservedBy;
	
	// just getters and setters...
}
```

This POJO can be used by the library as it is, note that there is no one requirement on implementing 
any interface or extending any base class or using any annotations. Naturally, the entity should have some accessors (getters) so its data
can be read and there is one implicit requirement that attributes (fields) of entity must be settable individually per
each attribute. This can be simply achieved by creating setters, or using immutable entity with copy methods
for each attribute (returning new updated entity) or using a builder of immutable entity.

### Entity mapper

For storing voucher entities in database, we must define an entity mapper which maps all persisted fields of entity to
database attributes:

```java
/**
 * Mapping of voucher attributes to database attributes.
 */
public class VoucherMapper extends DynamicEntityMapper<Voucher, VoucherFilter> {

    /** Mapped entity class. */
    private static final Class<Voucher> cls = Voucher.class;
    private static final String DB_TABLE_NAME = "voucher";
    private static final VoucherMapper INSTANCE = new VoucherMapper();
    
    public final Attribute<Voucher, String> code;
    public final Attribute<Voucher, Instant> creation_time;
    public final Attribute<Voucher, BigDecimal> discount_price;
    public final Attribute<Voucher, Instant> valid_from;
    public final Attribute<Voucher, Instant> valid_to;
    public final Attribute<Voucher, Instant> redemption_time;
    public final Attribute<Voucher, String> reserved_by;
    public final Attribute<Voucher, String> redeemed_by;
	    
    private VoucherMapper() {
        code = add(Attr.ofString(cls, "code").primary().get(e -> e.getCode()).maxLength(20));
        creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()));
        discount_price = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).maxLength(10));
        valid_from = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()));
        valid_to = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()));
        redemption_time = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()));
        reserved_by = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).maxLength(40));
        redeemed_by = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).maxLength(40));
    }

    public static VoucherMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTableName() {
        return DB_TABLE_NAME;
    }

    @Override
    public Voucher createEntity(AttributeSource attributeSource, List<Attribute<Voucher, ?>> attributes, String aliasPrefix) {
        Voucher voucher = new Voucher();
        voucher.setCode(code.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setCreationTime(creation_time.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setDiscountPrice(discount_price.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setValidFrom(valid_from.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setValidTo(valid_to.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setRedemptionTime(redemption_time.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setReservedBy(reserved_by.getValueFromSource(attributeSource, aliasPrefix));
        voucher.setRedeemedBy(redeemed_by.getValueFromSource(attributeSource, aliasPrefix));
        return voucher;
    }

    @Override
    public List<Condition> composeFilterConditions(VoucherFilter filter) {
        return new ArrayList<>();
    }
}
```

Entity mapper describes:
  * name of database table to store the entities in,
  * persisted attributes of an entity and the way they are read from an entity, 
  * how a new entity can be created,
  * how filtering conditions are constructed from a filter object if there are any filtering requirements (we will cover filtering criteria later in a moment; basically, the filter object can be anything suitable to you).

 ```java
public class VoucherFilter {
    // empty for now
}
```

### Voucher repository

After we have covered all the necessary mapping logic in one simple refactorable entity mapper, we can create and use a repository for storing and querying entities. 
This will be really easy as you will see. Let's create an extendable interface and
implementation for the voucher repository:
 
```java
public interface VoucherRepository extends Repository<Voucher, String, VoucherFilter> {
	// nothing new here
}

```

```java
public class VoucherRepositoryImpl extends SqlRepository<Voucher, String, VoucherFilter> implements VoucherRepository {

	public VoucherRepositoryImpl(DataSource dataSource) {
		super(dataSource, VoucherMapper.getInstance());
	}
}
```

The repository is connected to a database via standard javax.sql.DataSource. Now, we can extend a repository little bit and create some methods for storing and accessing database entities.
But wait, what we can gain from a Repository/SqlRepository we have already extended?

There is already plenty of methods that are fully implemented for you: 
  * create, update/partial update, delete, delete by filter,
  * find by id, find by filter, find by overview (filtering, grouping, ordering and pagination) settings, find all,
  * count by filter, aggregate (count, sum, min, max, avg) by filter and grouping to obtain one aggregated result.
  
This is probably all you will need for various basic use cases! Also 1:1 and 1:n joins are prepared for you. 
If you have some special requirements, you can implement additional queries using many protected methods that are present in superclass, 
but our implementation is now more than complete. Let's hope this will be really easily maintainable and refactorable repository code for you!
There is not so much to maintain at all :-) Mainly the mapper implementation. But note you can reuse
it probably as a general attribute source also for some other business logic (e.g. exports, generic forms etc.).

### One-to-one joins
 
As an example, let's implement query that will fetch customers already joined with
their vouchers (a customer can have one or none voucher assigned). Customer class has a private Voucher field and
corresponding getter/setter.

Joins are implemented as compositions of two mappers of related entities. Only simple reusable join specification 
is needed when composing the mappers. This join specification can reside in CustomerRepositoryImpl class:

```java
    final EntityMapper<Customer, CustomerFilter> joinVoucherMapper = getEntityMapper().leftJoin(getVoucherMapper())
        .on(getEntityMapper().id, getVoucherMapper().reserved_by) // ON condition
        .composeEntity((customer, voucher) -> { customer.setVoucher(voucher); return customer; }) // joined entity composition
        .decomposeFilter(Decompose.filterToIdenticalAnd(new VoucherFilter())) // filter decomposition to first and second entity filters
        .build();
```

Join mapper could be defined also in private static final field of our customer repository. A query that will use this join specification
to return already joined objects looks like this:

```java
List<Customer> customers = findByOverview(overview, joinVoucherMapper);
```

### One-to-many joins

Consider the case we want to fetch customers with related supply points of customer. One customer can have many supply points. 
If a pagination settings is specified, the library will automatically fetch related supply points using a second database query, 
so the pagination is correctly applied for the primary records - customers. 
This is not unlike the lazy loading in Hibernate. If the pagination settings is not specified, the library will perform only one join query.

The good news are, one-to-many join looks very similar to one-to-one join, only the composeEntity method is replaced by composeEntityWithMany.
This join specification can reside in CustomerRepositoryImpl class: 

```java
    final EntityMapper<Customer, CustomerFilter> joinSupplyPointsMapper = getEntityMapper().leftJoin(getSupplyPointMapper())
        .on(getEntityMapper().id, getSupplyPointMapper().customer_id) // ON condition
        .composeEntityWithMany((customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; })
        .decomposeFilter(Decompose.filterToIdenticalAnd(new SupplyPointFilter())) // filter decomposition to first and second entity filters
        .decomposeOrdering(Decompose.orderingToIdenticalAnd(new Order(getSupplyPointMapper().code))) // ordering decomposition to first and second entity ordering
        .build();
```

A query that will use this join specification to return customers already joined with supply points looks like this (using CustomerRepository):

```java
List<Customer> customers = findByOverview(overview, joinSupplyPointsMapper);
```

Or with counting total records:

```java
ResultsWithOverview<Customer, CustomerFilter> resultsWithOverview = findResultsWithOverview(overview, joinSupplyPointsMapper);
List<Customer> customers = resultsWithOverview.getResults();
Integer totalCount = resultsWithOverview.getOverview().getPagination().getTotalCount();
```

### Combine more joins together

What if we want to load customers with both joined vouchers (one-to-one join) and supply points (one-to-many join)?
We can compose already prepared joinVoucherMapper with the supply points mapper:

```java
    final EntityMapper<Customer, CustomerFilter> joinVoucherJoinSupplyPointsMapper = joinVoucherMapper.leftJoin(getSupplyPointMapper())
        .on(getEntityMapper().id, getSupplyPointMapper().customer_id)
        .composeEntityWithMany((customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; })
        .decomposeFilter(Decompose.filterToIdenticalAnd(new SupplyPointFilter()))
        .decomposeOrdering(Decompose.orderingToIdenticalAnd(new Order(getSupplyPointMapper().code)))
        .build();
```

## Where to go next?

You can discover a little extended example that is part of the library's tests for yourself:
 * [VoucherMapper](src/test/java/org/xbery/overview/sql/repo/VoucherMapper.java)
 * [VoucherRepository](src/test/java/org/xbery/overview/sql/repo/VoucherRepository.java)
 * [VoucherRepositoryImpl](src/test/java/org/xbery/overview/sql/repo/VoucherRepositoryImpl.java)
 * Methods in repository that you will gain implemented (repo interface): [Repository](src/main/java/org/xbery/overview/repo/Repository.java)
 * Just add attributes and implement composeFilterConditions method in your entity mapper.

## Library maintenance

### Release

 * Fill in CHANGELOG.md.
 * Create tag REL-x.y.z (with version to release) and push it.
 * Just run: gradlew :overview-repo-sql:clean :overview-repo-sql:test :overview-repo-sql:assemble to see all is ok and ready for release.
 * Publish to Maven Central: gradlew :overview-repo-sql:clean :overview-repo-sql:uploadArchives 
 * Login to https://oss.sonatype.org/, "Close" the Staging repository for library, "Refresh" it and "Release" it.

See http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central and http://central.sonatype.org/pages/gradle.html for details.  
