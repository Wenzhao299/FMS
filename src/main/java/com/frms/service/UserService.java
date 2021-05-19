package com.frms.service;

import com.frms.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {
    User login(User user);
    void register(User user);
    User findByUsername(String username);
    void updateCounter(@Param("username") String username, @Param("counter") Integer counter);
}
