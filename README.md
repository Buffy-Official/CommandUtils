This is a Minecraft plugin that provides a variety of commands and placeholders with loads of configuration for server owners!

Download the latest version from the github release page or from spigotmc: https://www.spigotmc.org/resources/commandutils.117733/

List of commands:
/blockcycle {oxidize/wax} {world} {location (x y z)}
/blockgravity {world} {location} {gravity enabled}
/bonemealblock {location} {world} {amount} {radius} {affects target block}
/breakandreplant {location} {world} {player}
/breakinfacing {location} {world} {player} {radius} {depth} {whitelisted blocks} {blacklisted blocks}
/breakinradius {location} {world} {player} {radius} {depth} {whitelisted blocks} {blacklisted blocks}
/breakinxyz {location} {world} {player} {radius} {depth} {whitelisted blocks} {blacklisted blocks}
/cobwebprison {location} {world} {player} {radius} {height} - requires ExecutableItems plugin
/highlightblocks {location} {world} {radius} {block}
/itemattribute {add/remove/set} {player} {item slot} {attribute} {value} {operation} {equipment slot}
/removeitemsetvariable {player} {material} {max amount} {commands}
/runcommandlater {player} {ticks} {command}
/sendbossbar {player} {bossbar id} {bossbar color} {bossbar progress} {ticks to show} {bossbar content}
/sendmessage {player} {message}
/setitem {player} {slot} {item}
/setitemnbt {player} {slot} {namespace} {key} {content}
/spawnodamagefirework {location} {ticks to detonate} {no damage player}
/waterlogblock {location} {world} {waterlogged state} {radius}
/weightedrandom {args}
/while {player} {compare 1} {compare method} {compare 2} {initial delay} {interval} {commands} 
