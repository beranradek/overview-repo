package cz.etn.overview.mapper;

import cz.etn.overview.Order;
import cz.etn.overview.common.Pair;
import cz.etn.overview.common.funs.CollectionFuns;

import java.util.List;
import java.util.function.Function;

/**
 * Auxiliary methods for defining join operations.
 * @author Radek Beran
 */
public class Joins {

    private static final Object OBJECT_FILTER = new Object();

    /**
     * Decomposition of filter: Filter can be used directly for left side of join operation.
     * @param <F>
     * @return
     */
    public static <F> Function<F, Pair<F, Object>> filterForLeftSide() {
        return filterForLeftSideWithRightFilter(OBJECT_FILTER);
    }

    /**
     * Decomposition of filter: Filter can be used directly for left side of join operation. Filter for right side is specified.
     * @param <F>
     * @return
     */
    public static <F, G> Function<F, Pair<F, G>> filterForLeftSideWithRightFilter(final G rightFilter) {
        return filter -> new Pair<>(filter, rightFilter);
    }

    /**
     * Decomposition of ordering: Ordering can be used directly for left side of join operation.
     */
    public static Function<List<Order>, Pair<List<Order>, List<Order>>> orderingForLeftSide = ordering -> new Pair<>(ordering, CollectionFuns.empty());

    private Joins() {
        throw new AssertionError("Use static members of this class.");
    }
}
