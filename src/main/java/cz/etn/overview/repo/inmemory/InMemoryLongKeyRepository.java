/*
 * Created on 22. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo.inmemory;

import cz.etn.overview.Filter;
import cz.etn.overview.domain.Identifiable;
import cz.etn.overview.repo.Repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Basic abstract in-memory implementation of {@link Repository} with Long key.
 * @author Radek Beran
 */
public abstract class InMemoryLongKeyRepository<T extends Identifiable<Long>, F extends Filter> extends InMemoryRepository<T, Long, F> {

	private AtomicLong idSequence = new AtomicLong(1L);
	
	@Override
	public Long generateId() {
		return idSequence.getAndIncrement();
	}
}
