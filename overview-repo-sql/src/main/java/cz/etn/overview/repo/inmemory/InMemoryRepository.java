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
package cz.etn.overview.repo.inmemory;

import cz.etn.overview.Overview;
import cz.etn.overview.repo.AggType;
import cz.etn.overview.repo.Repository;
import cz.etn.overview.repo.RepositoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation intended to use in tests.
 * @author Radek Beran
 */
public abstract class InMemoryRepository<T, K, F> implements Repository<T, K, F> {
	
	protected Set<T> records = ConcurrentHashMap.newKeySet(); // derived concurrent hash set

	@Override
	public T create(T entity, boolean autogerateKey) {
		Optional<T> ent = findById(getEntityId(entity));
		if (ent.isPresent()) {
			throw new RepositoryException("Duplicate key " + getEntityId(entity));
		}
		if (autogerateKey) {
			entity = entityUpdatedWithId(entity, generateId());
		}
		records.add(entity);
		return entity;
	}

	@Override
	public Optional<T> update(T entity) {
		if (delete(getEntityId(entity))) {
			records.add(entity); // add updated entity
			return Optional.of(entity);
		}
		return Optional.empty();
	}

	@Override
	public boolean delete(K id) {
		if (id == null) return false;
		return records.removeIf(r -> id.equals(getEntityId(r)));
	}

	@Override
	public int deleteByFilter(F filter) {
		throw new UnsupportedOperationException("Filtering not supported by generic in-memory implementation");
	}

	@Override
	public Optional<T> findById(K id) {
		if (id == null) return Optional.empty();
		return records.stream().filter(r -> id.equals(getEntityId(r))).findFirst();
	}

	@Override
	public List<T> findByOverview(Overview<F> overview) {
		if (overview.getOrder() != null) {
			throw new UnsupportedOperationException("Ordering not supported by generic in-memory implementation");
		}
		if (overview.getFilter() != null) {
			throw new UnsupportedOperationException("Filtering not supported by generic in-memory implementation");
		}
		List<T> retList = null;
		if (overview.getPagination() != null) {
			retList = new ArrayList<>();
			Iterable<T> paged = records.stream().skip(overview.getPagination().getOffset()).limit(overview.getPagination().getLimit()).collect(Collectors.toList());
			for (T t : paged) {
				retList.add(t);
			}
		} else {
			retList = new ArrayList<>(records);
		}
		return retList;
	}

	@Override
	public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter) {
		throw new UnsupportedOperationException("Aggregation is not supported by generic in-memory implementation");
	}

	abstract protected T entityUpdatedWithId(T entity, K id);

	abstract protected K getEntityId(T entity);

	abstract protected K generateId();
	
}
