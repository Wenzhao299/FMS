package com.frms.service.impl;

import com.frms.entity.User;
import com.frms.mapper.UserMapper;
import com.frms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User login(User user) {
        return userMapper.login(user);
    }

    @Override
    public void register(User user) {
        userMapper.register(user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void updateCounter(String username, Integer counter) {
        userMapper.updateCounter(username, counter);
    }
}
