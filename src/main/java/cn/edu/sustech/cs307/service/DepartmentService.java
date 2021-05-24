package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.Department;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface DepartmentService {
    /**
     *  if adding a new department which has the same name with an existing department,
     *  it should throw an {@code IntegrityViolationException}
     * @param name
     * @return
     */
    int addDepartment(String name);

    void removeDepartment(int departmentId);

    List<Department> getAllDepartments();

    /**
     * If there is no Department about specific id, throw EntityNotFoundException.
     * @param departmentId
     * @return
     */
    Department getDepartment(int departmentId);
}
