/*
 * Created on 14. 3. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import java.util.Collection;

/**
 * Auxiliary methods for entity mapping classes.
 * @author Radek Beran
 */
class EntityMappers {

	static String join(Collection<String> coll, String delimiter) {
        StringBuilder s = new StringBuilder();
        if (coll != null) {
        	int i = 0;
	        for (String c : coll) {
	          if (i != 0) { 
	        	  s.append(delimiter);
	    	  }
	          s.append(c);
	          i++;
	        }
        }
        return s.toString();
    }
}
