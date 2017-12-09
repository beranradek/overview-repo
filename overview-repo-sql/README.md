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
		code = add(Attr.ofString(cls, "code").primary().get(e -> e.getCode()).set((e, a) -> e.setCode(a)).maxLength(20));
		creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
		discount_price = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).set((e, a) -> e.setDiscountPrice(a)).maxLength(10));
		valid_from = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()).set((e, a) -> e.setValidFrom(a)));
		valid_to = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()).set((e, a) -> e.setValidTo(a)));
		redemption_time = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()).set((e, a) -> e.setRedemptionTime(a)));
		reserved_by = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).set((e, a) -> e.setReservedBy(a)).maxLength(40));
		redeemed_by = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).set((e, a) -> e.setRedeemedBy(a)).maxLength(40));
	}

	public static VoucherMapper getInstance() {
		return INSTANCE;
	}

	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public Voucher createEntity() {
		return new Voucher();
	}

	@Override
	public List<Condition> composeFilterConditions(VoucherFilter filter) {
		return new ArrayList<>();
	}
}
```

Entity mapper describes:
  * name of database table to store the entities in,
  * persisted attributes of an entity and the way they are read from an entity and written to an existing entity (one-by-one so the updated entity is returned for each updated attribute), 
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

### One-to-one and one-to-many joins

Using the library, you can also implement queries returning joined entities. One-to-one
(left/right/inner) joins are fully and easily supported in functional way. Regarding one-to-many or many-to-one joins, you can simply use the same
implementation logic as for one-to-one joins, but for now, there is need to aggregate related entities together into one collection 
after the objects are fetched, until the fully convenient support of one-to-many joins is implemented directly in the library.
 
As an example, let's implement query that will fetch customers already joined with
their vouchers (a customer can have at least one voucher assigned - Customer class has a field: private Voucher voucher and
corresponding getter/setter).

Joins are implemented as compositions of two mappers of related entities to allow fetching of related entities 
as one composed (pageable) entity, with almost no effort. Only simple join specification is needed when composing the mappers.
This join specification can reside in CustomerRepositoryImpl class:

```java
    protected EntityMapper<Customer, CustomerFilter> getCustomerLeftJoinVoucherMapper() {
        return getEntityMapper().leftJoin(getVoucherMapper(),
            Condition.eqAttributes(getEntityMapper().id, getVoucherMapper().reserved_by), // ON condition
            (customer, voucher) -> { customer.setVoucher(voucher); return customer; }, // joined entity composition
            filter -> new Pair<>(filter, filter) // filter decomposition to first and second entity filters
        );
    }
```

It could be defined also in private static final field. Let's implement query that will use this join specification
to return already joined objects. For this, we will introduce overriden findByOverview method in CustomerRepositoryImpl, as our intention is
to always return customers with joined vouchers:

```java
    /**
     * Loads customers including joined (one or none) voucher.
     */
    @Override
    public List<Customer> findByOverview(Overview<CustomerFilter> overview) {
        Objects.requireNonNull(overview, "overview should be specified");
        // Resulting records are returned according to given filtering, ordering and pagination settings
        List<Customer> customers = findByOverview(overview, getCustomerLeftJoinVoucherMapper());
        return customers;
    }
```

This was easy once the reusable join specification is defined. Consider the case we want to fetch customers not only with related
vouchers but also with related supply points of customer. One customer can have one or many supply points. This one-to-many join
can now be achieved using the second database query that will fetch all supply points for our current page of customers (not unlike the lazy loading in Hibernate).
Once needed supply points are loaded, we will aggregate them into collections of related customers in the second phase.
Note that this approach is correct for usage with arbitrary pagination, filtering and ordering settings.

```java
    /**
     * Loads customers including joined voucher and supply points data.
     */
    @Override
    public List<Customer> findByOverview(Overview<CustomerFilter> overview) {
        Objects.requireNonNull(overview, "overview should be specified");
        // First load customers joined with vouchers
        List<Customer> customers = findByOverview(overview, getCustomerLeftJoinVoucherMapper());

        // Lazy loading of related supply points using one additional query
        // (if they would be joined with customers in one query, it would break pagination limit in returned SQL result set)
        List<Integer> customerIds = customers.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<SupplyPoint> supplyPoints = supplyPointRepo.findByCustomerIds(customerIds);

        // Append supply points to customers
        for (Customer customer : customers) {
            Iterable<SupplyPoint> supplyPointsOfCustomer = Iterables.filter(supplyPoints, sp -> customer.getId().equals(sp.getCustomerId()));
            customer.setSupplyPoints(Lists.newArrayList(supplyPointsOfCustomer));
        }

        return customers;
    }
```

You can discover a little extended example that is part of the library's tests for yourself:
 * [VoucherMapper](src/test/java/cz/etn/overview/repo/VoucherMapper.java)
 * [VoucherRepository](src/test/java/cz/etn/overview/repo/VoucherRepository.java)
 * [VoucherRepositoryImpl](src/test/java/cz/etn/overview/repo/VoucherRepositoryImpl.java)
 * Methods in repository that you will gain implemented (repo interface): [Repository](src/main/java/cz/etn/overview/repo/Repository.java)
 * Just add attributes and implement composeFilterConditions method in your entity mapper.

## Library maintenance

 * Running tests: gradlew :overview-repo-sql:clean :overview-repo-sql:test
 * Publishing artifact: gradlew :overview-repo-sql:clean :overview-repo-sql:uploadArchives
 * Releasing artifact: 
   * gradlew :overview-repo-sql:clean :overview-repo-sql:release
   * Increase version in gradle.properties of subproject to next version
