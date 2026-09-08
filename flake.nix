{
  description = "Nikukyu Actix and React development environment";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    {
      nixpkgs,
      ...
    }:
    let
      supportedSystems = [
        "aarch64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];

      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
    in
    {
      devShells = forAllSystems (
        system:
        let
          pkgs = import nixpkgs { inherit system; };
          seaOrmCli = pkgs.rustPlatform.buildRustPackage rec {
            pname = "sea-orm-cli";
            version = "2.0.2";

            src = pkgs.fetchCrate {
              inherit pname version;
              hash = "sha256-tkyZSsTE1a08AIif5NNkBazASs+pvBgP69CnZhEZkhw=";
            };

            cargoHash = "sha256-4+rFHOBRyUGF6DXxT4Y54Y2s4F9MGcNF/ELWj/4fPWo=";

            nativeBuildInputs = [ pkgs.pkg-config ];
            buildInputs = [ pkgs.openssl ];
          };
        in
        {
          default = pkgs.mkShell {
            packages = [
              # Rust and Actix Web development.
              pkgs.cargo
              pkgs.clippy
              pkgs.rust-analyzer
              pkgs.rustc
              pkgs.rustfmt
              pkgs.cargo-edit
              pkgs.cargo-watch
              pkgs.pkg-config
              pkgs.openssl
              seaOrmCli

              # React and TypeScript development. App dependencies stay local.
              pkgs.nodejs_24
              pkgs.pnpm

              # Shared project tooling.
              pkgs.git
              pkgs.just
            ];

            RUST_BACKTRACE = "1";
            RUST_SRC_PATH = "${pkgs.rustPlatform.rustLibSrc}";
            DEV_ENV_NAME = "nikukyu";

            shellHook = ''
              echo "Rust: $(rustc --version)"
              echo "Node.js: $(node --version)"
              echo "pnpm: $(pnpm --version)"
            '';
          };
        }
      );

      formatter = forAllSystems (
        system:
        let
          pkgs = import nixpkgs { inherit system; };
        in
        pkgs.nixfmt
      );
    };
}
