package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 *
 */
@ParametersAreNonnullByDefault
public interface StudentService {
    /**
     * The priority of EnrollResult should be (if not SUCCESS):
     *
     * COURSE_NOT_FOUND > ALREADY_ENROLLED > ALREADY_PASSED > PREREQUISITES_NOT_FULFILLED > COURSE_CONFLICT_FOUND > COURSE_IS_FULL > UNKNOWN_ERROR
     */
    enum EnrollResult {
        /**
         * Enrolled successfully
         */
        SUCCESS,
        /**
         * Cannot found the course section
         */
        COURSE_NOT_FOUND,
        /**
         * The course section is full
         */
        COURSE_IS_FULL,
        /**
         * The course section is already enrolled by the student
         */
        ALREADY_ENROLLED,
        /**
         * The course (of the section) is already passed by the student
         */
        ALREADY_PASSED,
        /**
         * The student misses prerequisites for the course
         */
        PREREQUISITES_NOT_FULFILLED,
        /**
         * The student's enrolled courses has time conflicts with the section,
         * or has course conflicts (same course) with the section.
         */
        COURSE_CONFLICT_FOUND,
        /**
         * Other (unknown) errors
         */
        UNKNOWN_ERROR
    }

    enum CourseType {
        /**
         * All courses
         */
        ALL,
        /**
         * Courses in compulsory courses of the student's major
         */
        MAJOR_COMPULSORY,
        /**
         * Courses in elective courses of the student's major
         */
        MAJOR_ELECTIVE,
        /**
         * Courses only in other majors than the student's major
         */
        CROSS_MAJOR,
        /**
         * Courses not belong to any major's requirements
         */
        PUBLIC
    }

    /**
     * Add one student according to following parameters.
     * If some of parameters are invalid, throw {@link cn.edu.sustech.cs307.exception.IntegrityViolationException}
     *
     * @param userId
     * @param majorId
     * @param firstName
     * @param lastName
     * @param enrolledDate
     */
    void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate);

    /**
     * Search available courses (' sections) for the specified student in the semester with extra conditions.
     * The result should be first sorted by course ID, and then sorted by course full name (course.name[section.name]).
     * Ignore all course sections that have no sub-classes.
     * Note: All ignore* arguments are about whether or not the result should ignore such cases.
     * i.e. when ignoreFull is true, the result should filter out all sections that are full.
     *
     * @param studentId
     * @param semesterId
     * @param searchCid                  search course id. Rule: searchCid in course.id
     * @param searchName                 search course name. Rule: searchName in "course.name[section.name]"
     * @param searchInstructor           search instructor name.
     *                                   Rule: firstName + lastName begins with searchInstructor
     *                                   or firstName + ' ' + lastName begins with searchInstructor
     *                                   or firstName begins with searchInstructor
     *                                   or lastName begins with searchInstructor.
     * @param searchDayOfWeek            search day of week. Matches *any* class in the section in the search day of week.
     * @param searchClassTime            search class time. Matches *any* class in the section contains the search class time.
     * @param searchClassLocations       search class locations. Matches *any* class in the section contains *any* location from the search class locations.
     * @param searchCourseType           search course type. See {@link cn.edu.sustech.cs307.service.StudentService.CourseType}
     * @param ignoreFull                 whether or not to ignore full course sections.
     * @param ignoreConflict             whether or not to ignore course or time conflicting course sections.
     *                                   Note that a section is both course and time conflicting with itself.
     *                                   See {@link cn.edu.sustech.cs307.dto.CourseSearchEntry#conflictCourseNames}
     * @param ignorePassed               whether or not to ignore the student's passed courses.
     * @param ignoreMissingPrerequisites whether or not to ignore courses with missing prerequisites.
     * @param pageSize                   the page size, effectively `limit pageSize`.
     *                                   It is the number of {@link cn.edu.sustech.cs307.dto.CourseSearchEntry}
     * @param pageIndex                  the page index, effectively `offset pageIndex * pageSize`.
     *                                   If the page index is so large that there is no message,return an empty list
     * @return a list of search entries. See {@link cn.edu.sustech.cs307.dto.CourseSearchEntry}
     */
    List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid,
                                         @Nullable String searchName, @Nullable String searchInstructor,
                                         @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime,
                                         @Nullable List<String> searchClassLocations,
                                         CourseType searchCourseType,
                                         boolean ignoreFull, boolean ignoreConflict,
                                         boolean ignorePassed, boolean ignoreMissingPrerequisites,
                                         int pageSize, int pageIndex);

    /**
     * It is the course selection function according to the studentId and courseId.
     * The test case can be invalid data or conflict info, so that it can return 8 different
     * types of enroll results.
     *
     * It is possible for a student-course have ALREADY_SELECTED and ALREADY_PASSED or PREREQUISITES_NOT_FULFILLED.
     * Please make sure the return priority is the same as above in similar cases.
     * {@link cn.edu.sustech.cs307.service.StudentService.EnrollResult}
     *
     * To check whether prerequisite courses are available for current one, only check the
     * grade of prerequisite courses are >= 60 or PASS
     *
     * @param studentId
     * @param sectionId the id of CourseSection
     * @return See {@link cn.edu.sustech.cs307.service.StudentService.EnrollResult}
     */
    EnrollResult enrollCourse(int studentId, int sectionId);

    /**
     * Drop a course section for a student
     *
     * @param studentId
     * @param sectionId
     * @throws IllegalStateException if the student already has a grade for the course section.
     */
    void dropCourse(int studentId, int sectionId) throws IllegalStateException;

    /**
     * It is used for importing existing data from other sources.
     * <p>
     * With this interface, staff for teaching affairs can bypass the
     * prerequisite fulfillment check to directly enroll a student in a course
     * and assign him/her a grade.
     *
     * If the scoring scheme of a course is one type in pass-or-fail and hundredmark grade,
     * your system should not accept the other type of grade.
     *
     * Course section's left capacity should remain unchanged after this method.
     *
     * @param studentId
     * @param sectionId We will get the sectionId of one section first
     *                  and then invoke the method by using the sectionId.
     * @param grade     Can be null
     */
    void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade);

    /**
     * For teachers to give students grade.
     *
     * @param studentId student id is in database
     * @param sectionId section id in test cases that have selected by the student
     * @param grade     a new grade
     */
    void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade);

    /**
     * Queries grades of all enrolled courses in the given semester for the given student
     *
     * If a student selected one course for over one times, for example
     * failed the course and passed it in the next semester,
     * in the {@Code Map<Course, Grade>}, it only record the latest grade.
     *
     * @param studentId
     * @param semesterId the semester id, null means return all semesters' result.
     * @return A map from enrolled courses to corresponding grades.
     * If the grade is a hundred-mark score, the value should be wrapped by a
     * {@code HundredMarkGrade} object.
     * If the grade is pass or fail, the value should be {@code PassOrFailGrade.PASS}
     * or {@code PassOrFailGrade.FAIL} respectively.
     * If the grade is not set yet, the value should be null.
     */
    Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId);

    /**
     * Return a course table in current week according to the date.
     *
     * @param studentId
     * @param date
     * @return the student's course table for the entire week of the date.
     * Regardless which day of week the date is, return Monday-to-Sunday course table for that week.
     */
    CourseTable getCourseTable(int studentId, Date date);

    /**
     * check whether a student satisfy a certain course's prerequisites.
     *
     * @param studentId
     * @param courseId
     * @return true if the student has passed the course's prerequisites (>=60 or PASS).
     */
    boolean passedPrerequisitesForCourse(int studentId, String courseId);

    Major getStudentMajor(int studentId);
}
