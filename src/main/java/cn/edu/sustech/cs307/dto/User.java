package cn.edu.sustech.cs307.dto;

import java.util.Objects;

public abstract class User {

    public int id;
    /**
     * A user's full name is: first_name || ' ' || last_name, if both first name and last name are alphabetical (English alphabets) or space (' '), otherwise first_name || last_name.
     *
     * For example, if a user has first name David and last name Lee then the full name is David Lee; if another user has first name 张 and last name 三, the full name is 张三;
     * if first name 'David Lee' and last name 'Roth' then full name is 'David Lee Roth'.
     */
    public String fullName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && fullName.equals(user.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName);
    }
}
