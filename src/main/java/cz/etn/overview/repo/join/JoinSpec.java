package cz.etn.overview.repo.join;

import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.repo.FilterCondition;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Specification of entity join operation.
 * @type T type of first entity
 * @type U type of second entity
 * @type V type of entity that is result of join operation (can be the same as T or U)
 * @author Radek Beran
 */
public class JoinSpec<T, U, R> {

    private final EntityMapper<T> leftMapper;
    private final JoinType joinType;
    private final EntityMapper<U> rightMapper;
    private final List<FilterCondition> onConditions;
    private final BiFunction<T, U, R> resultingEntity;

    public JoinSpec(EntityMapper<T> leftMapper, JoinType joinType, EntityMapper<U> rightMapper, List<FilterCondition> onConditions, BiFunction<T, U, R> resultingEntity) {
        this.leftMapper = leftMapper;
        this.joinType = joinType;
        this.rightMapper = rightMapper;
        this.onConditions = onConditions;
        this.resultingEntity = resultingEntity;
    }

    public EntityMapper<T> getLeftMapper() {
        return leftMapper;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public EntityMapper<U> getRightMapper() {
        return rightMapper;
    }

    public List<FilterCondition> getOnConditions() {
        return onConditions;
    }

    public BiFunction<T, U, R> getResultingEntity() {
        return resultingEntity;
    }
}
