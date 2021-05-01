package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.Department;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface DepartmentService {
    int addDepartment(String name);

    void removeDepartment(int departmentId);

    List<Department> getAllDepartments();

    Department getDepartment(int departmentId);
}
