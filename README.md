# CrackedGuard

A Spigot/Paper plugin that:

1. Tags cracked (offline-mode) players' names with a `+` prefix on join.
2. Tags Bedrock/Pocket Edition players' names with a `.` prefix on join (via Floodgate).
3. Kicks any cracked player who has OP, unless their name is in the config exception list.
4. Kicks a cracked player who runs a "restricted" (admin-only) command, unless exempt.
5. Sends "Server is Restarting" to your Discord server via DiscordSRV when the plugin/server stops.
6. Provides `/cgexcept` to manage the exception list in-game.

## How "cracked" is detected

A player counts as cracked if their UUID is **version 3** (name-based, the kind
generated for offline-mode players via
`UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes())`).
Real Mojang-authenticated accounts are always issued a **version 4** (random)
UUID. This works whether your server itself runs `online-mode: false`, or
sits behind a proxy (Velocity/Bungee) that forwards both premium and
non-premium connections. Bedrock players are checked first via Floodgate's
API so they're never mistaken for "cracked."

## Commands

| Command | Description | Permission |
|---|---|---|
| `/cgexcept add <player>` | Add a player to the exception list | `crackedguard.admin` |
| `/cgexcept remove <player>` | Remove a player from the exception list | `crackedguard.admin` |
| `/cgexcept list` | Show the current exception list | `crackedguard.admin` |
| `/cgnotify` | Manually send the Discord restart message | `crackedguard.admin` |

`crackedguard.admin` defaults to OP.

## Config (`config.yml`)

```yaml
exceptions:
  - "exampleplayer"

restricted-commands:
  - op
  - deop
  - stop
  - ban
  - ban-ip
  - pardon
  - pardon-ip
  - whitelist
  - kick
  - gamemode
  - reload
  - plugins
  - pl
  - restart

kick-messages:
  op-kick: "&cCracked/offline-mode accounts are not allowed to have OP on this server."
  command-kick: "&cThat command is not available to cracked accounts."

discord:
  enabled: true
  restart-message: "Server is Restarting"
```

- Add/remove entries under `restricted-commands` to fit whatever admin
  commands your server actually uses (works with any plugin's commands,
  including `pluginname:command` aliases).
- `exceptions` is easiest to manage with `/cgexcept` rather than editing
  the file directly (no reload needed that way).

## Soft-dependencies

- **Floodgate** — if not installed, Bedrock detection simply always returns
  `false` (no errors). Install Geyser + Floodgate as normal if you want
  Bedrock support.
- **DiscordSRV** — if not installed, the plugin skips sending Discord
  messages entirely, and `/cgnotify` will tell you it's not available.
  Make sure DiscordSRV's `MainTextChannel` is configured in DiscordSRV's own
  config, since that's the channel this plugin posts to.

## Building

Requires Java 17+ and Maven.

```bash
mvn clean package
```

The built jar will be at `target/CrackedGuard.jar` — drop it into your
server's `plugins/` folder along with Floodgate/DiscordSRV if you want those
integrations, then start the server once to generate `config.yml`.

## Notes / caveats

- The `+`/`.` name prefix is applied via `setDisplayName` /
  `setPlayerListName`. This affects the tab list and any chat plugin that
  actually uses `%displayname%` in its format — if you use a chat plugin
  with its own placeholder system (e.g. one based purely on
  `%player_name%` from PlaceholderAPI), you may need to point its format at
  the display name instead, or swap to a placeholder like
  `%player_displayname%`.
- The OP-kick (feature 3) happens at login, before the player fully joins
  the world — so it won't show a join message for kicked players.
- The restricted-command kick (feature 4) matches on the *base* command
  word only (e.g. `gamemode`), not arguments, and ignores a `plugin:`
  prefix if used (e.g. `/essentials:gamemode`).
