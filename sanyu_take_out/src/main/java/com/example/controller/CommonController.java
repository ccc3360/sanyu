package com.example.controller;

import com.example.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename=file.getOriginalFilename();
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName= UUID.randomUUID().toString()+suffix;
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        String dirPath = jarF.getParentFile().toString()+"/upload/";
        File filepath=new File(dirPath,fileName);
        if(!filepath.getParentFile().exists()){
            filepath.getParentFile().mkdirs();
        }
        try {
//            file.transferTo(filepath.getAbsoluteFile());
            Path path1= Paths.get(dirPath,fileName);
            file.transferTo(path1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            ApplicationHome h = new ApplicationHome(getClass());
            File jarF = h.getSource();
            String dirPath = jarF.getParentFile().toString()+"/upload/";
            FileInputStream fileInputStream=new FileInputStream(new File(dirPath+name));
            ServletOutputStream outputStream=response.getOutputStream();
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
