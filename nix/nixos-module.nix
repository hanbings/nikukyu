{ self }:
{
  config,
  lib,
  pkgs,
  ...
}:

let
  cfg = config.services.nikukyu;
in
{
  options.services.nikukyu = {
    enable = lib.mkEnableOption "the Nikukyu API server";

    package = lib.mkOption {
      type = lib.types.package;
      default = self.packages.${pkgs.stdenv.hostPlatform.system}.nikukyu-api;
      defaultText = lib.literalExpression "nikukyu.packages.\${pkgs.stdenv.hostPlatform.system}.nikukyu-api";
      description = "The Nikukyu package to run.";
    };

    configFile = lib.mkOption {
      type = lib.types.nullOr lib.types.str;
      default = null;
      example = "/run/secrets/nikukyu/config.toml";
      description = ''
        Absolute path to the existing Nikukyu TOML configuration. Use a string
        path outside the Nix store if the configuration contains secrets. The
        file and the OIDC signing key it references must be readable by the
        nikukyu user. Paths under /home are hidden from the service.
      '';
    };
  };

  config = lib.mkIf cfg.enable {
    assertions = [
      {
        assertion = cfg.configFile != null && lib.hasPrefix "/" cfg.configFile;
        message = "services.nikukyu.configFile must be an absolute path to a configuration file";
      }
    ];

    users.users.nikukyu = {
      isSystemUser = true;
      group = "nikukyu";
      description = "Nikukyu API service user";
    };
    users.groups.nikukyu = { };

    systemd.services.nikukyu = {
      description = "Nikukyu OAuth and OpenID Connect server";
      wantedBy = [ "multi-user.target" ];
      wants = [ "network-online.target" ];
      after = [
        "network-online.target"
        "postgresql.service"
        "postgresql-setup.service"
      ];

      environment.NIKUKYU_CONFIG = if cfg.configFile == null then "" else cfg.configFile;

      serviceConfig = {
        Type = "simple";
        User = "nikukyu";
        Group = "nikukyu";
        ExecStart = lib.getExe' cfg.package "nikukyu-api";
        Restart = "on-failure";
        RestartSec = "5s";
        NoNewPrivileges = true;
        PrivateTmp = true;
        ProtectHome = true;
        ProtectSystem = "strict";
      };
    };
  };
}
