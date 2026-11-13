# 简单补丁实施与验证（2026-11-12）

后续进展（2026-11-13）：下文记录的 1.16.5 开发映射启动阻断已解决，专服及羽蛇回归现已通过，见 [开发映射冲突修复](issues-18-20-fixes.md#1165-开发映射冲突修复2026-11-13)。本报告其余内容保留当时的验证范围。

本轮在 `E:/Java/IAF-Patcher` 的 `stonecutter` 分支中迁移小范围修复，保留当前版本号 1.3.1。新增 8 个 Mixin，使用现有 Stonecutter 条件处理四个 Forge 版本。没有提交或推送代码。

候选分析及依赖版本见 [迁移候选报告](ce-backport-candidates-2026-11-12.md) 和 [静态证据](ce-backport-evidence-2026-11-12.json)。那份报告记录实施前的状态，本报告记录实际落地与验证结果。

## 实施范围

| 新增 Mixin | 修复行为 | 覆盖版本 / 原报告 |
| --- | --- | --- |
| `ai.SearchCooldownMixin` | 将原有 `% 4 == 0` 查询门槛换为每个 Goal 独立的四 tick 截止时间；首次查询立即执行，支持时间回退，保留原查询范围和缓存列表 | 四版分别覆盖 12 / 11 / 11 / 10 个 Goal；M06 |
| `ai.DragonAIMateMixin` | 实现真正的 `canContinueToUse`，检查伴侣存活、发情、行动能力及繁殖延时 | 四版；M07 |
| `ai.SheepFollowCyclopsMixin` | 羊继续跟随存活的独眼巨人，在到达、超距或目标失效时结束 | 四版；M07 |
| `ai.PixieAIFleeMixin` | 精灵向隐藏点逃跑期间持续执行，到达、威胁失效、被驯服或空手时结束 | 四版；M07 |
| `ai.WatchClosestIgnoreRiderMixin` | 初始化观察者字段，排除观察者的乘客及坐骑 | 四版；M07 |
| `HydraHeadIndexMixin` | 伤害处理前将最后命中的头索引限制在当前头数内 | 四版；仅 M01 的实体内部防御 |
| `MultipartCollisionMixin` | 删除慢跟随分支中的第一次碰撞调用，保留移动后的公共调用 | 四版；M13 |
| `CyclopsMovementMixin` | 将破树时缩放得到的速度写回实体 | 四版；M18 的速度部分 |

搜索补丁逐版核对了目标类中的时钟调用：每个类恰好一处，只用于上述查询门槛，位于 `canUse` 或其私有 `updateList` 中。因此用单个多目标 Mixin 覆盖这两种方法布局，并设置 `allow = 1`；未使用 `require = 0` 掩盖注入失败。1.16.5 的默认调度不存在同样的奇偶相位锁死，其收益是统一独立冷却，不能描述为修复了同样的永久搜索失效。

九头蛇补丁只防止非法和过期索引进入数组访问，没有修复多部件网络包的距离、伤害或权限校验。首次索敌、网络协议、讲台、锻炉、权杖、召唤水晶、存档和客户端缓存等项目本轮未迁移。

## 实际验证

| Minecraft | 正式构建 | 专服加载全部新增目标类 | 行为回归 |
| --- | --- | --- | --- |
| 1.16.5 | 通过 | 被现有上游映射依赖阻断 | 未执行 |
| 1.18.2 | 通过 | 通过 | 未执行完整行为套件 |
| 1.19.2 | 通过 | 通过 | 未执行完整行为套件 |
| 1.20.1 | 通过 | 通过 | 9 / 9 通过 |

1.20.1 在真实 Forge 专服和变换后的上游实体/Goal 上执行了以下检查：

1. 初次查询为空后，在奇数 tick 105 找到新投放的蛋糕；tick 103 不提前刷新。
2. 四 tick 截止时间、同 tick 不重复查询，以及世界时间回退。
3. 龙繁殖的有效伴侣、延时 59/60、结束发情和空伴侣。
4. 羊跟随存活独眼巨人，到达、目标移除和空目标后停止。
5. 精灵在到达隐藏点前继续逃跑，到达、被驯服或空手后停止。
6. 观察者字段正确赋值，能选择无乘骑关系的目标，并排除自己的乘客。
7. 九头蛇头索引为 -1、9、整数极值、有效索引及头数减少后的旧索引。
8. 普通和慢跟随部件每次 tick 都只有一次碰撞调用。
9. 实际破坏树干后，独眼巨人的 X 速度由 1 变为 0.6。

另在独立的基线世界中只禁用了本轮新增的 8 个 Mixin：同样的 9 项检查全部失败，分别暴露了搜索失效、缺少冷却方法、繁殖/跟随/逃跑条件错误、观察者未初始化、九头蛇数组越界、慢部件碰撞调用两次和减速丢失。恢复 Mixin 配置后再次运行，9 项全部通过。

1.16.5 在实体注册阶段遇到 `ClassFormatError: Duplicate method name "getScale" with signature "()F"`，来自 `EntityDreadScuttler`。`javap` 确认开发环境的上游映射 JAR 本身包含两个同签名方法，且该 JAR 的 SHA-256 与实施前的静态证据一致。本轮没有修改它，也没有据此声称 1.16.5 运行验证通过。

这些检查不等于客户端、双人联机或长时间生存测试。没有做 FPS/MSPT 基准；能够确认的性能变化是慢部件碰撞查询/推挤调用从两次减少为一次。

## 复跑方法

回归源码通过 `-PpatchRegression` 显式启用，默认构建不包含它们。测试会修改隔离测试世界的时间并生成测试实体，完成后自动关闭专服。请只使用下面的专用测试目录。

在项目根目录的 PowerShell 中运行以下代码。默认执行 1.20.1 的 9 项行为检查；改为 `1.18.2` 或 `1.19.2` 时，只执行目标类加载检查。1.16.5 仍受上述开发依赖问题限制。

```powershell
$env:JAVA_HOME = 'D:/Program Files/Microsoft/jdk-21.0.8.9-hotspot'
$mcVersion = '1.20.1'
$regressionDir = Join-Path (Get-Location) "build/patch-regression/$mcVersion"
New-Item -ItemType Directory -Force -Path $regressionDir | Out-Null
Set-Content -LiteralPath (Join-Path $regressionDir 'eula.txt') -Value 'eula=true' -Encoding ASCII
$propertiesFile = Join-Path $regressionDir 'server.properties'
if (!(Test-Path -LiteralPath $propertiesFile)) {
    @'
server-ip=127.0.0.1
server-port=0
online-mode=false
level-name=world
level-type=flat
generate-structures=false
spawn-animals=false
spawn-monsters=false
spawn-npcs=false
max-tick-time=-1
view-distance=2
simulation-distance=2
level-seed=1
'@ | Set-Content -LiteralPath $propertiesFile -Encoding ASCII
}
$resultFile = Join-Path $regressionDir 'patch-regression-result.txt'
Remove-Item -LiteralPath $resultFile -Force -ErrorAction SilentlyContinue
& .\gradlew.bat -PpatchRegression ":${mcVersion}:runServer" -x ":${mcVersion}:downloadAssets" --offline --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Gradle failed; inspect the server log.' }
if (!(Test-Path -LiteralPath $resultFile)) { throw 'Server did not reach the regression checks.' }
$result = Get-Content -LiteralPath $resultFile -Raw
if ($result -notmatch '^PASS:' -or $result -match 'FAIL:') { throw $result }
$result
```

这里显式跳过专服不需要的资源下载。`--offline` 需要本机已缓存依赖，初次配置时可去掉。Forge 启动失败时 Gradle 仍可能显示成功，因此必须检查本次生成的结果文件，不能只看 `BUILD SUCCESSFUL`。

测试后重新生成不带回归源码的正式 JAR：

```powershell
.\gradlew.bat :1.16.5:clean :1.18.2:clean :1.19.2:clean :1.20.1:clean :1.16.5:build :1.18.2:build :1.19.2:build :1.20.1:build --offline --console=plain
```

成品位于 `versions/<Minecraft版本>/build/libs/iaf_patcher-1.3.1-<Minecraft版本>.jar`。不要带 `-PpatchRegression` 打发布包，因为该选项会把测试启动器加入主输出，测试启动器会在专服启动后运行检查并关服。

## 本机证据位置

- 正式构建：`build/simple-patches-production-final.log`。
- 恢复配置后的 1.20.1 回归：`build/simple-patches-regression-1.20.1-restored.log`。
- 通过与基线结果：`build/patch-regression/1.20.1/patched-result.txt`、`baseline-result.txt`；最新结果为同目录 `patch-regression-result.txt`。
- 1.18.2 / 1.19.2 加载结果：`build/patch-regression/<版本>/patch-regression-result.txt`。
- 1.16.5 崩溃报告：`build/patch-regression/1.16.5/crash-reports/crash-2026-11-12_20.30.20-fml.txt`。
- 正式 JAR 的内容、字节码与校验和记录：[构建校验记录](simple-patches-validation-2026-11-12.json)。

`build` 下的日志和世界属于本地生成物，不进入 Git；清理根 `build` 会删除它们。验证结论及正式包记录保留在本报告和校验记录中。
