package com.frms.service.impl;

import com.frms.entity.UserFile;
import com.frms.mapper.UserFileMapper;
import com.frms.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserFileServiceImpl implements UserFileService {
    @Autowired
    private UserFileMapper userFileMapper;

    @Override
    public List<UserFile> findByUserId(Integer userId) {
        return userFileMapper.findByUserId(userId);
    }

    @Override
    public void upload(UserFile userFile) {
        String isImg = userFile.getType().startsWith("image")?"Yes":"No";
        userFile.setIsImg(isImg);
        userFile.setDownCounts(0);
        userFile.setUploadTime(new Date());
        userFileMapper.upload(userFile);
    }

    @Override
    public UserFile findById(Integer id) {
        return userFileMapper.findById(id);
    }

    @Override
    public void update(UserFile userFile) {
        userFileMapper.update(userFile);
    }

    @Override
    public void delete(Integer id) {
        userFileMapper.delete(id);
    }
}
