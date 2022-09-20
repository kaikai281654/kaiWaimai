package com.kai.waimai.controller;


import com.kai.waimai.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
//做文件上传和处理
public class CommonController {

        @Value("${dakaizi.path}")
        private String basePath;


        @PostMapping("/upload")
        public R<String> upload(MultipartFile file) throws IOException {
            //file为零时文件,需要转存，否则请求完成后会自动删除

            //原始文件名 文件类型
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            //随机生成文件名
            String s = UUID.randomUUID().toString();

            //整体文件名称为
            String fileName=s+suffix;

            //创建一个目录对象
            File dir=new File(basePath);
            //判断当前目录是否存在
            if (!dir.exists()){
                dir.mkdir();
            }
            //将零时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));

            log.info(file.toString());
            return R.success(fileName);
        }

        @GetMapping("/download")
        public void dowload(String name , HttpServletResponse response) throws IOException {
            //输入流，通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流,通过输出流将图片文件资源返回给客户端
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");


            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();

        }


}
