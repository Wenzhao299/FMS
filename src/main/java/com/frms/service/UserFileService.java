package com.frms.service;

import com.frms.entity.UserFile;

import java.util.List;

public interface UserFileService {
    List<UserFile> findByUserId(Integer userId);
    void upload(UserFile userFile);
    UserFile findById(Integer id);
    void update(UserFile userFile);
    void delete(Integer id);
}
