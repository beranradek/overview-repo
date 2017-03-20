/*
 * Created on 14. 3. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

/**
 * {@link ResultSet} attribute source.
 * @author Radek Beran
 */
public class ResultSetAttributeSource implements AttributeSource {

	private final ResultSet resultSet;
	
	public ResultSetAttributeSource(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}

	@Override
	public <A> A get(Class<A> cls, String attributeName) {
		Object value = null;
		if (cls.isAssignableFrom(Boolean.class)) {
			value = getBoolean(attributeName);
		} else if (cls.isAssignableFrom(Byte.class)) {
			value = getByte(attributeName);
		} else if (cls.isAssignableFrom(Integer.class)) {
			value = getInteger(attributeName);
		} else if (cls.isAssignableFrom(Long.class)) {
			value = getLong(attributeName);
		} else if (cls.isAssignableFrom(Float.class)) {
			value = getFloat(attributeName);
		} else if (cls.isAssignableFrom(Double.class)) {
			value = getDouble(attributeName);
		} else if (cls.isAssignableFrom(Date.class)) {
			value = getDate(attributeName);
		} else if (cls.isAssignableFrom(Instant.class)) {
			value = getInstant(attributeName);
		} else if (cls.isAssignableFrom(BigDecimal.class)) {
			value = getBigDecimal(attributeName);
		} else if (cls.isAssignableFrom(String.class)) {
			value = getString(attributeName);
		}
		return value != null ? cls.cast(value) : null;
	}

	protected Long getLong(String attributeName) {
		try {
			return resultSet.getLong(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Integer getInteger(String attributeName) {
		try {
			return resultSet.getInt(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected String getString(String attributeName) {
		try {
			return resultSet.getString(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Instant getInstant(String attributeName) {
		try {
			return sqlTimestampToInstant(resultSet.getTimestamp(attributeName));
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected BigDecimal getBigDecimal(String attributeName) {
		try {
			return resultSet.getBigDecimal(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Boolean getBoolean(String attributeName) {
		try {
			return resultSet.getBoolean(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Byte getByte(String attributeName) {
		try {
			return resultSet.getByte(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Date getDate(String attributeName) {
		try {
			return sqlDateToDate(resultSet.getDate(attributeName));
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Float getFloat(String attributeName) {
		try {
			return resultSet.getFloat(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Double getDouble(String attributeName) {
		try {
			return resultSet.getDouble(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	protected Instant sqlTimestampToInstant(java.sql.Timestamp date) {
		if (date == null) return null;
		return date.toInstant();
	}
	
	protected Date sqlDateToDate(java.sql.Date date) {
		if (date == null) return null;
		return new Date(date.getTime());
	}
}
