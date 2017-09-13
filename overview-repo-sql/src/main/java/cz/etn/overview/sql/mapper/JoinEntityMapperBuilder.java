package cz.etn.overview.sql.mapper;

import cz.etn.overview.Order;
import cz.etn.overview.common.Pair;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.filter.EqAttributesCondition;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.Cardinality;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.mapper.JoinType;
import cz.etn.overview.repo.Conditions;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Builder for {@link JoinEntityMapper}.
 * @author Radek Beran
 */
public class JoinEntityMapperBuilder<T, F, U, G, V, H, O> {

    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;
    private final JoinType joinType;

    private Cardinality cardinality;
    private EqAttributesCondition<T, U, O, O> joinCondition;
    private List<Condition> additionalOnConditions;
    private BiFunction<T, U, V> composeEntity;
    private BiFunction<T, List<U>, V> composeEntityWithMany;
    private Function<H, Pair<F, G>> decomposeFilter;
    private Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder;

    JoinEntityMapperBuilder(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, JoinType joinType) {
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.joinType = joinType;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute, List<Condition> additionalOnConditions) {
        this.joinCondition = Conditions.eqAttributes(firstAttribute, secondAttribute);
        this.additionalOnConditions = additionalOnConditions;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute) {
        return on(firstAttribute, secondAttribute, CollectionFuns.empty());
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> composeEntity(BiFunction<T, U, V> composeEntity) {
        this.composeEntity = composeEntity;
        this.cardinality = Cardinality.ONE;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> composeEntityWithMany(BiFunction<T, List<U>, V> composeEntityWithMany) {
        this.composeEntityWithMany = composeEntityWithMany;
        this.cardinality = Cardinality.MANY;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> decomposeFilter(Function<H, Pair<F, G>> decomposeFilter) {
        this.decomposeFilter = decomposeFilter;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> decomposeOrder(Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder) {
        this.decomposeOrder = decomposeOrder;
        return this;
    }

    public JoinEntityMapper<T, F, U, G, V, H, O> build() {
        // TODO: Checks on required fields
        return new JoinEntityMapper<T, F, U, G, V, H, O>(firstMapper, secondMapper, joinType, cardinality, joinCondition, additionalOnConditions, composeEntity, composeEntityWithMany, decomposeFilter, decomposeOrder);
    }

}
