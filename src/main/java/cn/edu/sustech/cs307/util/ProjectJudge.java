package cn.edu.sustech.cs307.util;

import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.factory.ServiceFactory;
import cn.edu.sustech.cs307.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ProjectJudge {
    private static final File searchCourse1Dir = new File("./data/searchCourse1/");
    private static final File enrollCourse1Dir = new File("./data/enrollCourse1/");
    private static final File courseTable2Dir = new File("./data/courseTable2/");
    private static final File searchCourse2Dir = new File("./data/searchCourse2/");
    private static final File enrollCourse2Dir = new File("./data/enrollCourse2/");

    private final ServiceFactory serviceFactory = Config.getServiceFactory();
    private final CourseService courseService = serviceFactory.createService(CourseService.class);
    private final DepartmentService departmentService = serviceFactory.createService(DepartmentService.class);
    private final SemesterService semesterService = serviceFactory.createService(SemesterService.class);
    private final StudentService studentService = serviceFactory.createService(StudentService.class);
    private final UserService userService = serviceFactory.createService(UserService.class);
    private final DataImporter importer = new DataImporter();

    private final List<CourseSearchEntry> errorCourseList = List.of(new CourseSearchEntry());
    private final CourseTable errorTable = new CourseTable();

    public EvalResult testSearchCourses(File searchCourseDir) {
        EvalResult result = new EvalResult();
        for (File file : searchCourseDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Object>> searchCourseParams = readValueFromFile(file, List.class);
            List<List<CourseSearchEntry>> searchCourseExpected = readValueFromFile(
                    new File(searchCourseDir, file.getName().replace(".json", "Result.json")), List.class);
            searchCourseExpected.parallelStream().forEach(this::mapSearchEntryId);
            long beforeTime = System.nanoTime();
            List<List<CourseSearchEntry>> searchCourseResult = IntStream.range(0, searchCourseParams.size()).parallel()
                    .mapToObj(it -> testSearchCourse(searchCourseParams.get(it)))
                    .collect(Collectors.toUnmodifiableList());
            result.elapsedTimeNs.addAndGet(System.nanoTime() - beforeTime);
            result.passCount.addAndGet(IntStream.range(0, searchCourseParams.size()).parallel()
                    .filter(it -> searchCourseExpected.get(it).equals(searchCourseResult.get(it))).count());
        }
        return result;
    }

    public List<CourseSearchEntry> testSearchCourse(List<Object> params) {
        try {
            return studentService.searchCourse((int) params.get(0), importer.mapSemesterId((int) params.get(1)),
                    (String) params.get(2), (String) params.get(3), (String) params.get(4),
                    (DayOfWeek) params.get(5), shortValue(params.get(6)), (List<String>) params.get(7),
                    (StudentService.CourseType) params.get(8),
                    (boolean) params.get(9), (boolean) params.get(10),
                    (boolean) params.get(11), (boolean) params.get(12),
                    (int) params.get(13), (int) params.get(14));
        } catch (Throwable t) {
            t.printStackTrace();
            return errorCourseList;
        }
    }

    public void mapSearchEntryId(List<CourseSearchEntry> result) {
        for (CourseSearchEntry entry : result) {
            entry.section.id = importer.mapSectionId(entry.section.id);
            for (CourseSectionClass clazz : entry.sectionClasses) {
                clazz.id = importer.mapClassId(clazz.id);
            }
            entry.sectionClasses = Set.copyOf(entry.sectionClasses); // fix HashSet internal state
        }
    }

    public EnrollEvalResult testEnrollCourses(File enrollCourseDir) {
        EnrollEvalResult evalResult = new EnrollEvalResult();
        evalResult.succeedSections = new ArrayList<>();
        for (File file : enrollCourseDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Integer>> enrollCourseParams = readValueFromFile(file, List.class);
            List<StudentService.EnrollResult> enrollCourseResults = readValueFromFile(
                    new File(enrollCourseDir, file.getName().replace(".json", "Result.json")), List.class);
            for (int i = 0; i < enrollCourseParams.size(); i++) {
                StudentService.EnrollResult expected = enrollCourseResults.get(i);
                long beforeTime = System.nanoTime();
                StudentService.EnrollResult result = testEnrollCourse(enrollCourseParams.get(i));
                evalResult.elapsedTimeNs.addAndGet(System.nanoTime() - beforeTime);
                if (expected == result) {
                    evalResult.passCount.incrementAndGet();
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

    public EvalResult testDropEnrolledCourses(EnrollEvalResult evalResult) {
        EvalResult result = new EvalResult();
        long beforeTime = System.nanoTime();
        evalResult.succeedSections.parallelStream().forEach(it -> {
            try {
                studentService.dropCourse(it.get(0), importer.mapSectionId(it.get(1)));
                result.passCount.incrementAndGet();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });
        result.elapsedTimeNs.set(System.nanoTime() - beforeTime);
        return result;
    }

    public EvalResult testCourseTables(File courseTableDir) {
        EvalResult result = new EvalResult();
        for (File file : courseTableDir.listFiles((dir, name) -> !name.endsWith("Result.json"))) {
            List<List<Integer>> courseTableParams = readValueFromFile(file, List.class);
            List<CourseTable> courseTableExpected = readValueFromFile(
                    new File(courseTableDir, file.getName().replace(".json", "Result.json")), List.class);
            long beforeTime = System.nanoTime();
            List<CourseTable> courseTableResults = IntStream.range(0, courseTableParams.size()).parallel()
                    .mapToObj(it -> testCourseTable(courseTableParams.get(it)))
                    .collect(Collectors.toUnmodifiableList());
            result.elapsedTimeNs.addAndGet(System.nanoTime() - beforeTime);
            result.passCount.addAndGet(IntStream.range(0, courseTableParams.size()).parallel()
                    .filter(it -> courseTableExpected.get(it).equals(courseTableResults.get(it))).count());
        }
        return result;
    }

    public CourseTable testCourseTable(List<Integer> params) {
        try {
            return studentService.getCourseTable(params.get(0), Date.valueOf(LocalDate.ofEpochDay(params.get(1))));
        } catch (Throwable t) {
            t.printStackTrace();
            return errorTable;
        }
    }

    public EvalResult testDropCourses(Map<String, Map<String, Grade>> studentCourses) {
        EvalResult result = new EvalResult();
        long beforeTime = System.nanoTime();
        studentCourses.entrySet().parallelStream().forEach(grades -> {
            int student = Integer.parseInt(grades.getKey());
            grades.getValue().entrySet().parallelStream().forEach(it -> {
                if (it.getValue() != null) {
                    int section = importer.mapSectionId(Integer.parseInt(it.getKey()));
                    try {
                        studentService.dropCourse(student, section);
                    } catch (IllegalStateException e) {
                        result.passCount.getAndIncrement();
                    }
                }
            });
        });
        result.elapsedTimeNs.set(System.nanoTime() - beforeTime);
        return result;
    }

    private static Short shortValue(Object integer) {
        if (integer != null) {
            return ((Integer) integer).shortValue();
        } else {
            return null;
        }
    }

    public void benchmark() {
        if (!courseService.getAllCourses().isEmpty()
                || !departmentService.getAllDepartments().isEmpty()
                || !semesterService.getAllSemesters().isEmpty()
                || !userService.getAllUsers().isEmpty()) {
            System.out.println("Database is not empty! Trying to truncate all your tables.");
            try {
                courseService.getAllCourses().parallelStream().forEach(it -> courseService.removeCourse(it.id));
                departmentService.getAllDepartments().parallelStream()
                        .forEach(it -> departmentService.removeDepartment(it.id));
                semesterService.getAllSemesters().parallelStream().forEach(it -> semesterService.removeSemester(it.id));
                userService.getAllUsers().parallelStream().forEach(it -> userService.removeUser(it.id));
            } catch (Throwable t) {
                System.out.println("Failed to truncate database.");
                t.printStackTrace();
                System.exit(1);
            }
        }

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
        EvalResult searchCourse1 = testSearchCourses(searchCourse1Dir);
        System.out.println("Test search course 1: " + searchCourse1.passCount.get());
        System.out.printf("Test search course 1 time: %.2fs\n", searchCourse1.elapsedTimeNs.get() / 1000000000.0);
        // 3. Test enrollCourse1
        EnrollEvalResult enrollCourse1 = testEnrollCourses(enrollCourse1Dir);
        System.out.println("Test enroll course 1: " + enrollCourse1.passCount.get());
        System.out.printf("Test enroll course 1 time: %.2fs\n", enrollCourse1.elapsedTimeNs.get() / 1000000000.0);
        // 4. Drop all success course
        EvalResult dropEnrolledCourse1 = testDropEnrolledCourses(enrollCourse1);
        System.out.println("Test drop enrolled course 1: " + dropEnrolledCourse1.passCount.get());
        System.out.printf("Test drop enrolled course 1 time: %.2fs\n",
                dropEnrolledCourse1.elapsedTimeNs.get() / 1000000000.0);
        // 5. Import studentCourses.json
        startTimeNs = System.nanoTime();
        System.out.println("Import student courses");
        importer.importStudentCourses(studentCourses);
        endTimeNs = System.nanoTime();
        System.out.printf("Import student courses time: %.2fs\n", (endTimeNs - startTimeNs) / 1000000000.0);
        // 6. Try to drop graded course, test if throw IllegalStateException
        EvalResult dropCourse = testDropCourses(studentCourses);
        System.out.println("Test drop course: " + dropCourse.passCount.get());
        System.out.printf("Test drop course time: %.2fs\n", dropCourse.elapsedTimeNs.get() / 1000000000.0);
        // 7. Test courseTable2
        EvalResult courseTables2 = testCourseTables(courseTable2Dir);
        System.out.println("Test course table 2: " + courseTables2.passCount.get());
        System.out.printf("Test course table 2 time: %.2fs\n", courseTables2.elapsedTimeNs.get() / 1000000000.0);
        // 8. Test searchCourse2
        EvalResult searchCourse2 = testSearchCourses(searchCourse2Dir);
        System.out.println("Test search course 2: " + searchCourse2.passCount.get());
        System.out.printf("Test search course 2 time: %.2fs\n", searchCourse2.elapsedTimeNs.get() / 1000000000.0);
        // 9. Test enrollCourse2
        EnrollEvalResult enrollCourse2 = testEnrollCourses(enrollCourse2Dir);
        System.out.println("Test enroll course 2: " + enrollCourse2.passCount.get());
        System.out.printf("Test enroll course 2 time: %.2fs\n", enrollCourse2.elapsedTimeNs.get() / 1000000000.0);
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

    private static class EvalResult {
        public AtomicLong passCount = new AtomicLong();
        public AtomicLong elapsedTimeNs = new AtomicLong();
    }

    private static class EnrollEvalResult extends EvalResult {
        public List<List<Integer>> succeedSections;
    }
}
