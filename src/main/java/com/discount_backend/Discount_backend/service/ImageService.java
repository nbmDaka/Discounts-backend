package com.discount_backend.Discount_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectFile;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.repository.objectRepository.ObjectFileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Service
public class ImageService {
    private final Cloudinary cloudinary;
    private final ObjectFileRepository fileRepo;

    public ImageService(Cloudinary cloudinary, ObjectFileRepository fileRepo) {
        this.cloudinary = cloudinary;
        this.fileRepo = fileRepo;
    }

    public String uploadAndSave(Long objectId, ObjectType type, MultipartFile file) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String url = result.get("secure_url").toString();

        ObjectFile metadata = new ObjectFile();
        metadata.setObjectTypeId(type.getId());
        metadata.setObjectId(objectId);
        metadata.setImageUrl(url);
        metadata.setPrimary(true);
        fileRepo.save(metadata);

        return url;
    }

    public void disableImageByObject(Long objectId, ObjectType type) {
        ObjectFile file = fileRepo
                .findFirstByObjectTypeIdAndObjectIdAndIsPrimaryTrueAndActiveTrue(
                        type.getId(), objectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No active image for " + type + " id=" + objectId));
        file.setActive(false);
        fileRepo.save(file);
    }

    public String getActiveImageUrl(ObjectType type, Long referenceId) {
        return fileRepo
                .findFirstByObjectTypeIdAndObjectIdAndIsPrimaryTrueAndActiveTrue(
                        type.getId(), referenceId
                )
                .map(ObjectFile::getImageUrl)    // <-- return the stored Cloudinary URL
                .orElse(null);
    }
}