package cn.edu.sustech.cs307.dto;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CourseSearchEntry {
    /**
     * The course of the searched section
     */
    public Course course;

    /**
     * The searched course section
     */
    public CourseSection section;

    /**
     * All classes of the section
     */
    public Set<CourseSectionClass> sectionClasses;

    /**
     * List all course or time conflicting courses' full name, sorted alphabetically.
     * Course full name: String.format("%s[%s]", course.name, section.name)
     * <p>
     * The conflict courses come from the student's enrolled courses (' sections).
     * <p>
     * Course conflict is when multiple sections belong to the same course.
     * Time conflict is when multiple sections have time-overlapping classes.
     * Note that a section is both course and time conflicting with itself!
     */
    public List<String> conflictCourseNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseSearchEntry entry = (CourseSearchEntry) o;
        return course.equals(entry.course) && section.equals(entry.section)
                && sectionClasses.equals(entry.sectionClasses)
                && conflictCourseNames.equals(entry.conflictCourseNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, section, sectionClasses, conflictCourseNames);
    }
}
