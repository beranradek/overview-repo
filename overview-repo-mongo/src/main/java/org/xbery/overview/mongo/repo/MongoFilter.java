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
package org.xbery.overview.mongo.repo;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple helper to create compound Mongo filters.
 *
 * <ul>
 * <li>Thread-safe: No.
 * <li>Lifetime: Short.
 * </ul>
 *
 * @author Radek Beran
 * @author Martin Kacer
 */
public class MongoFilter {
	
	private Bson oneFilter = null;
	private List<Bson> moreFilters = null;

	public MongoFilter add(Bson filter) {
		Objects.requireNonNull(filter, "filter must be specified");
		if (moreFilters == null) {
			if (oneFilter == null) {
				oneFilter = filter;
				return this;
			}
			moreFilters = new ArrayList<>();
			moreFilters.add(oneFilter);
			oneFilter = null;
		}
		moreFilters.add(filter);
		return this;
	}
	
	public MongoFilter add(Optional<Bson> filter) {
		filter.map(f -> add(f));
		return this;
	}
	
	public Optional<Bson> getFilter() {
		if (moreFilters == null) {
			return Optional.ofNullable(oneFilter);
		}
		return Optional.of(Filters.and(moreFilters));
	}
	
}
