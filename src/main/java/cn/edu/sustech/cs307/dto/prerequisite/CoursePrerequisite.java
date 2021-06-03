package cn.edu.sustech.cs307.dto.prerequisite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents a leaf node of each prerequisite.
 * <p>
 * For example,
 * the terms in {@code AndPrerequisite}/{@code OrPrerequisite} can be either
 * implementation of {@code Prerequisite}.
 */
public class CoursePrerequisite implements Prerequisite {
    public final String courseID;

    @JsonCreator
    public CoursePrerequisite(@JsonProperty("courseID") @Nonnull String courseID) {
        this.courseID = courseID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoursePrerequisite that = (CoursePrerequisite) o;
        return courseID.equals(that.courseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }

    @Override
    public <R> R when(Cases<R> cases) {
        return cases.match(this);
    }
}
