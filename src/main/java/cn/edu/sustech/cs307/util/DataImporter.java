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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DataImporter {
    private final ServiceFactory serviceFactory = Config.getServiceFactory();

    private final Map<Integer, Integer> sectionIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> classIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> semesterIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> departmentIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> majorIdMap = new ConcurrentHashMap<>();

    public int mapSectionId(int sectionId) {
        return sectionIdMap.getOrDefault(sectionId, sectionId);
    }

    public int mapClassId(int classId) {
        return classIdMap.getOrDefault(classId, classId);
    }

    public int mapSemesterId(int semesterId) {
        return semesterIdMap.getOrDefault(semesterId, semesterId);
    }

    public int mapDepartmentId(int departmentId) {
        return departmentIdMap.getOrDefault(departmentId, departmentId);
    }

    public int mapMajorId(int majorId) {
        return majorIdMap.getOrDefault(majorId, majorId);
    }

    public void importCourses(List<Course> courses, Map<String, Prerequisite> prerequisites) {
        CourseService courseService = serviceFactory.createService(CourseService.class);
        courses.forEach(it -> courseService
                .addCourse(it.id, it.name, it.credit, it.classHour, it.grading, prerequisites.get(it.id)));
    }

    public void importCourseSection(Map<String, Map<String, List<CourseSection>>> sections) {
        CourseService courseService = serviceFactory.createService(CourseService.class);
        sections.entrySet().parallelStream().forEach(courses -> {
            String courseId = courses.getKey();
            courses.getValue().entrySet().parallelStream().forEach(semester -> {
                int semesterId = mapSemesterId(Integer.parseInt(semester.getKey()));
                semester.getValue().parallelStream().forEach(section ->
                        sectionIdMap.put(section.id, courseService.addCourseSection(courseId, semesterId,
                                section.name, section.totalCapacity))
                );
            });
        });
    }

    public void importCourseSectionClasses(Map<String, List<CourseSectionClass>> classes) {
        CourseService courseService = serviceFactory.createService(CourseService.class);
        classes.entrySet().parallelStream().forEach(it -> {
            int sectionId = mapSectionId(Integer.parseInt(it.getKey()));
            it.getValue().parallelStream().forEach(c ->
                    classIdMap.put(c.id, courseService.addCourseSectionClass(sectionId, c.instructor.id, c.dayOfWeek,
                            c.weekList, c.classBegin, c.classEnd, c.location)));
        });
    }

    public void importDepartments(List<Department> departments) {
        DepartmentService departmentService = serviceFactory.createService(DepartmentService.class);
        departments.forEach(it -> departmentIdMap.put(it.id, departmentService.addDepartment(it.name)));
    }

    public void importMajors(List<Major> majors) {
        MajorService majorService = serviceFactory.createService(MajorService.class);
        majors.forEach(it ->
                majorIdMap.put(it.id, majorService.addMajor(it.name, mapDepartmentId(it.department.id))));
    }

    public void importUsers(List<User> users) {
        StudentService studentService = serviceFactory.createService(StudentService.class);
        InstructorService instructorService = serviceFactory.createService(InstructorService.class);
        users.parallelStream().filter(it -> it instanceof Student).forEach(it -> {
            Student student = (Student) it;
            String[] name = student.fullName.split(",", 2);
            studentService.addStudent(student.id, mapMajorId(student.major.id), name[0], name[1], student.enrolledDate);
        });
        users.parallelStream().filter(it -> it instanceof Instructor).forEach(it -> {
            String[] name = it.fullName.split(",", 2);
            instructorService.addInstructor(it.id, name[0], name[1]);
        });
    }

    public void importSemesters(List<Semester> semesters) {
        SemesterService semesterService = serviceFactory.createService(SemesterService.class);
        semesters.forEach(it -> semesterIdMap.put(it.id, semesterService.addSemester(it.name, it.begin, it.end)));
    }

    public void importMajorCompulsoryCourses(Map<String, List<String>> majorCompulsoryCourses) {
        MajorService majorService = serviceFactory.createService(MajorService.class);
        majorCompulsoryCourses.forEach((key, value) -> value
                .forEach(it -> majorService.addMajorCompulsoryCourse(mapMajorId(Integer.parseInt(key)), it)));
    }

    public void importMajorElectiveCourses(Map<String, List<String>> majorElectiveCourses) {
        MajorService majorService = serviceFactory.createService(MajorService.class);
        majorElectiveCourses.forEach((key, value) -> value
                .forEach(it -> majorService.addMajorElectiveCourse(mapMajorId(Integer.parseInt(key)), it)));
    }

    public void importStudentCourses(Map<String, Map<String, Grade>> studentCourses) {
        StudentService studentService = serviceFactory.createService(StudentService.class);
        studentCourses.entrySet().parallelStream().forEach(grades -> {
            int studentId = Integer.parseInt(grades.getKey());
            grades.getValue().entrySet().parallelStream().forEach(it -> {
                int sectionId = mapSectionId(Integer.parseInt(it.getKey()));
                studentService.addEnrolledCourseWithGrade(studentId, sectionId, it.getValue());
            });
        });
    }

    private static <T> T readValueFromFile(String fileName, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        try {
            return objectMapper.readValue(new File("./data/" + fileName), tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
