package com.skytakeout.controller.admin;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.result.Result;
import com.skytakeout.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
@Tag(name = "图片通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 文件上传
     * @param file 上传的文件
     * @return 文件访问路径
     */
    @PostMapping("/upload")
    @Operation(summary = "图片上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file.getOriginalFilename());
        try {
            String fileName = fileUploadUtil.uploadFile(file);
            return Result.success(fileName);
        } catch (IOException e) {
            log.error(MessageConstant.UPLOAD_FAILED);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

    }
}
