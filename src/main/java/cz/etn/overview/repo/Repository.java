/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Filter;
import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.ResultsWithOverview;

import java.util.List;
import java.util.Optional;

/**
 * Abstract repository.
 * @author Radek Beran
 */
public interface Repository<T, K, F extends Filter> {

	/**
	 * Creates new entity with given data.
	 * @param entity entity to create
	 * @param autogenerateKey true if entity key should be generated
	 * @return created entity which includes also possible generated key
	 */
	T create(T entity, boolean autogenerateKey);
	
	/**
	 * Updates entity with given data. Returns updated entity if entity was successfully updated, or empty result if entity was not found.
	 * @param entity entity to update
	 * @return data of entity after update
	 */
	Optional<T> update(T entity);

	// TODO RBe: Partial update method accepting function that returns updated entity.

	// TODO RBe: Support for update of entity by creating new immutable record with new version.
	
	/**
	 * Deletes entity with given id. Returns true if entity was successfully deleted.
	 * @param id
	 * @return
	 */
	boolean delete(K id);

	/**
	 * Deletes entity by given filter. Be aware to set the correct filter!
	 * @param filter
	 * @return count of deleted entities
     */
	int deleteByFilter(F filter);
	
	/**
	 * Finds entity by given id.
	 * @param id
	 * @return
	 */
	Optional<T> findById(K id);
	
	/**
	 * Returns results for given filtering, sorting and pagination settings.
	 * @param overview
	 * @return
	 */
	List<T> findByOverview(Overview<F> overview);

	/**
	 * Returns total count of results for given filter.
	 * @param filter
	 * @return
	 */
	int countByFilter(F filter);

	/**
	 * Returns results for given filtering and sorting settings.
	 * @param filter
	 * @param ordering
	 * @return
	 */
	default List<T> findByFilter(F filter, List<Order> ordering) {
		Overview<F> overview = new Overview<>(filter, ordering, null);
		return findByOverview(overview);
	}

	/**
	 * Returns all results for given filter.
	 * @param filter
	 * @return
	 */
	default List<T> findByFilter(F filter) {
		return findByFilter(filter, null);
	}
	
	/**
	 * Returns results along with overview (filtering, sorting and pagination) settings. Pagination settings is returned filled with total
	 * count of records - this count is loaded using separate count query.
	 * @param overview
	 * @return
	 */
	default ResultsWithOverview<T, F> findResultsWithOverview(Overview<F> overview) {
		List<T> results = findByOverview(overview);
		// Total count of records regardless of page limit
		int totalCount = countByFilter(overview.getFilter());
		return new ResultsWithOverview<>(results, overview.withPagination(overview.getPagination().withTotalCount(totalCount)));
	}
}
