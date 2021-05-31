package cn.edu.sustech.cs307.dto;

import java.sql.Date;
import java.util.Objects;

public class Student extends User {
    public Date enrolledDate;

    public Major major;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Student student = (Student) o;
        return enrolledDate.equals(student.enrolledDate) && major.equals(student.major);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enrolledDate, major);
    }
}
