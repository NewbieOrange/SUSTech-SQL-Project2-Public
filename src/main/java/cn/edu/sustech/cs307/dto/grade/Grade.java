package cn.edu.sustech.cs307.dto.grade;

/**
 * Represents a student's grade in a course, which has 2 implementations:
 * {@link cn.edu.sustech.cs307.dto.grade.PassOrFailGrade},
 * {@link cn.edu.sustech.cs307.dto.grade.HundredMarkGrade}.
 */
public interface Grade {
    /** Visitor of {@code Grade} */
    interface Cases<R> {
        R match(PassOrFailGrade self);

        R match(HundredMarkGrade self);
    }

    /**
     * Matches the {@code Grade} instance with the given visitor {@code cases}
     *
     * @param cases visitor instance, which can be an anonymous inner class instance.
     * @param <R>   expression type
     * @return the returned value from the matched case
     */
    <R> R when(Cases<R> cases);
}
