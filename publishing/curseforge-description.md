# Pouch of Unknown: Reborn (AStages)

**Pouch of Unknown: Reborn (AStages)** is an unofficial port of [Pouch of Unknown by TCreopargh](https://github.com/TCreopargh/PouchOfUnknown), adapted to work with AStages on modern Minecraft versions. The original project is distributed under the MIT license; its copyright notice and license are included in this port.

## What does it do?

Carry a Pouch of Unknown to collect items that AStages prevents you from picking up or storing. When you unlock the relevant stage, hold the pouch and right-click to retrieve available items. If your inventory is full, the remaining items stay in the pouch; sneak-right-click to drop unlocked overflow instead.

- Stores up to 540 stacks per pouch by default. Set `pouchCapacity` in the common configuration to a value from 1 to 4096.
- If Curios is installed, wear the pouch in its dedicated slot. Restricted equipped Curios can also be collected by the pouch.
- Respects AStages' pickup, inventory, and equipment restrictions. Items restricted only from use are left in the inventory.
- If you have no pouch or it is full, uncollected ground items remain on the ground.

## Crafting

Place a gold ingot between two pieces of leather, with a third piece of leather in the center slot below them.

## Requirements

Install the matching mod file **on both the client and the server**:

- Minecraft 1.20.1: Forge, AStages 2.5.3, and its required dependencies. Curios 5.9.1 or newer for Minecraft 1.20.1 is optional.
- Minecraft 1.21.1: NeoForge, AStages 2.5.3, and its required dependencies. Curios 9.5.1 or newer for Minecraft 1.21.1 is optional.

These first builds are **alpha**: compilation and storage-packing checks passed, but in-game interaction has not yet been verified. The NeoForge file was compiled against locally cached development files after the NeoForge build repository timed out.

**World upgrades:** pouch contents are not automatically migrated from 1.12.2 or between 1.20.1 and 1.21.1. Empty pouches and back up your world before upgrading.
