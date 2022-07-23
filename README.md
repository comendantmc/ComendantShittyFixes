# ComendantShittyFixes

A plugin written for my 1.12.2 anarchy server. I don't recommend using any of the fixes, most of them are garbage. Pull requests are welcome.

## Config

Everything that can be disabled is disabled by default

```yml
pluginPrefix: '&b[&6CSFixes&b]&r '
configVersion: '1'
disabledClasses:
  - CharacterFilter
  - DupeStashFinder
  - StrictPositionCheck
  - LimitBlocksPerChunk
  - PacketFilter
  - StrictPositionCheck
  - WatchChunkLoading
  - CancelRemount
  - CrystalFix
  - FixContainerDupes
  - FixZombieDupe
  - NetherRoofTeleport
  - ResetSpeedAttributes
  - NameTagFilter
characterFilter:
  chatRegex: '[^A-Za-zА-Яа-яЁё0-9 !%\(\)\?\>\+\-_,\/:]+'
  anvilRegex: '[^A-Za-zА-Яа-яЁё0-9 !%\(\)\?\>\+\-_,\/:]+'
  nameTagRegex: '[^A-Za-zА-Яа-яЁё0-9 !%\(\)\?\>\+\-_,\/:]+'
  nameTagBlacklist:
    - '1 + 1'
  nameTagMinLived: 1
  replaceAllToCyrillic: false
  whitelistNicknames: false
  tellCmds:
    - tell
    - pm
    - w
    - msg
    - whisper
    - r
    - reply
    - l
    - last
  replyCmds:
    - r
    - reply
    - l
    - last
spam:
  filterNumbers: false
  maxNumber: 3
  adminUsername: 'comendantmc'
  adminPMReply: '&6Для связи с администратором используй емейл 2b2t.org.ru@gmail.com (или /r)'
killOnNetherRoof: false
crystal:
  minLived: 4
dupes:
  killZombiesWithItems: false
  preventZombieItemPickup: false
  killAnimalsWithChests: false
  preventAnimalsWithChests: false
  clearInvInteractAnimalsWithChests: false
  cancelInteractAnimalsWithChests: false
  preventDeathOfAnimalsWithChests: false
  cancelInteractFromVehicle: false
  preventContainerDupe:
    enabled: false
    minecarts: false
    hopperMinecarts: false
    horses: false
  hopperMinecartRemoveItems: false
resetPlayerSpeeds: false
limitBlocksPerChunk:
  enabled: false
  maxRedstoneDustPerSlice: 16
explicitSpawnSet: false
strictPositionCheck:
  enable: false
  reportedDistance: 900
  minimalSpawnDistance: 15000
  teleportBack: false
chunks:
  checkLoading: false
  checkDistance: 500
  minimalDistanceFromSpawn: 500
packetFilter:
  hiddenUUIDs:
    - '6b4c0d8f-a386-3051-9e7c-8b5f01b2a7ed'
telegram:
  enabled: false
  secret: 'your_secret'
  chatId: 000000000
hastebin:
  provider: 'https://www.toptal.com/developers/hastebin'
meta:
  averageTPS: 20
disableMetrics: false
```

## Acknowledgements

I might've used code or got some inspiration from the following projects: 

* [AEF](https://github.com/moom0o/AnarchyExploitFixes)
* [meteor-pvp](https://github.com/MeteorDevelopment/meteor-pvp)
* [Piston projects](https://github.com/AlexProgrammerDE?tab=repositories)
* Maybe some more, but I don't remember