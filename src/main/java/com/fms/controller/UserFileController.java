package com.fms.controller;

import com.fms.entity.UserFile;
import com.fms.entity.User;
import com.fms.service.UserFileService;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @GetMapping("/show")
    public String show(HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        List<UserFile> files = userFileService.findByUserId(user.getId());
        model.addAttribute("files", files);
        return "showAll";
    }

    @PostMapping("/upload")
    public String upload(MultipartFile filename, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        //文件原名称
        String odlFileName = filename.getOriginalFilename();
        //文件后缀
        String ext = "." + FilenameUtils.getExtension(odlFileName);
        //文件新名称
        String newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + UUID.randomUUID().toString().replace("-","") + ext;
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
        //文件上传
        filename.transferTo(new File(dateDir,newFileName));
        //文件信息放入数据库
        UserFile userFile = new UserFile();
        userFile.setOldFileName(odlFileName).setNewFileName(newFileName).setExt(ext).setSize(String.valueOf(size)).setType(type).setPath("/files/"+dateFormat).setUserId(user.getId());

        userFileService.upload(userFile);

        return "redirect:/file/show";
    }

    @GetMapping("/download")
    public void download(String openStyle, Integer id, HttpServletResponse response) throws IOException {
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
        //文件拷贝
        IOUtils.copy(is, os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
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
        return "redirect:/file/show";
    }
}
