package cn.edu.sustech.cs307.dto;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CourseTable {
    public static class CourseTableEntry {
        /**
         * Course full name: String.format("%s[%s]", course.name, section.name)
         */
        public String courseFullName;
        /**
         * The section class's instructor
         */
        public Instructor instructor;
        /**
         * The class's begin and end time (e.g. 3 and 4).
         */
        public short classBegin, classEnd;
        /**
         * The class location.
         */
        public String location;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CourseTableEntry entry = (CourseTableEntry) o;
            return classBegin == entry.classBegin && classEnd == entry.classEnd && courseFullName
                    .equals(entry.courseFullName)
                    && instructor.equals(entry.instructor) && location.equals(entry.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(courseFullName, instructor, classBegin, classEnd, location);
        }
    }

    /**
     * Stores all courses(encapsulated by CourseTableEntry) according to DayOfWeek.
     * The key should always be from MONDAY to SUNDAY, if the student has no course for any of the days, put an empty list.
     */
    public Map<DayOfWeek, Set<CourseTableEntry>> table;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseTable that = (CourseTable) o;
        return table.equals(that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table);
    }
}
