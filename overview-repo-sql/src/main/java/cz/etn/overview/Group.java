package cz.etn.overview;

import cz.etn.overview.mapper.Attribute;

import java.io.Serializable;

/**
 * Grouping according to one entity attribute. Immutable class.
 * @author Radek Beran
 */
public class Group implements Serializable {
    private static final long serialVersionUID = -1L;

    // Change this to typed attribute object? Maybe not, Group is handy as Serializable and attributes are not serializable.
    private final String attribute;

    public Group(Attribute<?, ?> attribute) {
        this.attribute = attribute.getNameFull();
    }

    /**
     * Name or full (qualified) name of attribute.
     */
    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "Group [attribute=" + attribute + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Group other = (Group)obj;
        if (attribute == null) {
            if (other.attribute != null) return false;
        } else if (!attribute.equals(other.attribute)) return false;
        return true;
    }

}
