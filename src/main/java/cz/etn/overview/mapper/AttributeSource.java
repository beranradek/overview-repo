/*
 * Created on 14. 3. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

/**
 * Attribute source (e.g. stored attributes of an entity).
 * @author Radek Beran
 */
public interface AttributeSource {

	<A> A get(Class<A> cls, String attributeName);
}
