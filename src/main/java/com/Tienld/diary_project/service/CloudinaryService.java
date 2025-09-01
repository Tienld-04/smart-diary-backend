package com.Tienld.diary_project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    //upload file cơ bản
    public Map uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
            "folder", folder,
            "resource_type", "auto"  // Tự động detect loại file
        );
        return cloudinary.uploader().upload(file.getBytes(), options);
    }
    
    // upload ảnh cho diary
    public Map uploadDiaryImage(MultipartFile file, Long diaryId) throws IOException {
        String folder = "diary_images";
        String publicId = "diary_" + diaryId + "_" + System.currentTimeMillis();
        
        Map<String, Object> options = ObjectUtils.asMap(
            "folder", folder,
            "public_id", publicId,
            "resource_type", "image",
            "transformation", "w_800,h_600,c_fill" // Resize ảnh về kích thước phù hợp
        );
        
        return cloudinary.uploader().upload(file.getBytes(), options);
    }
    
    //  upload ảnh với caption
    public Map uploadImageWithCaption(MultipartFile file, String folder, String caption) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
            "folder", folder,
            "resource_type", "image",
            "context", "caption=" + caption // Lưu caption vào metadata
        );
        
        return cloudinary.uploader().upload(file.getBytes(), options);
    }
}
