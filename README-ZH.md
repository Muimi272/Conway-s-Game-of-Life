# Conway's Game of Life

本项目是基于 Java 实现的经典控制台版**康威生命游戏**（Conway's Game of Life）。程序支持自定义尺寸与活细胞密度随机初始化，内建 Glider（滑翔机）与 Blinker（闪烁灯）等经典初始构型。世界空间采用**环面（Torus）边界**设计，默认每 500ms 演化一代，支持交互式退出。

旨在为 Java 初学者提供一个结构清晰、低依赖且充满趣味的面向对象与控制台交互实践范例。

---

## 项目架构

项目遵循清晰的职责分离原则（MVC 模式）：

| 类路径 | 职责说明 |
| --- | --- |
| `club.muimi.Main` | 程序唯一入口，负责生命周期调度与退出钩子绑定 |
| `club.muimi.controller.Game` | 核心控制器，处理用户交互输入、初始化流转与演化调度循环 |
| `club.muimi.model.Universe` | 领域模型，维护网格数据、邻居统计与核心生命演化规则（B3/S23） |
| `club.muimi.model.Initializer` | 状态初始化工厂，提供预设图样加载与概率随机分布生成 |
| `club.muimi.view.Console` | 控制台视图层，负责 ANSI 终端重绘、双缓冲输出与终端状态恢复 |

---

## 核心实现特性

1. **双缓冲无污染演算**：每一代演化均基于当前状态的只读快照生成下一代独立数组，演算完成后统一原子替换，杜绝原地更新（In-place update）对邻居状态判断的脏数据污染。
2. **环面拓扑边界（Torus World）**：坐标采用取模映射处理边界环绕，消除边界死角效应，使细胞能够平滑跨越网格边缘。
3. **终端状态防御机制**：通过 ANSI 控制序列实现就地刷新与光标隐藏；在程序退出时统一恢复光标可见性与原终端屏幕状态，避免污染用户终端环境。
4. **健全的输入容错**：对网格宽高范围及密度概率施加严格的边界校验，保证程序健壮性。

---

## 编译与运行

### 环境要求

* JDK 17 或更高版本

### 编译与启动

#### Windows (PowerShell)

```powershell
# 编译所有源代码至 bin 目录
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# 启动程序
java -cp bin club.muimi.Main

```

#### Linux / macOS (Bash)

```bash
# 编译所有源代码至 bin 目录
javac -encoding UTF-8 -d bin $(find src -name "*.java")

# 启动程序
java -cp bin club.muimi.Main

```

---

## 运行配置与参数说明

* **尺寸限制**：网格宽度与高度最小均为 `20`，总单元格数量上限为 `1,000,000`。
* **参数输入示例**：选择“密度初始化”模式后，依次输入**模式编号**、**宽度**、**高度**及 **0.0 ~ 1.0 之间的存活密度**。
```text
2 30 20 0.3

```


*(含义：模式 2，宽度 30，高度 20，初始活细胞占比 30%)*
* **退出方式**：在演化运行过程中输入 `q` 并回车即可优雅终止程序并恢复终端环境。
