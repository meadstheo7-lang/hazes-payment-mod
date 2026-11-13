# Haze's Payment Mod

A client-side Fabric mod for Minecraft 26.2 that tracks incoming `/pay` bets (great for streams),
shows a customisable pot HUD, and gives you clickable payout buttons.

## Features
- Detects "X paid you $Y" chat messages and keeps running totals per player (supports `5k`, `2.5M`, `1B`)
- Customisable HUD: position, scale, background opacity, colours, number of recent bets
- Clickable **[PAY]** / **[REFUND]** buttons that pre-fill `/pay` (you press Enter - nothing is auto-sent)

## Commands
| Command | What it does |
|---|---|
| `/bets` | List all bets with payout buttons |
| `/bets win <player> [multiplier]` | Payout button for one winner |
| `/bets config` | Open the settings screen (live preview) |
| `/bets clear` | Reset the round |

## Config
`config/hazespaymentmod.json`. If your server words payment messages differently, edit `payRegex`
(group 1 = sender, group 2 = amount) and restart.

## Building
```
./gradlew build
```
The jar is in `build/libs/`. Requires Java 25 and Fabric API.

## Disclaimer
Check your server's rules on gambling, streaming and client mods before using this. Not affiliated with
any server or Mojang.

## License
MIT
