package com.skytakeout.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {
    @Value("${sky.upload.path}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        String originFileName = file.getOriginalFilename();
        String extension = originFileName.substring(originFileName.lastIndexOf("."));
        //UUID 生成新的拓展名
        String filename = UUID.randomUUID() + extension;
        // 创建目录对象
        File dir = new File(uploadPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // 创建文件对象
        File dest = new File(dir, filename);
        // 保存文件
        file.transferTo(dest);

        return filename;
    }
}
