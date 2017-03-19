package cz.etn.overview.funs;

import java.util.Collection;

/**
 * @author Radek Beran
 */
public class CollectionFuns {

    private CollectionFuns() {
    }

    public static String join(Collection<String> coll, String delimiter) {
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
