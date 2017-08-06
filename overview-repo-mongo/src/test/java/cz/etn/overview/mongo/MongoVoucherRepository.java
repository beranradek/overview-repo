package cz.etn.overview.mongo;

import com.mongodb.client.MongoDatabase;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mongo.repo.AbstractMongoRepository;
import cz.etn.overview.repo.VoucherRepository;

/**
 * Default implementation of {@link VoucherRepository}.
 * @author Radek Beran
 */
public class MongoVoucherRepository extends AbstractMongoRepository<Voucher, String, Object> implements VoucherRepository {

    private final MongoDatabase database;

    public MongoVoucherRepository(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public MongoVoucherMapper getEntityMapper() {
        return MongoVoucherMapper.getInstance();
    }

    @Override
    public MongoDatabase getDatabase() {
        return database;
    }
}
