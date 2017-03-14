/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base interface for implementing SQL entity mappers.
 * @author Radek Beran
 */
public interface AbstractEntityMapper<T> extends EntityMapper<T>, AttributeMapping<T> {
	
	T createEntity();
	
	AttributeMapping<T>[] getAttributeMappings();
	
	@Override
	default public String getPrimaryAttributeName() {
		AttributeMapping<T> keyAttr = null;
		for (AttributeMapping<T> con : getAttributeMappings()) {
			if (con.isPrimaryAttribute()) {
				keyAttr = con;
				break;
			}
		}
		return keyAttr != null ? keyAttr.getAttributeName() : null;
	}
	
	@Override
	default public List<String> getAttributeNames() {
		return Arrays.asList(getAttributeMappings()).stream().map(v -> v.getAttributeName()).collect(Collectors.toList());
	}
	
	@Override
	default public List<String> getAttributeNamesWithPrefix(String prefix, String aliasPrefix) {
		return Arrays.asList(getAttributeMappings()).stream()
			.map(v -> (prefix + AttributeMapping.ATTR_NAME_SEPARATOR + v.getAttributeName() + (aliasPrefix != null ? (" AS " + aliasPrefix + v.getAttributeName()) : "")))
			.collect(Collectors.toList());
	}
	
	@Override
	default public List<Object> getAttributeValues(T instance) {
		List<Object> attrValues = new ArrayList<>();
		for (AttributeMapping<T> fld : getAttributeMappings()) {
			attrValues.add(fld.getAttributeValue(instance));
		}
		return attrValues;
	}
	
	@Override
	default public Object getPrimaryAttributeValue(T instance) {
		Object keyAttrValue = null;
		for (AttributeMapping<T> con : getAttributeMappings()) {
			if (con.isPrimaryAttribute()) {
				keyAttrValue = con.getAttributeValue(instance);
				break;
			}
		}
		return keyAttrValue;
	}
	
	@Override
	default public T buildEntity(AttributeSource attributeSource, String aliasPrefix) {
		try {
			T instance = createEntity();
			for (AttributeMapping<T> fld : getAttributeMappings()) {
				String alias = null;
				if (aliasPrefix != null) {
					alias = aliasPrefix + fld.getAttributeName();
				}
				instance = fld.entityUpdatedWithAttributeValue(instance, attributeSource, alias);
			}
			return instance;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
