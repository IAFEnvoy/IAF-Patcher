# Issue #18 / #20 修复与验证

目标工程：`E:/Java/IAF-Patcher`，`stonecutter` 分支，版本号保留 `2.0-beta.1`。

## 实施范围

| Issue | 新增 Mixin | 覆盖范围 |
| --- | --- | --- |
| [#18 羽蛇下骑后持续升空](https://github.com/IAFEnvoy/IAF-Patcher/issues/18) | `AmphithereFlightMixin` | 1.16.5、1.18.2、1.19.2、1.20.1 |
| [#20 海马浮在水面](https://github.com/IAFEnvoy/IAF-Patcher/issues/20) | `HippocampusSwimmingMixin` | 仅 1.20.1；旧三版没有这个控制器及无条件上浮逻辑 |

羽蛇在没有乘客时清理控制字节及继承的横向、垂直移动输入。客户端下骑后不会再发送该坐骑的按键释放包，旧输入可能一直留在服务端；原有起飞判断又没有要求骑手存在，会使残留的上升位绕过吹笛后的起飞冷却。

1.16.5–1.19.2 还有一个版本差异：自主飞行在原版重力和垂直阻尼计算之后增加 `0.08` 升力，导致持续上漂。补丁在无人骑乘且不接触水/岩浆时，将补偿改为 `0.08 × 0.98`；关闭重力时不再增加这段升力。骑乘和液体中的既有升力保留。

自主游荡选点仍沿用原逻辑，仅将高于当地地表 16 格的结果压回该高度，为高空羽蛇提供下降目标。高度图包含水面并忽略树叶；额外查询只读取已加载区块。跟随主人及家园盘旋在另一方法中指定的高度保留，不给玩家骑乘设置高度上限。

海马的 `HippoMoveControl.updateSpeed` 不再无条件增加 `0.005` 向上速度。仅在无人骑乘、没有坐下、没有活动路径且接近水面时，给予 `0.015` 的小幅向下加速度；深水保持中性，陆地处理沿用原方法。水深判断只查询上方两格及下方一格的流体，不逐 tick 扫描水柱。原有骑乘升降与气泡柱免疫配置继续使用。

## 回归方法

| Minecraft | 正式构建 | 本轮服务端回归 |
| --- | --- | --- |
| 1.16.5 | 通过 | 开发映射冲突已绕过；三个恐惧生物缩放读写/存档检查、6 / 6 羽蛇检查通过 |
| 1.18.2 | 通过 | 6 / 6 羽蛇检查通过 |
| 1.19.2 | 通过 | 6 / 6 羽蛇检查通过 |
| 1.20.1 | 通过 | 8 / 8 本轮检查、9 / 9 既有简单修复检查通过；9 / 9 既有小功能检查连续两次通过 |

```powershell
$env:JAVA_HOME = 'D:/Program Files/Microsoft/jdk-21.0.8.9-hotspot'
.\scripts\test-movement.ps1 -MinecraftVersion 1.16.5
.\scripts\test-movement.ps1 -MinecraftVersion 1.18.2
.\scripts\test-movement.ps1 -MinecraftVersion 1.19.2
.\scripts\test-movement.ps1 -MinecraftVersion 1.20.1
```

脚本使用 `-PmovementRegression`，在 `build/movement-regression/<MC版本>/` 中启动隔离的 Forge 专服。测试使用变换后的真实上游实体、控制器、导航和世界，主动注入残留输入并推进实体 tick；无需连接客户端。检查完毕后自动关闭测试服务器。世界和日志属于本地生成物。

羽蛇的 6 项检查覆盖：下骑清理、吹笛落地后的残留起飞输入、保留骑乘输入、开/关重力时的残留移动输入、陆地/水面上方的高空选点，以及启用正常 AI 后从高空下降。1.20.1 再增加 2 项海马检查，覆盖深水闲置、水面下潜、坐下、骑乘和活动路径。

最初的 1.20.1 基线测试尚未启用这两个 Mixin：注入 `yya = 1` 和初始向上速度后，羽蛇在 160 次实体 tick 中从 Y=180 上升至约 Y=330，垂直输入始终未清理；闲置海马从 Y≈94 浮到 Y≈100。基线还确认下骑控制字节保持为 15、高空选点保持在 Y=220。基线中“骑乘时保留输入”检查正常通过。

完整通过记录及正式包校验见 [验证记录](issues-18-20-validation.json)。这不是玩家客户端、多人联机或长期生存测试，也没有测量 FPS/MSPT。

既有小功能套件第一次复查时，龙食恢复生长检查单次失败；随后只增强断言的诊断信息，没有修改龙食产品代码，连续两次复跑全部通过。单次失败原因尚未确定，保留为后续重复测试的观察项。

## 手动复测

1. 在陆地和海面上空分别骑乘羽蛇上升，保持上升键时下骑，观察其后续高度；再吹笛让它落地，确认不会立即再次被残留输入拉起。
2. 将无人骑乘的羽蛇放到明显高于地面的空中，确认它会逐渐回到较低空域；再检查主人跟随、家园盘旋及正常骑乘升降。
3. 在没有气泡柱的深水区域放置海马，观察闲置海马不会持续浮到水面，水面附近会小幅下潜；骑乘检查上升和下降，再用食物引诱它，确认导航仍正常。

## 发布构建

回归参数会将自动关服的测试启动器加入主输出。发布构建应在测试完成后逐版本 `clean`，并去掉所有回归参数：

```powershell
.\gradlew.bat :1.16.5:clean :1.18.2:clean :1.19.2:clean :1.20.1:clean :1.16.5:build :1.18.2:build :1.19.2:build :1.20.1:build '-Dorg.gradle.jvmargs=-Xmx2G' --no-parallel --max-workers=2 --offline --console=plain
```

产物位于 `versions/<MC版本>/build/libs/iaf_patcher-2.0-beta.1-<MC版本>.jar`。发布前检查 JAR 中没有 `com/iafenvoy/iafpatcher/regression/` 类。

## 1.16.5 开发映射冲突修复（2026-11-13）

此前的阻断已经解决。原始发布 JAR 没有重复方法：`EntityDreadBeast`、`EntityDreadGhoul` 和 `EntityDreadScuttler` 各自包含一个数据驱动的 `getScale()`，以及一个调用前者的原版 `func_213355_cm()` 覆盖方法。ForgeGradle 的 official 映射将后者也改为 `getScale()`，生成两个同名同签名的方法，导致 JVM 加载失败。

新增 `dev.LegacyScaleMappingMixin` 作为这三个类的空目标钩子。插件只在 1.16.5、`FMLEnvironment.production == false` 时注册它，在 Mixin 的 `preApply` 中精确删除冲突的转发包装。删除要求目标恰好有两个 `getScale()F`，其中恰好一个的有效字节码为 `ALOAD 0; INVOKEVIRTUAL <同类>.getScale()F; FRETURN`；数据读取方法保留，遇到其他冲突结构则报错。

该处理不修改原始依赖、Gradle 映射缓存或存档；正式游戏环境不注册这个修复 Mixin。它处理的是开发环境映射冲突，不能据此声称原始发布 JAR 本身有相同启动错误。

真实 1.16.5 Forge 专服现已成功启动。验证确认三个恐惧生物都只剩一个缩放 getter，可以设置缩放，通过 `LivingEntity.getScale()` 读取，并按上游各自的 `Scale` / `DreadScale` 键保存及恢复；随后 6 项羽蛇回归全部通过。日志位于 `build/dev-mapping-repair/movement-1.16.5.log`。客户端渲染与多人联机尚未验证。
