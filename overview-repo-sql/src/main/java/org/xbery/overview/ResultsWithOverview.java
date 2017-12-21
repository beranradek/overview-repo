/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbery.overview;

import java.util.List;

/**
 * Results of a query along with applied overview settings.
 * Immutable object if objects in result list are immutable.
 * @author Radek Beran
 */
public class ResultsWithOverview<T, F> {

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
