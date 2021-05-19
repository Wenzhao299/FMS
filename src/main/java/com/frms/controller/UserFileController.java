package com.frms.controller;

import com.aliyun.oss.OSS;
import com.frms.entity.UserFile;
import com.frms.entity.User;
import com.frms.service.UserFileService;
import com.frms.utils.FileCryptoUtil;
import com.frms.utils.GetMacUtil;
import com.frms.utils.OSSClientUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class UserFileController {
    @Autowired
    private UserFileService userFileService;

    @GetMapping("/showJson")
    @ResponseBody
    public List<UserFile> showJson(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<UserFile> files = userFileService.findByUserId(user.getId());
        return files;
    }

    @GetMapping("/fileList")
    public String fileList(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<UserFile> files = userFileService.findByUserId(user.getId());
        session.setAttribute("files", files);
        return "redirect:/toFileList";
    }

    @PostMapping("/upload")
    public String upload(MultipartFile filename, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        String uploader = user.getUsername();
        String mac = (String) session.getAttribute("mac");
        System.out.println(mac);
        //文件原名称
        String odlFileName = filename.getOriginalFilename();
        //文件后缀
        String ext = "." + FilenameUtils.getExtension(odlFileName);
        //文件新名称
        String uuid = UUID.randomUUID().toString().replace("-","");
        String newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + uuid + ext;
        //文件大小
        long size = filename.getSize();
        //文件类型
        String type = filename.getContentType();
        //根据日期生成目录
        String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static/files";
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateDirPath = realPath + "/" + dateFormat;
        File dateDir = new File(dateDirPath);
        if(!dateDir.exists()){
            dateDir.mkdirs();
        }
        //根据日期生成缓存目录
        String tmpPath = ResourceUtils.getURL("classpath:").getPath() + "/static/filesTmp";
        String dateTmpDirPath = tmpPath + "/" + dateFormat;
        File dateTmpDir = new File(dateTmpDirPath);
        if(!dateTmpDir.exists()){
            dateTmpDir.mkdirs();
        }
        //缓存文件上传
        File tmpFile;
        filename.transferTo(tmpFile = new File(dateTmpDir,newFileName));
        //文件加密上传
        String encKey = GetMacUtil.getEncKey(uploader + mac);
        try (FileInputStream fis = new FileInputStream(dateTmpDir + File.separator + newFileName);
             FileOutputStream fos = new FileOutputStream(dateDir + File.separator + newFileName, true)) {
            FileCryptoUtil.encryptFile(fis, fos, encKey);
            tmpFile.delete();
        }

        //文件信息放入数据库
        UserFile userFile = new UserFile();
        userFile.setUuid(uuid).setOldFileName(odlFileName).setNewFileName(newFileName).setExt(ext).setSize(String.valueOf(size)).setType(type).setPath("/files/"+dateFormat).setUserId(user.getId());
        userFileService.upload(userFile);
        return "redirect:/file/fileList";
    }

    @GetMapping("/download")
    public void download(String openStyle, Integer id, HttpServletResponse response, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        String uploader = user.getUsername();
        String mac = (String) session.getAttribute("mac");
        openStyle = openStyle==null?"attachment":openStyle;
        //获取文件信息
        UserFile userFile = userFileService.findById(id);
        //获取文件路径
        String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static" + userFile.getPath();
        //获取文件输入流
        FileInputStream is = new FileInputStream(new File(realPath, userFile.getNewFileName()));
        //附件下载
        response.setHeader("content-disposition",openStyle+";filename=" + URLEncoder.encode(userFile.getOldFileName(),"utf-8"));
        //获取响应输出流
        ServletOutputStream os = response.getOutputStream();

        // 文件解密
        String encKey = GetMacUtil.getEncKey(uploader + mac);
        FileCryptoUtil.decryptedFile(is, os, encKey);

        //更新下载次数
        if(openStyle.equals("attachment")) {
            userFile.setDownCounts(userFile.getDownCounts() + 1);
            userFileService.update(userFile);
        }
    }

    @GetMapping("/delete")
    public String delete(Integer id) throws FileNotFoundException {
        UserFile userFile = userFileService.findById(id);
        String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static" + userFile.getPath();
        File file = new File(realPath, userFile.getNewFileName());
        if(file.exists()){
            file.delete();
        }
        userFileService.delete(id);
        return "redirect:/file/fileList";
    }
}
