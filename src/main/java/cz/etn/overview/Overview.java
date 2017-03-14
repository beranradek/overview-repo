/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package cz.etn.overview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameters for overview of domain objects. Immutable class if filter implementation is immutable.
 * @author Radek Beran
 */
public class Overview<T extends Filter> implements Serializable {
	private static final long serialVersionUID = 6433561386638154113L;

	/** Attributes for filtering. */
    private final T filter;
    
    /** Order specification. */
    private final List<Order> order;

    private final Pagination pagination;
    
    public static <F extends Filter> Overview<F> empty() {
    	return new Overview<>(null, new ArrayList<>(), null);
    }

    public Overview(T filter, List<Order> order, Pagination pagination) {
        this.filter = filter;
        this.order = order;
        this.pagination = pagination;
    }
    
    /**
     * Returns new instance/copy of overview with given filter set.
     * @param f
     * @return
     */
    public Overview<T> withFilter(T f) {
    	return new Overview<>(f, this.order, this.pagination);
    }
    
    /**
     * Returns new instance/copy of overview with given ordering set.
     * @param ordering
     * @return
     */
    public Overview<T> withOrder(List<Order> ordering) {
    	return new Overview<>(this.filter, ordering, this.pagination);
    }
    
    /**
     * Returns new instance/copy of overview with empty pagination set.
     * @return
     */
    public Overview<T> withEmptyPagination() {
    	return new Overview<>(this.filter, this.order, null);
    }
    
    /**
     * Returns new instance/copy of overview with pagination set.
     * @param pag
     * @return
     */
    public Overview<T> withPagination(Pagination pag) {
    	return new Overview<>(this.filter, this.order, pag);
    }
    
    /**
     * Returns new instance/copy of overview with empty ordering set.
     * @return
     */
    public Overview<T> withEmptyOrdering() {
    	return new Overview<>(this.filter, new ArrayList<>(), this.pagination);
    }

    /**
     * Filter settings.
     * @return
     */
    public T getFilter() {
        return filter;
    }

    /**
     * Ordering settings.
     * @return
     */
    public List<Order> getOrder() {
		return order;
	}
    
    /**
     * Pagination settings.
     * @return
     */
    public Pagination getPagination() {
		return pagination;
	}

	@Override
	public String toString() {
		return "Overview [filter=" + filter + ", order=" + order + ", pagination=" + pagination + "]";
	}    
    
}
