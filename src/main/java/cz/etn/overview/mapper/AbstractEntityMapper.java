/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import cz.etn.overview.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base interface for implementing entity mappers.
 * @author Radek Beran
 */
public interface AbstractEntityMapper<T, F extends Filter> extends EntityMapper<T, F> {
	
	T createEntity();
	
	Attribute<T, ?>[] getAttributes();

	default List<Attribute<T, ?>> getPrimaryAttributes() {
		List<Attribute<T, ?>> primAttrs = new ArrayList<>();
		Attribute<T, ?>[] attributes = getAttributes();
		if (attributes != null) {
			for (Attribute<T, ?> attr : attributes) {
				if (attr.isPrimary()) {
					primAttrs.add(attr);
				}
			}
		}
		return primAttrs;
	}
	
	@Override
	default List<String> getPrimaryAttributeNames() {
		return getPrimaryAttributes().stream().map(attr -> attr.getName()).collect(Collectors.toList());
	}

	@Override
	default List<Object> getPrimaryAttributeValues(T entity) {
		return getPrimaryAttributes().stream().map(attr -> attr.getValue(entity)).collect(Collectors.toList());
	}
	
	@Override
	default List<String> getAttributeNames() {
		return Arrays.asList(getAttributes()).stream().map(v -> v.getName()).collect(Collectors.toList());
	}

	@Override
	default List<String> getAttributeNamesFullAliased() {
		String prefix = getDataSet();
		String aliasPrefix = getAliasPrefix();
		return Arrays.asList(getAttributes()).stream()
			.map(v -> (prefix + "." + v.getName() + (aliasPrefix != null ? (" AS " + aliasPrefix + v.getName()) : "")))
			.collect(Collectors.toList());
	}
	
	@Override
	default List<Object> getAttributeValues(T instance) {
		List<Object> attrValues = new ArrayList<>();
		for (Attribute<T, ?> fld : getAttributes()) {
			attrValues.add(fld.getValue(instance));
		}
		return attrValues;
	}
	
	@Override
	default T buildEntity(AttributeSource attributeSource, String aliasPrefix) {
		try {
			T instance = createEntity();
			for (Attribute<T, ?> attr : getAttributes()) {
				String alias = null;
				if (aliasPrefix != null) {
					alias = aliasPrefix + attr.getName();
				}
				instance = attr.entityWithAttribute(instance, attributeSource, attr.getName(alias));
			}
			return instance;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
