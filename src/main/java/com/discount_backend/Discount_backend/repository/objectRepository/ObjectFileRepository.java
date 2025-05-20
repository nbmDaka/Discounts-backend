package com.discount_backend.Discount_backend.repository.objectRepository;

import com.discount_backend.Discount_backend.entity.objectfiles.ObjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectFileRepository extends JpaRepository<ObjectFile, Long> {
    List<ObjectFile> findByObjectTypeIdAndObjectId(int objectTypeId, Long objectId);
}
