package cn.edu.sustech.cs307.dto;

import java.util.Objects;

public class CourseSection {
    /**
     *For example it can represent the id of section "No.1 Chinese class of database principle"
     */
    public int id;
    /**
     * if the course name is "database principle", the name here could be "No.1 Chinese class", "No.1 English class" ...
     */
    public String name;
    public int totalCapacity, leftCapacity;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseSection section = (CourseSection) o;
        return id == section.id && totalCapacity == section.totalCapacity && leftCapacity == section.leftCapacity
                && name.equals(section.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, totalCapacity, leftCapacity);
    }
}
