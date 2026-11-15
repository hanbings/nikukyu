{ lib, rustPlatform }:

rustPlatform.buildRustPackage {
  pname = "nikukyu-api";
  version = (lib.importTOML ../nikukyu-api/Cargo.toml).package.version;

  # Keep local configuration, private keys, and build artifacts out of the source.
  src = lib.fileset.toSource {
    root = ../nikukyu-api;
    fileset = lib.fileset.unions [
      ../nikukyu-api/Cargo.toml
      ../nikukyu-api/Cargo.lock
      ../nikukyu-api/src
      ../nikukyu-api/models/Cargo.toml
      ../nikukyu-api/models/src
      ../nikukyu-api/migration/Cargo.toml
      ../nikukyu-api/migration/src
    ];
  };
  cargoLock.lockFile = ../nikukyu-api/Cargo.lock;

  cargoBuildFlags = [
    "--workspace"
    "--bins"
  ];
  cargoTestFlags = [ "--workspace" ];

  postInstall = ''
    mv "$out/bin/migration" "$out/bin/nikukyu-migration"
  '';

  meta = {
    description = "Nikukyu OAuth and OpenID Connect server";
    mainProgram = "nikukyu-api";
    platforms = lib.platforms.unix;
  };
}
