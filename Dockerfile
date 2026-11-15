# syntax=docker/dockerfile:1

FROM rust:1.97.0-slim-bookworm AS builder

WORKDIR /src
ARG TARGETARCH

COPY nikukyu-api/ ./

RUN --mount=type=cache,target=/usr/local/cargo/registry,sharing=locked \
    --mount=type=cache,target=/usr/local/cargo/git,sharing=locked \
    --mount=type=cache,id=nikukyu-target-${TARGETARCH},target=/src/target,sharing=locked \
    cargo build --locked --release --workspace --bins \
    && install -D -m 0755 target/release/nikukyu-api /build-output/nikukyu-api \
    && install -D -m 0755 target/release/migration /build-output/nikukyu-migration

FROM debian:bookworm-slim AS runtime

RUN groupadd --gid 10001 nikukyu \
    && useradd --uid 10001 --gid nikukyu --no-create-home --no-log-init \
       --shell /usr/sbin/nologin nikukyu

# The Rust builder already includes Debian's CA certificates.
COPY --from=builder /etc/ssl/certs/ /etc/ssl/certs/
COPY --from=builder /usr/share/ca-certificates/ /usr/share/ca-certificates/
COPY --from=builder /build-output/ /usr/local/bin/

ENV NIKUKYU_CONFIG=/etc/nikukyu/config.toml \
    NIKUKYU__SERVER__HOST=0.0.0.0

USER 10001:10001
EXPOSE 8080

CMD ["nikukyu-api"]
