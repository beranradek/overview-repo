package org.xbery.overview.mongo;

import com.mongodb.client.MongoDatabase;
import org.xbery.overview.domain.Voucher;
import org.xbery.overview.mongo.repo.AbstractMongoRepository;
import org.xbery.overview.repo.VoucherRepository;

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
