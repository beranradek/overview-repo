/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package cz.etn.overview;

import java.util.List;

/**
 * Results of a query along with applied overview settings.
 * Immutable object if objects in result list are immutable.
 * @author Radek Beran
 */
public class ResultsWithOverview<T, F extends Filter> {

	private final List<T> results;
	
	private final Overview<F> overview;
	
	public ResultsWithOverview(List<T> results, Overview<F> overview) {
		this.results = results;
		this.overview = overview;
	}

	/**
	 * Results of query.
	 * @return
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Overview with applied filter and pagination settings.
	 * If nested pagination object contains totalCount, it can be used to display pages navigation in UI. 
	 * @return
	 */
	public Overview<F> getOverview() {
		return overview;
	}
}
