package cn.edu.sustech.cs307.dto;

import java.util.Objects;

public class Course {
    public enum CourseGrading {
        PASS_OR_FAIL, HUNDRED_MARK_SCORE
    }

    // A courseId is valid if it has been added to the system with addCourse()
    // Do not check whether it is 'CS307' or '307CS'
    public String id;

    public String name;

    public int credit;

    public int classHour;

    public CourseGrading grading;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return credit == course.credit && classHour == course.classHour && id.equals(course.id)
                && name.equals(course.name) && grading == course.grading;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, credit, classHour, grading);
    }
}
