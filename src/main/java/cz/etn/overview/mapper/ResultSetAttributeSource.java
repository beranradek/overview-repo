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
	public Long getLong(String attributeName) {
		try {
			return resultSet.getLong(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Integer getInteger(String attributeName) {
		try {
			return resultSet.getInt(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getString(String attributeName) {
		try {
			return resultSet.getString(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Instant getInstant(String attributeName) {
		try {
			return sqlTimestampToInstant(resultSet.getTimestamp(attributeName));
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String attributeName) {
		try {
			return resultSet.getBigDecimal(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Boolean getBoolean(String attributeName) {
		try {
			return resultSet.getBoolean(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Byte getByte(String attributeName) {
		try {
			return resultSet.getByte(attributeName);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Date getDate(String attributeName) {
		try {
			return sqlDateToDate(resultSet.getDate(attributeName));
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Float getFloat(String attributeName) {
		try {
			return resultSet.getFloat(attributeName);
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
