package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.Major;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface MajorService {
    int addMajor(String name, int departmentId);

    void removeMajor(int majorId);

    List<Major> getAllMajors();

    Major getMajor(int majorId);

    /**
     * Binding a course id {@code courseId} to major id {@code majorId}, and the selection is compulsory.
     * @param majorId the id of major
     * @param courseId the course id
     */
    void addMajorCompulsoryCourse(int majorId, String courseId);

    /**
     * Binding a course id{@code courseId} to major id {@code majorId}, and the selection is elective.
     * @param majorId the id of major
     * @param courseId the course id
     */
    void addMajorElectiveCourse(int majorId, String courseId);
}
