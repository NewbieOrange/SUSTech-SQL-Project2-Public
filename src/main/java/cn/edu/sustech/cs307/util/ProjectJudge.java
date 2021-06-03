package cn.edu.sustech.cs307.util;

import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.factory.ServiceFactory;
import cn.edu.sustech.cs307.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProjectJudge {
    private static final File searchCourse1Dir = new File("./data/searchCourse1/");
    private static final File enrollCourse1Dir = new File("./data/enrollCourse1/");
    private static final File courseTable2Dir = new File("./data/courseTable2/");
    private static final File searchCourse2Dir = new File("./data/searchCourse2/");
    private static final File enrollCourse2Dir = new File("./data/enrollCourse2/");

    private final ServiceFactory serviceFactory = Config.getServiceFactory();
    private final StudentService studentService = serviceFactory.createService(StudentService.class);
    private final DataImporter importer = new DataImporter();

    public int testSearchCourses(File searchCourseDir) {
        int passCount = 0;
        for (File file : searchCourseDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Object>> searchCourseParams = readValueFromFile(file, List.class);
            List<List<CourseSearchEntry>> searchCourseResults = readValueFromFile(
                    new File(searchCourseDir, file.getName().replace(".json", "Result.json")), List.class);
            for (int i = 0; i < searchCourseParams.size(); i++) {
                List<CourseSearchEntry> expected = searchCourseResults.get(i);
                mapSearchEntryId(expected);
                if (testSearchCourse(expected, searchCourseParams.get(i))) {
                    passCount++;
                }
            }
        }
        return passCount;
    }

    public boolean testSearchCourse(List<CourseSearchEntry> expected, List<Object> params) {
        try {
            List<CourseSearchEntry> result = studentService
                    .searchCourse((int) params.get(0), importer.mapSemesterId((int) params.get(1)),
                            (String) params.get(2), (String) params.get(3), (String) params.get(4),
                            (DayOfWeek) params.get(5), shortValue(params.get(6)), (List<String>) params.get(7),
                            (StudentService.CourseType) params.get(8),
                            (boolean) params.get(9), (boolean) params.get(10),
                            (boolean) params.get(11), (boolean) params.get(12),
                            (int) params.get(13), (int) params.get(14));
            return expected.equals(result);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public void mapSearchEntryId(List<CourseSearchEntry> result) {
        for (CourseSearchEntry entry : result) {
            entry.section.id = importer.mapSectionId(entry.section.id);
            for (CourseSectionClass clazz : entry.sectionClasses) {
                clazz.id = importer.mapClassId(clazz.id);
            }
            entry.sectionClasses = new HashSet<>(entry.sectionClasses); // fix HashSet internal state
        }
    }

    public EnrollEvalResult testEnrollCourses(File enrollCourseDir) {
        EnrollEvalResult evalResult = new EnrollEvalResult();
        evalResult.passCount = 0;
        evalResult.succeedSections = new ArrayList<>();
        for (File file : enrollCourseDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Integer>> enrollCourseParams = readValueFromFile(file, List.class);
            List<StudentService.EnrollResult> enrollCourseResults = readValueFromFile(
                    new File(enrollCourseDir, file.getName().replace(".json", "Result.json")), List.class);
            for (int i = 0; i < enrollCourseParams.size(); i++) {
                StudentService.EnrollResult expected = enrollCourseResults.get(i);
                if (expected == testEnrollCourse(enrollCourseParams.get(i))) {
                    evalResult.passCount++;
                }
                if (expected == StudentService.EnrollResult.SUCCESS) {
                    evalResult.succeedSections.add(enrollCourseParams.get(i));
                }
            }
        }
        return evalResult;
    }

    public StudentService.EnrollResult testEnrollCourse(List<Integer> params) {
        try {
            return studentService.enrollCourse(params.get(0), importer.mapSectionId(params.get(1)));
        } catch (Throwable t) {
            t.printStackTrace();
            return StudentService.EnrollResult.UNKNOWN_ERROR;
        }
    }

    public int testDropEnrolledCourses(EnrollEvalResult evalResult) {
        int passedCount = 0;
        for (List<Integer> params : evalResult.succeedSections) {
            try {
                studentService.dropCourse(params.get(0), importer.mapSectionId(params.get(1)));
                passedCount++;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return passedCount;
    }

    public int testCourseTables(File courseTableDir) {
        int passCount = 0;
        for (File file : courseTableDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Integer>> courseTableParams = readValueFromFile(file, List.class);
            List<CourseTable> courseTableResults = readValueFromFile(
                    new File(courseTableDir, file.getName().replace(".json", "Result.json")), List.class);
            for (int i = 0; i < courseTableParams.size(); i++) {
                CourseTable expected = courseTableResults.get(i);
                if (testCourseTable(expected, courseTableParams.get(i))) {
                    passCount++;
                }
            }
        }
        return passCount;
    }

    public boolean testCourseTable(CourseTable expected, List<Integer> params) {
        try {
            CourseTable result = studentService
                    .getCourseTable(params.get(0), Date.valueOf(LocalDate.ofEpochDay(params.get(1))));
            return expected.equals(result);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public int testDropCourses(Map<String, Map<String, Grade>> studentCourses) {
        AtomicInteger passCount = new AtomicInteger();
        studentCourses.forEach((studentId, grades) -> {
            int student = Integer.parseInt(studentId);
            grades.forEach((sectionId, grade) -> {
                if (grade != null) {
                    int section = importer.mapSectionId(Integer.parseInt(sectionId));
                    try {
                        studentService.dropCourse(student, section);
                    } catch (IllegalStateException e) {
                        passCount.getAndIncrement();
                    }
                }
            });
        });
        return passCount.get();
    }

    private static Short shortValue(Object integer) {
        if (integer != null) {
            return ((Integer) integer).shortValue();
        } else {
            return null;
        }
    }

    public void benchmark() {
        // 1. Import everything other than studentCourses.json
        List<Department> departments = readValueFromFile("departments.json", List.class);
        List<Major> majors = readValueFromFile("majors.json", List.class);
        List<User> users = readValueFromFile("users.json", List.class);
        List<Semester> semesters = readValueFromFile("semesters.json", List.class);
        List<Course> courses = readValueFromFile("courses.json", List.class);
        Map<String, List<String>> majorCompulsoryCourses = readValueFromFile("majorCompulsoryCourses.json", Map.class);
        Map<String, List<String>> majorElectiveCourses = readValueFromFile("majorElectiveCourses.json", Map.class);
        Map<String, Prerequisite> coursePrerequisites = readValueFromFile("coursePrerequisites.json", Map.class);
        Map<String, Map<String, List<CourseSection>>> sections = readValueFromFile("courseSections.json", Map.class);
        Map<String, List<CourseSectionClass>> classes = readValueFromFile("courseSectionClasses.json", Map.class);
        Map<String, Map<String, Grade>> studentCourses = readValueFromFile("studentCourses.json", Map.class);
        long startTimeNs, endTimeNs;
        startTimeNs = System.nanoTime();
        System.out.println("Import departments");
        importer.importDepartments(departments);
        System.out.println("Import majors");
        importer.importMajors(majors);
        System.out.println("Import users");
        importer.importUsers(users);
        System.out.println("Import semesters");
        importer.importSemesters(semesters);
        System.out.println("Import courses");
        importer.importCourses(courses, coursePrerequisites);
        System.out.println("Import sections");
        importer.importCourseSection(sections);
        System.out.println("Import classes");
        importer.importCourseSectionClasses(classes);
        System.out.println("Import major courses");
        importer.importMajorCompulsoryCourses(majorCompulsoryCourses);
        importer.importMajorElectiveCourses(majorElectiveCourses);
        endTimeNs = System.nanoTime();
        System.out.printf("Import time usage: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 2. Test searchCourse1
        startTimeNs = System.nanoTime();
        int searchCourse1 = testSearchCourses(searchCourse1Dir);
        endTimeNs = System.nanoTime();
        System.out.println("Test search course 1: " + searchCourse1);
        System.out.printf("Test search course 1 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 3. Test enrollCourse1
        startTimeNs = System.nanoTime();
        EnrollEvalResult enrollCourse1 = testEnrollCourses(enrollCourse1Dir);
        endTimeNs = System.nanoTime();
        System.out.println("Test enroll course 1: " + enrollCourse1.passCount);
        System.out.printf("Test enroll course 1 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 4. Drop all success course
        startTimeNs = System.nanoTime();
        int dropEnrolledCourse1 = testDropEnrolledCourses(enrollCourse1);
        endTimeNs = System.nanoTime();
        System.out.println("Test drop enrolled course 1: " + dropEnrolledCourse1);
        System.out.printf("Test drop enrolled course 1 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 5. Import studentCourses.json
        startTimeNs = System.nanoTime();
        System.out.println("Import student courses");
        importer.importStudentCourses(studentCourses);
        endTimeNs = System.nanoTime();
        System.out.printf("Import student courses time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 6. Try to drop graded course, test if throw IllegalStateException
        startTimeNs = System.nanoTime();
        int dropCourse = testDropCourses(studentCourses);
        endTimeNs = System.nanoTime();
        System.out.println("Test drop course: " + dropCourse);
        System.out.printf("Test drop course time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 7. Test courseTable2
        startTimeNs = System.nanoTime();
        int courseTables2 = testCourseTables(courseTable2Dir);
        endTimeNs = System.nanoTime();
        System.out.println("Test course table 2: " + courseTables2);
        System.out.printf("Test course table 2 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 8. Test searchCourse2
        startTimeNs = System.nanoTime();
        int searchCourse2 = testSearchCourses(searchCourse2Dir);
        endTimeNs = System.nanoTime();
        System.out.println("Test search course 2: " + searchCourse2);
        System.out.printf("Test search course 2 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 9. Test enrollCourse2
        startTimeNs = System.nanoTime();
        EnrollEvalResult enrollCourse2 = testEnrollCourses(enrollCourse2Dir);
        endTimeNs = System.nanoTime();
        System.out.println("Test enroll course 2: " + enrollCourse2.passCount);
        System.out.printf("Test enroll course 2 time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);

        // TODO: Multi-threaded benchmark

    }

    public static void main(String[] args) {
        ProjectJudge judge = new ProjectJudge();
        judge.benchmark();
    }

    private static <T> T readValueFromFile(File file, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        try {
            return objectMapper.readValue(file, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T readValueFromFile(String fileName, Class<T> tClass) {
        return readValueFromFile(new File("./data/" + fileName), tClass);
    }

    private static class EnrollEvalResult {
        public int passCount;
        public List<List<Integer>> succeedSections;
    }
}
