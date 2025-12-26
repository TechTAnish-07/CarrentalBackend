package com.sangraj.carrental.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {
        try {
            String contentType = file.getContentType();

            Map<String, Object> options = new HashMap<>();


            if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
                options.put("resource_type", "raw");
            } else {
                options.put("resource_type", "image");
            }

            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), options);

            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed");
        }
    }


//    public void delete(String imageUrl) {
//        try {
//            String publicId = extractPublicId(imageUrl);
//
//            cloudinary.uploader().destroy(
//                    publicId,
//                    Map.of("resource_type", "image")
//            );
//
//        } catch (Exception e) {
//            throw new RuntimeException("Image delete failed", e);
//        }
//
//    }
//    private String extractPublicId(String imageUrl) {
//        String withoutParams = imageUrl.split("\\?")[0];
//        String[] parts = withoutParams.split("/upload/");
//
//        String path = parts[1]; // v123/car-inspections/abc.png
//        path = path.replaceFirst("^v\\d+/", ""); // remove version
//        return path.substring(0, path.lastIndexOf('.'));
//    }
}
