package com.example.FurnitureShop.Service.Implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.FurnitureShop.DTO.Response.UploadResult;
import com.example.FurnitureShop.Model.CommentMedia.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public UploadResult upload(MultipartFile file, String folder) throws IOException {
        log.info("Files {}", file);

        String resourceType = detectResourceType(file);

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", resourceType,
                "quality", "auto",
                "fetch_format", "auto"
        );

        Map uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), options);

        String secureUrl = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        String thumbnailUrl = null;
        Double duration = null;

        if ("video".equals(resourceType)) {
            thumbnailUrl = generateVideoThumbnail(publicId);
            Object durationObj = uploadResult.get("duration");
            if (durationObj != null) {
                duration = Double.parseDouble(durationObj.toString());
            }
        }

        return new UploadResult(
                secureUrl,
                publicId,
                resourceType,
                thumbnailUrl,
                duration
        );
    }

    private String detectResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) {
            return "video";
        }
        return "image";
    }

    private String generateVideoThumbnail(String publicId) {
        return cloudinary.url()
                .resourceType("video")
                .format("jpg")
                .transformation(
                        new Transformation()
                                .startOffset("1")
                                .width(400)
                                .height(300)
                                .crop("fill")
                )
                .generate(publicId);
    }

}
