/*
 * Created on 20. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.funs;

/**
 * Function whose application can throw an checked exception.
 * @author Radek Beran
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
   R apply(T t) throws Exception;
}
