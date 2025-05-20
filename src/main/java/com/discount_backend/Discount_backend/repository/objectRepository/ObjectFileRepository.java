package com.discount_backend.Discount_backend.repository.objectRepository;

import com.discount_backend.Discount_backend.entity.objectfiles.ObjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectFileRepository extends JpaRepository<ObjectFile, Long> {
    List<ObjectFile> findByObjectTypeIdAndObjectId(int objectTypeId, Long objectId);
    Optional<ObjectFile> findFirstByObjectTypeIdAndObjectIdAndIsPrimaryTrueAndActiveTrue(
            int objectTypeId, Long objectId);
}
