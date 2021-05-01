package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.DayOfWeek;
import java.util.List;

@ParametersAreNonnullByDefault
public interface CourseService {
    /**
     * Add one course according to following parameters.
     * If some of parameters are invalid, throw {@link cn.edu.sustech.cs307.exception.IntegrityViolationException}
     *
     * @param courseId represents the id of course. For example, CS307, CS309
     * @param courseName the name of course
     * @param credit the credit of course
     * @param classHour The total teaching hour that the course spends.
     * @param grading the grading type of course
     * @param coursePrerequisite The root node of prerequisite.{@link cn.edu.sustech.cs307.dto.prerequisite.Prerequisite}
     */
    void addCourse(String courseId, String courseName, int credit, int classHour,
                   Course.CourseGrading grading, @Nullable Prerequisite coursePrerequisite);

    /**
     * Add one course section according to following parameters:
     * If some of parameters are invalid, throw {@link cn.edu.sustech.cs307.exception.IntegrityViolationException}
     *
     * @param courseId represents the id of course. For example, CS307, CS309
     * @param semesterId the id of semester
     * @param sectionName the name of section {@link cn.edu.sustech.cs307.dto.CourseSection}
     * @param totalCapacity the total capacity of section
     * @return the CourseSection id of new inserted line, if adding process is successful.
     */
    int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity);


    /**
     * Add one course section class according to following parameters:
     * If some of parameters are invalid, throw {@link cn.edu.sustech.cs307.exception.IntegrityViolationException}
     *
     * @param sectionId
     * @param instructorId
     * @param dayOfWeek
     * @param weekList
     * @param classStart
     * @param classEnd
     * @param location
     * @return the CourseSectionClass id of new inserted line.
     */
    int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek,List<Short> weekList,
                              short classStart, short classEnd, String location);


    void removeCourse(String courseId);

    void removeCourseSection(int sectionId);

    void removeCourseSectionClass(int classId);

    List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId);

    Course getCourseBySection(int sectionId);

    /**
     *
     * @param sectionId the id of {@code CourseSection}
     * @return
     */
    List<CourseSectionClass> getCourseSectionClasses(int sectionId);

    CourseSection getCourseSectionByClass(int classId);

    List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId);
}
