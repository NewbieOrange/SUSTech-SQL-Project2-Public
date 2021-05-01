package cn.edu.sustech.cs307.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;

/**
 * The relationship between {@code CourseSectionClass} with {@code CourseSection} is:
 * One CourseSection usually has two CourseSectionClass
 * the one is theory class, the other is lab class
 */
public class CourseSectionClass {
    public int id;// it is the id of course section class
    public Instructor instructor;
    public DayOfWeek dayOfWeek;
    public List<Short> weekList;
    //the time quantum of start and end.
    //For example: classStart is 3 while classEnd is 4
    public short classBegin, classEnd;
    public String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseSectionClass that = (CourseSectionClass) o;
        return id == that.id && classBegin == that.classBegin && classEnd == that.classEnd &&
                instructor.equals(that.instructor) && dayOfWeek == that.dayOfWeek && weekList.equals(that.weekList) &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instructor, dayOfWeek, weekList, classBegin, classEnd, location);
    }
}
