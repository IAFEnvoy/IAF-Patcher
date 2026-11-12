# 新功能配置与食物标签

本轮仅加入食物标签、普通龙食恢复生长、海马气泡柱免疫。四个目标版本为 Forge 1.16.5、1.18.2、1.19.2、1.20.1；没有加入龙血配方、刷鳞片、船交互或其他候选功能。

## 服务端配置

进入世界后 Forge 自动生成 `iaf_patcher-server.toml`：

- 单人：`saves/<存档>/serverconfig/iaf_patcher-server.toml`。
- 专服：`<level-name>/serverconfig/iaf_patcher-server.toml`，默认是 `world/serverconfig/iaf_patcher-server.toml`。
- 新建世界的默认配置可放在游戏 / 服务端根目录的 `defaultconfigs/iaf_patcher-server.toml`；已有世界请直接修改对应 `serverconfig` 文件。

这是 Forge 的 SERVER 配置，由服务端同步到客户端。修改并重新进入世界 / 重启服务器即可使用。可直接参考 [示例配置](../examples/iaf_patcher-server.toml)：

```toml
[features]
dragonMealRestoresGrowth = true
hippocampusBubbleColumnImmunity = false
```

| 配置项 | 默认值 | 效果 |
| --- | --- | --- |
| `dragonMealRestoresGrowth` | `true` | 主人给停止生长的龙喂普通龙食时，解除停止生长标记并执行原有生长流程。关闭后保留原版行为。 |
| `hippocampusBubbleColumnImmunity` | `false` | 开启后海马不受气泡柱上推 / 下拉影响，骑乘时也生效；包含柱内和柱顶处理。关闭后调用原版逻辑。 |

恢复生长只作用于原有普通龙食交互分支，保留主人判断、食物消耗和回血；不会直接修改所有龙的生长函数。病变龙食仍能再次停止生长，已有的 `AgingDisabled` 保存字段继续使用。

## 自定义食物

食物列表使用原版数据包的 JSON 标签文件，可通过 `/reload` 重载。这部分不放进 TOML，以便和 1.19.2、1.20.1 自带标签保持一致，并让普通标签同步流程处理客户端判定。

| 标签 | 用途 | 默认物品 |
| --- | --- | --- |
| `iceandfire:breed_ampithere` | 羽蛇繁殖 | `minecraft:cookie` |
| `iceandfire:breed_hippocampus` | 海马繁殖 | `minecraft:prismarine_crystals` |
| `iceandfire:breed_hippogryph` | 鹰马繁殖 | `minecraft:rabbit_stew` |
| `iceandfire:heal_ampithere` | 羽蛇治疗，含拾取食物 | `minecraft:cocoa_beans` |
| `iceandfire:heal_cockatrice` | 鸡蛇治疗，含拾取食物 | `#forge:seeds`、`minecraft:rotten_flesh` |
| `iceandfire:heal_hippocampus` | 海马喂食治疗及原有概率驯服 | `minecraft:kelp` |
| `iceandfire:heal_pixie` | 精灵治疗，含拾取食物 | `minecraft:sugar` |
| `iceandfire:tame_hippogryph` | 鹰马拾取食物治疗 / 驯服 | `minecraft:rabbit_foot` |
| `iceandfire:tame_pixie` | 野生精灵拾取食物驯服 | `minecraft:cake` |

`ampithere` 是原版标签名中的实际拼写，需保留。标签只调整接受哪些物品，回血量、驯服概率、年龄条件、所有者条件和消耗方式沿用原版。

精灵拾取后按原版顺序先判断治疗、再判断驯服；配置两种标签时请为它们选择不同的物品，以保留清晰的用途。

1.16.5、1.18.2 新增局部 Mixin 接入这些标签，同时覆盖右键、繁殖资格、拾取目标筛选和精灵拾取后的效果。1.19.2、1.20.1 沿用上游已有的标签逻辑，只补上精灵搜索时仍写死蛋糕 / 糖的遗漏，不应用旧版交互 Mixin。空标签可关闭对应食物用途。

### 数据包示例

将 [custom-foods 文件夹](../examples/custom-foods) 复制到存档的 `datapacks/` 下，得到：

