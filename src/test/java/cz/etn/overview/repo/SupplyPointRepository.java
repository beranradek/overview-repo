/*
 * Created on 17. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;

import java.util.List;

/**
 * Repository for supply points.
 * @author Radek Beran
 */
public interface SupplyPointRepository extends Repository<SupplyPoint, Integer, SupplyPointFilter> {

	List<SupplyPoint> findByCustomerIds(List<Integer> customerIds);
}
