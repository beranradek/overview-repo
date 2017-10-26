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
package cz.etn.overview.repo;

import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.ResultsWithOverview;
import cz.etn.overview.common.Pair;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.EntityMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Repository interface.
 * @author Radek Beran
 * @param <T> type of entity
 * @param <K> type of entity key (composed keys are supported)
 * @param <F> type of filter
 */
public interface Repository<T, K, F> {

	EntityMapper<T, F> getEntityMapper();

	/**
	 * Creates new entity with given data.
	 * @param entity entity to create
	 * @param autogenerateKey true if entity key should be generated
	 * @return created entity which includes also possible generated key
	 */
	T create(T entity, boolean autogenerateKey);

	/**
	 * Creates all given entities.
	 * @param entities entities to create
	 * @param autogenerateKey true if entity key should be generated
	 * @return created entities that include also possible generated keys
	 */
	default List<T> createAll(List<T> entities, boolean autogenerateKey) {
		List<T> list = new ArrayList<>();
		if (entities != null) {
			for (T e : entities) {
				list.add(create(e, autogenerateKey));
			}
		}
		return list;
	}
	
	/**
	 * Updates entity with given data. Returns updated entity if entity was successfully updated, or empty result if entity was not found.
	 * @param entity entity to update
	 * @return data of entity after update
	 */
	Optional<T> update(T entity);

	/**
	 * Updates specified attributes of entity with given values.
	 * @param id primary key of entity
	 * @param attributesWithValues attributes to update with their new values
     * @return number of updated records
     */
	int update(K id, List<Pair<Attribute<T, ?>, Object>> attributesWithValues);

	/**
	 * Updates entity with given transformation function. Returns updated entity if entity was successfully updated, or empty result if entity was not found.
	 * Note that this method first loads the entity and then applies given update function to it and calls database update.
	 * Use {@link #update(Object, List)} to update more effectively selected attributes.
	 * @param id primary key of entity
	 * @param partialUpdate transformation function
	 * @return data of entity after update
	 */
	default Optional<T> update(K id, Function<T, T> partialUpdate) {
		Objects.requireNonNull(partialUpdate, "Update function should be specified");
		return findById(id).flatMap(e -> update(partialUpdate.apply(e)));
	}

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
	 * @param entityMapper
	 * @return
	 */
	<T, K, F> Optional<T> findById(K id, EntityMapper<T, F> entityMapper);

	/**
	 * Finds entity by given id.
	 * @param id
	 * @return
	 */
	default Optional<T> findById(K id) {
		return findById(id, getEntityMapper());
	}
	
	/**
	 * Returns results for given filtering, sorting and pagination settings.
	 * @param overview
	 * @return
	 */
	List<T> findByOverview(Overview<F> overview);

	<T, F> List<T> findByOverview(final Overview<F> overview, EntityMapper<T, F> entityMapper);

	/**
	 * Returns all results.
	 * @return
	 */
	default List<T> findAll() {
		return findAll(getEntityMapper());
	}

	/**
	 * Returns all results.
	 * @param entityMapper
	 * @param <T>
	 * @param <F>
     * @return
     */
	default <T, F> List<T> findAll(EntityMapper<T, F> entityMapper) {
		return findByOverview(new Overview<>(null, null, null), entityMapper);
	}

	/**
	 * Returns total count of results for given filter.
	 * @param filter
	 * @return
	 */
	default int countByFilter(F filter) {
		return countByFilter(filter, getEntityMapper());
	}

	/**
	 * Returns total count of results for given filter.
	 * @param filter
	 * @param entityMapper
	 * @return
	 */
	default <T, F> int countByFilter(F filter, EntityMapper<T, F> entityMapper) {
		return aggByFilter(AggType.COUNT, Integer.class, "*", filter, entityMapper);
	}

	/**
	 * Returns aggregated values of given attribute for given filter.
	 * @param aggType aggregation type
	 * @param resultClass
	 * @param attrName
	 * @param filter
	 * @param <R>
	 * @return
	 */
	default <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter) {
		return aggByFilter(aggType, resultClass, attrName, filter, getEntityMapper());
	}

	/**
	 * Returns aggregated values of given attribute for given filter.
	 * @param aggType aggregation type
	 * @param resultClass
	 * @param attrName
	 * @param filter
	 * @param entityMapper
	 * @param <R>
	 * @return
	 */
	<R, T, F> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter, EntityMapper<T, F> entityMapper);

	/**
	 * Returns results for given filtering and sorting settings.
	 * @param filter
	 * @param ordering
	 * @return
	 */
	default List<T> findByFilter(F filter, List<Order> ordering) {
		return findByFilter(filter, ordering, getEntityMapper());
	}

	/**
	 * Returns results for given filtering and sorting settings.
	 * @param filter
	 * @param ordering
	 * @param entityMapper
	 * @return
	 */
	default <T, F> List<T> findByFilter(F filter, List<Order> ordering, EntityMapper<T, F> entityMapper) {
		Overview<F> overview = new Overview<>(filter, ordering, null);
		return findByOverview(overview, entityMapper);
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
		return findResultsWithOverview(overview, getEntityMapper());
	}

	/**
	 * Returns results along with overview (filtering, sorting and pagination) settings. Pagination settings is returned filled with total
	 * count of records - this count is loaded using separate count query.
	 * @param overview
	 * @param entityMapper
	 * @return
	 */
	default <T, F> ResultsWithOverview<T, F> findResultsWithOverview(Overview<F> overview, EntityMapper<T, F> entityMapper) {
		List<T> results = findByOverview(overview, entityMapper);
		// Total count of records regardless of page limit
		int totalCount = countByFilter(overview.getFilter(), entityMapper);
		return new ResultsWithOverview<>(results, overview.withPagination(overview.getPagination().withTotalCount(totalCount)));
	}
}
