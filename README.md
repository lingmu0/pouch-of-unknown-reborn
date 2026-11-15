![Pouch of Unknown icon](media/icon.png)

# Pouch of Unknown: Reborn

An unofficial port of [Pouch of Unknown](https://github.com/TCreopargh/PouchOfUnknown) by TCreopargh, adapted for AStages. This project keeps the original MIT license and credits.

The pouch collects items that AStages prevents a player from picking up or storing. Right-click the pouch to retrieve items once their restrictions are lifted; sneak-right-click also drops unlocked overflow when the inventory is full. Each pouch stores up to 540 stacks by default, configurable from 1 to 4096. If Curios is installed, players can equip the pouch in its dedicated slot.

| Branch | Minecraft | Loader | Java |
| --- | --- | --- | --- |
| `mc-1.20.1` | 1.20.1 | Forge | 17 |
| `mc-1.21.1` | 1.21.1 | NeoForge | 21 |

Install the mod and AStages 2.5.3 (including AStages' prerequisites) on both client and server. Curios is optional. The port was compiled and its storage-packing logic checked, but it has not yet been tested in a running game. The NeoForge artifact was built against locally cached development files because the upstream repository timed out; its complete Gradle build remains unverified.

Pouch contents are not automatically migrated between 1.20.1 and 1.21.1 or from the original 1.12.2 release. Empty pouches and back up your world before upgrading.

---

# 未知之袋：重生

这是 [Pouch of Unknown](https://github.com/TCreopargh/PouchOfUnknown) 的非官方移植，原作者为 TCreopargh，原版许可和贴图随项目保留。模组 ID 和物品 ID 保持为 `pouchofunknown` 和 `pouchofunknown:pouch`。源码按游戏版本存放在两个 Git 分支及工作目录：`mc-1.20.1`（Forge）和 `mc-1.21.1`（NeoForge）。

## 安装

- 客户端和服务器均需安装与游戏、加载器相匹配的本模组，以及 **AStages 2.5.3** 对应版本及其前置。
- **Curios 为可选依赖**：1.20.1 使用 Curios 5.9.1 或更高的同游戏版本，1.21.1 使用 Curios 9.5.1 或更高的同游戏版本。安装后会提供一个专门的未知之袋饰品槽，也可以把袋子放在物品栏。
- 与 AStages 2.5.3 的物品限制接口对接；其他 AStages 版本未验证。

## 玩法

三个皮革和一个金锭合成未知之袋：中间一行放「皮革、金锭、皮革」，下一行中间放一个皮革。AStages 禁止拾取或存放在物品栏中的物品会优先放入持有或装备的袋子。袋子默认可以储存 540 组；`pouchofunknown-common.toml` 的 `pouchCapacity` 可以调整为 1–4096。

手持袋子右键，会把可拾取且允许放入物品栏的物品取出。物品栏满时，剩余物品留在袋子内；潜行右键会把放不下的已解锁物品丢在地上。没有袋子或袋子已满时，地面物品不会被吞掉；从物品栏转移失败的部分会掉落在地面。已经装备的 Curios 物品如果受到 AStages 物品装备限制，也会被移入袋子或掉落。仅限制使用、但允许持有和装备的物品不会被收走。

袋子各自保存内容，不会把物品放进玩家共享的末影箱。1.20.1 与 1.21.1 的物品存储格式不同；不保证直接升级世界时迁移袋内物品，升级前请先取出并备份。旧版 1.12.2 的配置项和袋内存档也没有自动迁移。

## 构建与验证

使用 Java 21，在本目录运行 `gradlew.bat build`。产物位于 `build/libs/pouchofunknown-neoforge-2.0.0-1.21.1.jar`。本工作区的 NeoForge 仓库请求超时，因此现有产物使用本机 NeoForge 21.1.233 开发缓存和官方 AStages、Curios API 编译后打包，完整 Gradle 构建尚未验证。

可在不启动游戏的情况下运行下列数量守恒检查（PowerShell）：

```powershell
New-Item -ItemType Directory -Force build/verification | Out-Null
javac -encoding UTF-8 -d build/verification src/main/java/xyz/tcreopargh/pouchofunknown/StackPacking.java verification/StackPackingCheck.java
java -cp build/verification xyz.tcreopargh.pouchofunknown.StackPackingCheck
```

检查覆盖合并、容量、无法解析的条目和随机输入的数量守恒。没有启动游戏或服务器进行实际交互测试。
