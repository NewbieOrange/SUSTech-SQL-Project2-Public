package cn.edu.sustech.cs307.dto.prerequisite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * Represents the OR relationship.
 * <p>
 * This prerequisite expression is fulfilled if and only if any elements of
 * {@code terms} is fulfilled.
 */
public class OrPrerequisite implements Prerequisite {
    public final List<Prerequisite> terms;

    @JsonCreator
    public OrPrerequisite(@JsonProperty("terms") @Nonnull List<Prerequisite> terms) {
        this.terms = terms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrPrerequisite that = (OrPrerequisite) o;
        return terms.equals(that.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms);
    }

    @Override
    public <R> R when(Cases<R> cases) {
        return cases.match(this);
    }
}
