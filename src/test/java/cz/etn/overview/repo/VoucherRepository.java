/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Filter;
import cz.etn.overview.domain.Voucher;

/**
 * Voucher repository.
 * @author Radek Beran
 */
public interface VoucherRepository extends AbstractRepository<Voucher, String, Filter> {
	// nothing new here
}
