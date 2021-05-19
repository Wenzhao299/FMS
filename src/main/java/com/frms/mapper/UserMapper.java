package com.frms.mapper;

import com.frms.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User login(User user);
    void register(User user);
    User findByUsername(String username);
    void updateCounter(@Param("username") String username, @Param("counter") Integer counter);
}
