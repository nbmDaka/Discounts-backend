package com.discount_backend.Discount_backend.entity.objectfiles;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "object_file")
public class ObjectFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "object_type_id", nullable = false)
    private int objectTypeId;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}
