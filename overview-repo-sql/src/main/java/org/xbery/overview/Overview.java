/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbery.overview;

import org.xbery.overview.common.funs.CollectionFuns;

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
    private final List<Order> ordering;

    private final Pagination pagination;

    /** Grouping specification. */
    private final List<Group> grouping;
    
    public static <F> Overview<F> empty() {
    	return new Overview<>(null, null, null, null);
    }

    public static <F> Overview<F> fromOrdering(Order order) {
        return Overview.<F>empty().withOrdering(order);
    }

    public static <F> Overview<F> fromOrdering(List<Order> ordering) {
        return Overview.<F>empty().withOrdering(ordering);
    }

    public Overview(F filter, List<Order> ordering, Pagination pagination, List<Group> grouping) {
        this.filter = filter;
        this.ordering = ordering;
        this.pagination = pagination;
        this.grouping = grouping;
    }

    public Overview(F filter, List<Order> ordering, Pagination pagination) {
        this(filter, ordering, pagination, null);
    }

    public Overview(F filter, List<Order> ordering) {
        this(filter, ordering, null);
    }

    public Overview(F filter) {
        this(filter, null);
    }
    
    /**
     * Returns new instance/copy of overview with given filter set.
     * @param f
     * @return
     */
    public Overview<F> withFilter(F f) {
    	return new Overview<>(f, this.ordering, this.pagination, this.grouping);
    }
    
    /**
     * Returns new instance/copy of overview with given ordering set.
     * @param ordering
     * @return
     */
    public Overview<F> withOrdering(List<Order> ordering) {
    	return new Overview<>(this.filter, ordering, this.pagination, this.grouping);
    }

    /**
     * Returns new instance/copy of overview with given ordering set.
     * @param order
     * @return
     */
    public Overview<F> withOrdering(Order order) {
        return withOrdering(CollectionFuns.list(order));
    }

    /**
     * Returns new instance/copy of overview with given grouping set.
     * @param grouping
     * @return
     */
    public Overview<F> withGrouping(List<Group> grouping) {
        return new Overview<>(this.filter, this.ordering, this.pagination, grouping);
    }

    /**
     * Returns new instance/copy of overview with given grouping set.
     * @param group
     * @return
     */
    public Overview<F> withGrouping(Group group) {
        return withGrouping(CollectionFuns.list(group));
    }
    
    /**
     * Returns new instance/copy of overview with empty pagination set.
     * @return
     */
    public Overview<F> withEmptyPagination() {
    	return new Overview<>(this.filter, this.ordering, null, this.grouping);
    }
    
    /**
     * Returns new instance/copy of overview with pagination set.
     * @param pag
     * @return
     */
    public Overview<F> withPagination(Pagination pag) {
    	return new Overview<>(this.filter, this.ordering, pag, this.grouping);
    }
    
    /**
     * Returns new instance/copy of overview with empty ordering set.
     * @return
     */
    public Overview<F> withEmptyOrdering() {
    	return new Overview<>(this.filter, new ArrayList<>(), this.pagination, this.grouping);
    }

    /**
     * Returns new instance/copy of overview with empty grouping set.
     * @return
     */
    public Overview<F> withEmptyGrouping() {
        return new Overview<>(this.filter, this.ordering, this.pagination, new ArrayList<>());
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
    public List<Order> getOrdering() {
		return ordering;
	}
    
    /**
     * Pagination settings.
     * @return
     */
    public Pagination getPagination() {
		return pagination;
	}

    /**
     * Grouping settings.
     * @return
     */
    public List<Group> getGrouping() {
        return grouping;
    }

    @Override
	public String toString() {
		return "Overview [filter=" + filter + ", ordering=[" + CollectionFuns.mkString(ordering, item -> item.toString(), ", ") + "], pagination=" + pagination + ", grouping=[" + CollectionFuns.mkString(grouping, item -> item.toString(), ", ") + "]]";
	}    
    
}
