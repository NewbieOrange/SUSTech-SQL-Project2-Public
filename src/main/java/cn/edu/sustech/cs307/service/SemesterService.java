package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.Semester;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.util.List;

@ParametersAreNonnullByDefault
public interface SemesterService {
    /**
     * Add one semester according to following parameters:
     * If some of parameters are invalid, throw {@link cn.edu.sustech.cs307.exception.IntegrityViolationException}
     * @param name
     * @param begin
     * @param end
     * @return the Semester id of new inserted line, if adding process is successful.
     */
    int addSemester(String name, Date begin, Date end);

    void removeSemester(int semesterId);

    List<Semester> getAllSemesters();

    Semester getSemester(int semesterId);
}