```text
<存档>/datapacks/custom-foods/
├─ pack.mcmeta
└─ data/iceandfire/tags/items/breed_hippocampus.json
```

示例让海马额外接受胡萝卜作为繁殖食物：

```json
{
  "replace": false,
  "values": ["minecraft:carrot"]
}
```

`replace: false` 保留默认物品并追加；改为 `true` 后，只接受当前标签中的物品。其他用途按上表标签名创建同目录下的 JSON 文件即可，也可以用 `#命名空间:标签名` 引用其他物品标签。

示例 `pack.mcmeta` 对应 1.20.1。其他版本请调整 `pack_format`：

| Minecraft | 数据包 `pack_format` |
| --- | --- |
| 1.16.5 | 6 |
| 1.18.2 | 9 |
| 1.19.2 | 10 |
| 1.20.1 | 15 |

复制或修改后运行 `/reload`，通过 `/datapack list` 确认已加载；放入当前维度并满足原有年龄 / 所有权条件后测试。

## 验证方法

自动专服测试使用独立的 `build/feature-regression/<版本>/` 世界，准备一份 `replace: true` 的测试数据包，检查九个标签、实际交互、拾取判定、移除默认食物、主人限制、恢复生长保存和两个气泡柱回调。该测试包使用宝石作为测试食物，仅存在于测试世界中，不会进入正式 JAR。

```powershell
# 使用能启动本项目 Gradle 的 JDK；编译和游戏版本由项目 toolchain 选择。
$env:JAVA_HOME = 'D:/Program Files/Microsoft/jdk-21.0.8.9-hotspot'
.\scripts\test-features.ps1 -MinecraftVersion 1.18.2
.\scripts\test-features.ps1 -MinecraftVersion 1.19.2
.\scripts\test-features.ps1 -MinecraftVersion 1.20.1
```

脚本会检查本次生成的结果文件，缺失或任一检查失败都会报错。不要仅凭 Gradle 的成功信息判断游戏测试通过。离线运行要求本机已缓存依赖。

测试后生成正式包时去掉测试参数，并清理各版本输出：

```powershell
.\gradlew.bat :1.16.5:clean :1.18.2:clean :1.19.2:clean :1.20.1:clean :1.16.5:build :1.18.2:build :1.19.2:build :1.20.1:build '-Dorg.gradle.jvmargs=-Xmx2G' --no-parallel --max-workers=2 --offline --console=plain
```

`-PfeatureRegression` 和 `-PpatchRegression` 都仅用于开发测试，会加入自动运行检查并关服的测试启动器，不能带入发布构建。

## 本次验证记录（2026-11-12）

1.18.2、1.19.2、1.20.1 的真实 Forge 专服均通过九项新功能行为回归；1.20.1 同时通过原有九项简单补丁回归。测试覆盖了 `replace: true` 移除默认物品、右键繁殖 / 治疗、掉落物筛选与精灵拾取治疗、主人限制、恢复生长开关与保存字段，以及骑乘海马时两个方向和两种气泡柱回调的开关效果。

1.16.5 仍受实施前已确认的开发映射依赖问题限制：上游映射 JAR 中 `EntityDreadScuttler` 含重复 `getScale()F`，阻断专服启动。因此本轮没有把该版记为运行验证通过。没有执行客户端或双人联机验证。

本机测试结果保存在 `build/feature-regression/<版本>/patch-regression-result.txt`，对应 Gradle / 服务端日志为 `build/small-features-regression-<版本>.log`。这些生成物不会进入 Git，测试世界中自动生成的 `world/serverconfig/iaf_patcher-server.toml` 也可用于查看 Forge 输出的配置格式。

四版正式 JAR 均构建通过，已检查九个标签、版本启用条件、气泡柱覆盖方法的重映射及测试代码排除。成品位于 `versions/<版本>/build/libs/`，版本号仍为 1.3.1。初次并行构建因 Gradle 堆内存不足失败，调整本次构建内存和并发后成功；上面的发布命令已包含对应参数。构建日志为 `build/small-features-production-final.log`，校验和与结果记录见 [验证记录](small-features-validation-2026-11-12.json)。
