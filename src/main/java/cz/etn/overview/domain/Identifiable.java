/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

/**
 * Identifiable data entity.
 * @author Radek Beran
 */
public interface Identifiable<T> {

	T getId();
}
