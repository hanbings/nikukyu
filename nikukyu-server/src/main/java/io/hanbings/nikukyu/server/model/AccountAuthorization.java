package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accounts_authorizations")
@Table(name = "account_authorizations")
public class AccountAuthorization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "uuid", nullable = false)
    UUID uuid;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false)
    long createdAt;
    @JsonProperty("created_by")
    @Column(name = "created_by", nullable = false)
    @NotNull Long createdBy;

    @JsonProperty("provider")
    @Column(name = "provider", nullable = false)
    @NotNull String provider;
    @JsonProperty("openid")
    @Column(name = "openid", nullable = false)
    @NotNull String openid;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID();
        this.createdAt = TimeUtils.getMilliUnixTime();
    }
}
