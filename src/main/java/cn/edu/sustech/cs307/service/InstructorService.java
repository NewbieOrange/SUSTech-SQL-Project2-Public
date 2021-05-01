package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.CourseSection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface InstructorService {
    int addInstructor(int userId, String firstName, String lastName);

    /**
     *
     * @param instructorId
     * @param semesterId
     * @return
     */
    List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId);
}
