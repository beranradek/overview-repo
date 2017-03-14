/*
 * Created on 14. 3. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

/**
 * Attribute source (e.g. stored attributes of an entity).
 * @author Radek Beran
 */
public interface AttributeSource {

	Long getLong(String attributeName);
	
	Integer getInteger(String attributeName);
	
	String getString(String attributeName);
	
	Instant getInstant(String attributeName);
	
	BigDecimal getBigDecimal(String attributeName);
	
	Boolean getBoolean(String attributeName);
	
	Byte getByte(String attributeName);
	
	Date getDate(String attributeName);
	
	Float getFloat(String attributeName);
}
