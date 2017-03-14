/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package cz.etn.overview;

import java.io.Serializable;

/**
 * Pagination settings. Immutable class.
 * @author Radek Beran
 */
public final class Pagination implements Serializable {
	private static final long serialVersionUID = 1061354171426108893L;

	private final int offset;
	
	private final int limit;
	
	/** TotalCount is null in case records are not loaded yet. */
	private final Integer totalCount;
	
	public Pagination(int offset, int limit, Integer totalCount) {
		this.offset = offset;
		this.limit = limit;
		this.totalCount = totalCount;
	}
	
	public Pagination(int offset, int limit) {
		this(offset, limit, null);
	}
	
	/**
	 * Returns new instance/copy of pagination with total count set.
	 * @param count
	 * @return
	 */
	public Pagination withTotalCount(Integer count) {
    	return new Pagination(this.offset, this.limit, count);
    }
	
	/**
	 * Returns new instance/copy of pagination with offset set.
	 * @param count
	 * @return
	 */
	public Pagination withOffset(int offset) {
    	return new Pagination(offset, this.limit, this.totalCount);
    }

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public Integer getTotalCount() {
		return totalCount;
	}
	
	public int getNextOffset() {
		return offset + limit;
	}
	
	public int getPreviousOffset() {
		int prevOffset = offset - limit;
		if (prevOffset < 0) {
			prevOffset = 0;
		}
		return prevOffset;
	}
	
	/**
	 * Returns true if this pagination represents first page.
	 * @return
	 */
	public boolean isFirstPage() {
		return offset <= 0;
	}
	
	/**
	 * Returns true if this pagination represents last page, or false if there are next pages available.
	 * @return
	 */
	public boolean isLastPage() {
		boolean next = false;
		if (totalCount != null) {
			next = getNextOffset() < totalCount.intValue();  
		}
		return !next;
	}
	
	@Override
	public String toString() {
		return "Pagination [offset=" + offset + ", limit=" + limit + ", totalCount=" + totalCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + limit;
		result = prime * result + offset;
		result = prime * result + ((totalCount == null) ? 0 : totalCount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Pagination other = (Pagination)obj;
		if (limit != other.limit) return false;
		if (offset != other.offset) return false;
		if (totalCount == null) {
			if (other.totalCount != null) return false;
		} else if (!totalCount.equals(other.totalCount)) return false;
		return true;
	}
}
