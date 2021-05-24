package cn.edu.sustech.cs307.dto;

import java.util.List;

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
    public List<CourseSectionClass> sectionClasses;
    /**
     * List all course or time conflicting courses' full name, sorted alphabetically.
     * Course full name: String.format("%s[%s]", course.name, section.name)
     *
     * Course conflict is when multiple sections belong to the same course.
     * Time conflict is when multiple sections have time-overlapping classes.
     * Note that a section is both course and time conflicting with itself!
     */
    public List<String> conflictCourseNames;
}
