package com.discount_backend.Discount_backend.entity.user;

import com.discount_backend.Discount_backend.entity.City;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "id")           // this column doubles as the FK
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                        // “profile.id = user.id”
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    public UserProfile() {}

    /** called by User’s constructor **/
    public UserProfile(User user) {
        this.user = user;
    }

    // getters/setters…


}
