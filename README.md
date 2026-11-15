# Nikukyu

Nikukyu is an OAuth and OpenID Connect server. The API and its database
migrator can be built with Nix, and the flake provides a NixOS service module.

## Nix packages

Build the API and migrator from this checkout:

```sh
nix build .#nikukyu-api
```

The result contains `bin/nikukyu-api` and `bin/nikukyu-migration`. The API is
also the default package. To build or run just the migrator entry point:

```sh
nix build .#nikukyu-migration
DATABASE_URL="$(cat /path/to/database-url)" nix run .#nikukyu-migration -- up
```

The migrator uses `DATABASE_URL`, while the API reads `database.url` from its
TOML configuration. They must point to the same PostgreSQL database. The
database and its user must exist before the migration runs. See the
[migration guide](nikukyu-api/migration/README.md) for more commands.

## NixOS module

Add this repository as a flake input:

```nix
inputs.nikukyu.url = "github:hanbings/nikukyu";
```

Then, in the host's `nixosSystem` definition (where `nikukyu` is the flake
input), add the module and service settings:

```nix
modules = [
  ./configuration.nix
  nikukyu.nixosModules.default
  {
    services.nikukyu = {
      enable = true;
      configFile = "/run/secrets/nikukyu-config.toml";
    };
  }
];
```

The module starts the API as the dedicated `nikukyu` system user and restarts
it on failure. `services.nikukyu.package` can be overridden if a different
package is required. The module neither creates a PostgreSQL database nor runs
migrations automatically; manage PostgreSQL independently and apply migrations
before starting or upgrading the service.

Provide the TOML configuration as a runtime file outside the Nix store, for
example with sops-nix or agenix. Start from
[`example.config.toml`](nikukyu-api/example.config.toml), set the database URL
and the frontend and OIDC origins, and use an **absolute** path for
`oidc.signing_key_file`. Both the configuration file and the persistent RSA
private key must be readable by the `nikukyu` user. The systemd service cannot
read files under `/home`; `/run/secrets` is one suitable location. Do not put
credentials or private keys in Nix expressions or other files copied into the
world-readable Nix store.

The service listens on the host and port set in its configuration. Configure a
reverse proxy, TLS, and firewall access separately if needed. The web and
dashboard projects are currently placeholder frontends and are not included in
these packages.
