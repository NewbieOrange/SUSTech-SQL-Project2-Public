package cn.edu.sustech.cs307.dto;

import java.sql.Date;

/**
 * In our benchmark, there won't be overlapped semesters.
 */
public class Semester {
    public int id;
    public String name;
    /**
     * If the end date is before the start date, you need give an illegal Argument Error
     * If the date is ridiculous, such as 1900-1-1 or 3000-1-1, it should not give error.
     */
    public Date begin, end;
}
