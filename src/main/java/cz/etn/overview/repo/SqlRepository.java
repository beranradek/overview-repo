package cz.etn.overview.repo;

import cz.etn.overview.domain.Identifiable;
import cz.etn.overview.mapper.EntityMapper;

import javax.sql.DataSource;

/**
 * Full implementation of SQL repository using {@link EntityMapper}.
 * @author Radek Beran
 */
public class SqlRepository<T extends Identifiable<K>, K, F> extends AbstractRepository<T, K, F> {

    private final DataSource dataSource;

    private final EntityMapper<T, F> entityMapper;


    public SqlRepository(DataSource dataSource, EntityMapper<T, F> entityMapper) {
        this.dataSource = dataSource;
        this.entityMapper = entityMapper;
    }


    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected EntityMapper<T, F> getEntityMapper() {
        return entityMapper;
    }
}
