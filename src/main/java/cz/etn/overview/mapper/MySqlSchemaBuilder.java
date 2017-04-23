package cz.etn.overview.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Constructs base of SQL DDL commands based on entity attributes.
 * @author Radek Beran
 */
public class MySqlSchemaBuilder {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String UNKNOWN = "UNKNOWN";

    public <E> String composeCreateTableSQL(String tableName, List<Attribute<E, ?>> attributes) {
        StringBuilder sb = new StringBuilder();
        if (attributes != null && attributes.size() > 0) {
            Optional<String> primaryAttrNameOpt = getPrimaryAttributeName(attributes);
            sb.append("CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + NEW_LINE);
            boolean first = true;
            for (Attribute<E, ?> attr : attributes) {
                if (first) {
                    first = false;
                } else {
                    sb.append("," + NEW_LINE);
                }
                String sqlTypeName = getSqlTypeName(attr);
                sb.append("\t`" + attr.getName() + "` " + sqlTypeName);
                if (attr.isPrimary()) {
                    sb.append(" NOT NULL");
                    if (isNumber(attr)) {
                        sb.append(" AUTO_INCREMENT");
                    }
                }
            }
            primaryAttrNameOpt.map (primaryAttrName -> {
                sb.append("," + NEW_LINE);
                sb.append("\tPRIMARY KEY (`" + primaryAttrName + "`)");
                return null;
            });
            sb.append(NEW_LINE + ");" + NEW_LINE);
        }
        return sb.toString();
    }

    protected <E> Optional<String> getPrimaryAttributeName(List<Attribute<E, ?>> attributes) {
        Optional<String> name = Optional.empty();
        if (attributes != null) {
            for (Attribute<?, ?> attr : attributes) {
                if (attr.isPrimary()) {
                    name = Optional.ofNullable(attr.getName());
                    break;
                }
            }
        }
        return name;
    }

    protected <E, A> String getSqlTypeName(Attribute<E, A> attribute) {
        String typeName = UNKNOWN;
        if (attribute instanceof Attr) {
            Attr attr = (Attr)attribute;
            Class<A> attrClass = attr.getAttributeClass();
            if (String.class.isAssignableFrom(attrClass)) {
                typeName = "VARCHAR(" + UNKNOWN + ")";
            } else if (Byte.class.isAssignableFrom(attrClass) || Integer.class.isAssignableFrom(attrClass) || Long.class.isAssignableFrom(attrClass)) {
                typeName = "INT(" + UNKNOWN + ")";
            } else if (Instant.class.isAssignableFrom(attrClass) || Date.class.isAssignableFrom(attrClass)) {
                typeName = "DATETIME";
            } else if (BigDecimal.class.isAssignableFrom(attrClass)) {
                typeName = "DECIMAL(" + UNKNOWN + ", " + UNKNOWN + ")";
            }
        }
        return typeName;
    }

    protected <E, A> boolean isNumber(Attribute<E, A> attribute) {
        boolean number = false;
        if (attribute instanceof Attr) {
            Attr attr = (Attr)attribute;
            Class<A> attrClass = attr.getAttributeClass();
            number = Byte.class.isAssignableFrom(attrClass) || Integer.class.isAssignableFrom(attrClass) || Long.class.isAssignableFrom(attrClass) || BigDecimal.class.isAssignableFrom(attrClass);
        }
        return number;
    }
}
