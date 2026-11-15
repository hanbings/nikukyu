# Nikukyu

Nikukyu is an OAuth and OpenID Connect server. The API requires PostgreSQL and
a persistent RSA private key for signing OIDC tokens.

Configure the API using [`example.config.toml`](nikukyu-api/example.config.toml).
The database and its user must exist before applying the migrations described in
the [migration guide](nikukyu-api/migration/README.md). The API reads the
database URL from its TOML configuration; the migrator uses `DATABASE_URL`.

## Docker

Build the API image from the repository root with Docker BuildKit:

```sh
docker build -t nikukyu-api .
```

The build context includes only `nikukyu-api/`, excluding build artifacts and
common configuration and key files. Keep other secrets outside that directory.

The image includes both `nikukyu-api` and `nikukyu-migration`. Before starting
the API, create a PostgreSQL database and run the migrations with `DATABASE_URL`
set to its connection string:

```sh
docker run --rm --env DATABASE_URL nikukyu-api nikukyu-migration up
```

If you do not already have a persistent OIDC signing key, generate one once:

```sh
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out oidc-private.pem
```

Prepare a TOML configuration based on
[`example.config.toml`](nikukyu-api/example.config.toml). Set `database.url`
to the same database and `oidc.signing_key_file` to
`/run/secrets/oidc-private.pem`. Set `server.port` to `8080` or adjust the port
mapping below. Mount the configuration and persistent signing key when starting
the API, using absolute host paths:

```sh
docker run -d --name nikukyu-api \
  -p 127.0.0.1:8080:8080 \
  --mount type=bind,src=/absolute/path/config.toml,dst=/etc/nikukyu/config.toml,readonly \
  --mount type=bind,src=/absolute/path/oidc-private.pem,dst=/run/secrets/oidc-private.pem,readonly \
  nikukyu-api
```

The image runs as UID and GID `10001`; both mounted files must be readable by
that user. If both files belong to your current Linux user, add
`--user "$(id -u):$(id -g)"` to the API's `docker run` command instead. The
default `NIKUKYU__SERVER__HOST=0.0.0.0` makes the API reachable through
Docker's port mapping and can be overridden if necessary.
PostgreSQL must be reachable from inside the container: `localhost` refers to
the container itself. If PostgreSQL is in a Docker network, add the appropriate
`--network` to both commands and use its network hostname in both database URLs.
The image does not create the database or run migrations automatically.

## User interface

`nikukyu-web` is the account and authorization UI. Both frontend projects use
Node.js 26, npm and Rsbuild. TypeScript is held to 6.0.x while the current
`typescript-eslint` release supports versions below 6.1. The npm overrides keep
its transitive `minimatch` on a fixed release. For local development, start the
API on port 8080,
then run:

```sh
cd nikukyu-web
npm ci
npm run dev
```

Open <http://localhost:5173>. The development server proxies `/api/v1/`,
`/oidc/` and `/.well-known/` to `http://127.0.0.1:8080`; set
`NIKUKYU_API_TARGET` to change the upstream. For same-origin development, set
both `frontend.origin` and `oidc.issuer` to `http://localhost:5173` in the API
configuration. Register the GitHub callback at that origin if GitHub login is
enabled. `nikukyu-dashboard` can also be started with `npm ci && npm run dev`;
it is a separate, currently minimal developer-facing application.

Build the user UI image with its own restricted build context:

```sh
docker build -t nikukyu-web nikukyu-web
```

The web image serves the SPA and proxies the API and OIDC paths to
`nikukyu-api:8080`. Attach both containers to the same Docker network and give
the API container that name or network alias, for example:

```sh
docker network create nikukyu
docker network connect nikukyu nikukyu-api
docker run -d --name nikukyu-web --network nikukyu -p 127.0.0.1:3000:80 nikukyu-web
```

Put an HTTPS reverse proxy in front of the web container and set both the API's
`frontend.origin` and `oidc.issuer` to the public HTTPS origin. This keeps the
browser, session cookies and OIDC endpoints on the same origin. GitHub's
registered callback must be `<origin>/api/v1/sessions/github/callback`.

The API's login rate limiter uses the direct peer address. Behind a reverse
proxy, requests therefore share the proxy's limit; account for this when
setting `auth.login_attempts_per_minute`. The API does not trust forwarded IP
headers supplied by clients.
