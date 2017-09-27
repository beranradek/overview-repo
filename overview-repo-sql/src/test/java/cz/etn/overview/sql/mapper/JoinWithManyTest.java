package cz.etn.overview.sql.mapper;

import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.data.CustomerTestData;
import cz.etn.overview.data.SupplyPointTestData;
import cz.etn.overview.sql.repo.CustomerRepository;
import cz.etn.overview.sql.repo.CustomerRepositoryImpl;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * @author Radek Beran
 */
public class JoinWithManyTest {

    private final DataSource dataSource;
    private final CustomerTestData customerTestData;
    private final SupplyPointTestData spTestData;

    public JoinWithManyTest() {
        this.dataSource = new VoucherTestDb().createDataSource();
        this.customerTestData = new CustomerTestData();
        this.spTestData = new SupplyPointTestData();
    }

    @Test
    public void findCustomersLeftJoinManySupplyPoints() {
        CustomerRepository repo = createCustomerRepository();

    }

    protected CustomerRepositoryImpl createCustomerRepository() {
        return new CustomerRepositoryImpl(dataSource);
    }
}
