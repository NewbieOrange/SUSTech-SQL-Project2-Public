package cn.edu.sustech.cs307.dto.prerequisite;

/**
 * Represents the prerequisites of a course.
 * <p>
 * This interface has three implementations:
 * {@link cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite},
 * {@link cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite},
 * {@link cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite}.
 */
public interface Prerequisite {
    /**
     * Visitor of {@code Prerequisite}.
     */
    interface Cases<R> {
        R match(AndPrerequisite self);

        R match(OrPrerequisite self);

        R match(CoursePrerequisite self);
    }

    /**
     * Matches the {@code Prerequisite} instance with the given visitor {@code cases}
     *
     * @param cases visitor instance
     * @param <R> expression type
     * @return the returned value from the matched case
     */
    <R> R when(Cases<R> cases);
}
