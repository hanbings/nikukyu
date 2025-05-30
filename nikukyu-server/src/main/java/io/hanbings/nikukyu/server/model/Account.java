package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accounts")
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "uuid", nullable = false)
    UUID uuid;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    long createdAt;
    @Column(name = "verified")
    @JsonProperty("verified")
    boolean verified;
    @Column(name = "username")
    @JsonProperty("username")
    @Nullable
    String username;
    @Column(name = "nickname")
    @JsonProperty("nickname")
    @Nullable
    String nickname;
    @Column(name = "avatar")
    @JsonProperty("avatar")
    @Nullable
    String avatar;
    @Column(name = "email")
    @JsonProperty("email")
    @NotNull
    String email;
    @Column(name = "background")
    @JsonProperty("background")
    @Nullable
    String background;
    @Column(name = "color")
    @JsonProperty("color")
    @Nullable
    String color;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID();
        this.createdAt = TimeUtils.getMilliUnixTime();
    }
}
