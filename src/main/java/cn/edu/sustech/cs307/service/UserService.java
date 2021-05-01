package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.User;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserService {
    void removeUser(int userId);

    List<User> getAllUsers();

    User getUser(int userId);
}
