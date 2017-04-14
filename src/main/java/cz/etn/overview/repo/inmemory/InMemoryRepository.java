/*
 * Created on 14. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo.inmemory;

import cz.etn.overview.Filter;
import cz.etn.overview.Overview;
import cz.etn.overview.domain.Identifiable;
import cz.etn.overview.repo.AggType;
import cz.etn.overview.repo.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation intended to use in tests.
 * @author Radek Beran
 */
public abstract class InMemoryRepository<T extends Identifiable<K>, K, F extends Filter> implements Repository<T, K, F> {
	
	protected Set<T> records = ConcurrentHashMap.newKeySet(); // derived concurrent hash set
	
	public abstract K generateId();

	@Override
	public T create(T entity, boolean autogerateKey) {
		Optional<T> ent = findById(entity.getId());
		if (ent.isPresent()) {
			throw new RuntimeException("Duplicate key " + entity.getId());
		}
		if (autogerateKey) {
			entity = entityUpdatedWithId(entity, generateId());
		}
		records.add(entity);
		return entity;
	}

	@Override
	public Optional<T> update(T entity) {
		if (delete(entity.getId())) {
			records.add(entity); // add updated entity
			return Optional.of(entity);
		}
		return Optional.empty();
	}

	@Override
	public boolean delete(K id) {
		if (id == null) return false;
		return records.removeIf(r -> id.equals(r.getId()));
	}

	@Override
	public int deleteByFilter(F filter) {
		throw new UnsupportedOperationException("Filtering not supported by generic in-memory implementation");
	}

	@Override
	public Optional<T> findById(K id) {
		if (id == null) return Optional.empty();
		return records.stream().filter(r -> id.equals(r.getId())).findFirst();
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
	
}
