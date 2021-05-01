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
     * List all time conflicting courses' full name, sorted alphabetically.
     * Course full name: String.format("%s[%s]", course.name, section.name)
     */
    public List<String> conflictCourseNames;
}
