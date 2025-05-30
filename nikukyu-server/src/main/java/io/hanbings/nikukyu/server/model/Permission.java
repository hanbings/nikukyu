package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "permissions")
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "uuid", nullable = false)
    UUID uuid;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false)
    long createdAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID();
        this.createdAt = TimeUtils.getMilliUnixTime();
    }
}
