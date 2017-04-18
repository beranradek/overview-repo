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
public class Overview<F> implements Serializable {
	private static final long serialVersionUID = 6433561386638154113L;

	/** Attributes for filtering. */
    private final F filter;
    
    /** Order specification. */
    private final List<Order> order;

    private final Pagination pagination;
    
    public static <F> Overview<F> empty() {
    	return new Overview<>(null, null, null);
    }

    public Overview(F filter, List<Order> order, Pagination pagination) {
        this.filter = filter;
        this.order = order;
        this.pagination = pagination;
    }
    
    /**
     * Returns new instance/copy of overview with given filter set.
     * @param f
     * @return
     */
    public Overview<F> withFilter(F f) {
    	return new Overview<>(f, this.order, this.pagination);
    }
    
    /**
     * Returns new instance/copy of overview with given ordering set.
     * @param ordering
     * @return
     */
    public Overview<F> withOrder(List<Order> ordering) {
    	return new Overview<>(this.filter, ordering, this.pagination);
    }
    
    /**
     * Returns new instance/copy of overview with empty pagination set.
     * @return
     */
    public Overview<F> withEmptyPagination() {
    	return new Overview<>(this.filter, this.order, null);
    }
    
    /**
     * Returns new instance/copy of overview with pagination set.
     * @param pag
     * @return
     */
    public Overview<F> withPagination(Pagination pag) {
    	return new Overview<>(this.filter, this.order, pag);
    }
    
    /**
     * Returns new instance/copy of overview with empty ordering set.
     * @return
     */
    public Overview<F> withEmptyOrdering() {
    	return new Overview<>(this.filter, new ArrayList<>(), this.pagination);
    }

    /**
     * Filter settings.
     * @return
     */
    public F getFilter() {
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
