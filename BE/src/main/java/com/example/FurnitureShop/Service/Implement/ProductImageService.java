package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Response.ProductImageResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.ProductImage;
import com.example.FurnitureShop.Model.ProductVariant;
import com.example.FurnitureShop.Repository.ProductImageRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@CacheConfig("product_images")
public class ProductImageService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;

    @CacheEvict(allEntries = true)
    public List<ProductImageResponse> createProductImage(Long variantId, List<MultipartFile> files) throws IOException {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant không tồn tại"));

        files = files == null ? new ArrayList<>() : files;

        List<MultipartFile> validFiles = files.stream()
                .filter(f -> f.getSize() > 0)
                .toList();

        if (validFiles.size() > 5) {
            throw new AuthException( "Không được quá 5 ảnh!");
        }

        List<String> uploadedFiles = new ArrayList<>();
        List<ProductImageResponse> response = new ArrayList<>();
        try {
            for (MultipartFile file : validFiles) {

                if (file.getSize() > 10 * 1024 * 1024) {
                    throw new AuthException("File quá lớn (tối đa 10MB)");
                }

                if (!isValidImageFile(file)) {
                    throw new AuthException("File không phải ảnh hợp lệ");
                }

                String filename = storeFile(file);
                uploadedFiles.add(filename);

                ProductImage productImage = ProductImage.builder()
                        .productVariant(variant)
                        .url(filename) // ✅ LƯU TÊN FILE MỚI
                        .build();

                response.add(ProductImageResponse.fromEntity(productImage));
                productImageRepository.save(productImage);// ✅ SAVE DB
            }

            return response;

        } catch (Exception ex) {

            // ✅ rollback file đã upload
            for (String file : uploadedFiles) {
                deleteFile(file);
            }

            throw new RuntimeException("Upload thất bại: " + ex.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Invalid filename");
        }

        // Lấy extension an toàn
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
            // Validate extension
            if (!isAllowedExtension(extension)) {
                throw new IOException("File extension not allowed");
            }
        }

        // Tạo tên file mới hoàn toàn (không dùng tên cũ)
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Đảm bảo thư mục uploads tồn tại
        java.nio.file.Path uploadPath = Paths.get("src/main/resources/uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Resolve path an toàn (prevent path traversal)
        java.nio.file.Path destination = uploadPath.resolve(uniqueFilename).normalize();

        // Kiểm tra destination vẫn nằm trong uploadPath
        if (!destination.startsWith(uploadPath)) {
            throw new IOException("Invalid file path");
        }

        // Copy file
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isValidImageFile(MultipartFile file) {
        // Kiểm tra content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        // Kiểm tra magic bytes (optional nhưng recommended)
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 4) return false;

            // Kiểm tra signature của các định dạng ảnh phổ biến
            return isValidImageSignature(bytes);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isValidImageSignature(byte[] bytes) {
        // PNG: 89 50 4E 47
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return true;
        }
        // JPEG: FF D8 FF
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return true;
        }
        // GIF: 47 49 46
        if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46) {
            return true;
        }
        // WebP: 52 49 46 46 ... 57 45 42 50
        if (bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46) {
            return bytes.length >= 12 && bytes[8] == 0x57 && bytes[9] == 0x45 && bytes[10] == 0x42 && bytes[11] == 0x50;
        }
        return false;
    }

    private boolean isAllowedExtension(String extension) {
        String lowerExt = extension.toLowerCase();
        return lowerExt.equals(".jpg") || lowerExt.equals(".jpeg") ||
                lowerExt.equals(".png") || lowerExt.equals(".gif") ||
                lowerExt.equals(".webp");
    }

    private void deleteFile(String filename) {
        try {
            java.nio.file.Path filePath = Paths.get("uploads").resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error nhưng không throw exception
            System.err.println("Failed to delete file: " + filename);
        }
    }
}
