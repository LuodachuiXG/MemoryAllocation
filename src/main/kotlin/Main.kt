import algorithm.*
import algorithm.AlgorithmsEnum.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.sun.tools.javac.Main
import data.model.MemoryBlock
import data.model.MyLog
import javafx.scene.paint.Material
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.checkStartWith
import ui.component.MemoryBlockCard
import ui.theme.darkColors
import ui.theme.md_theme_dark_background
import ui.theme.md_theme_dark_error
import ui.theme.md_theme_dark_primary
import kotlin.random.Random
import kotlin.system.exitProcess

@Composable
@Preview
fun App() {
    Surface {
        AppContent()
    }
}

@Composable
fun AppContent(
    viewModel: MainViewModel = MainViewModel()
) {
    // 当前算法
    var currentAlgo by remember {
        mutableStateOf(FIRST_FIT)
    }
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Column (
            modifier = Modifier.weight(2f)
        ) {
            LeftContent(viewModel) {
                // 算法选择事件
                currentAlgo = it
                // 切换算法后清除原先的数据
                viewModel.clearData()
            }
        }

        Column (
            modifier = Modifier.weight(3f)
        ) {
            RightContent(currentAlgo, viewModel)
        }
    }
}


/**
 * 左侧内容 UI
 * @param viewModel [MainViewModel]
 * @param onAlgorithmChange 算法选项改变事件
 */
@Composable
fun LeftContent(
    viewModel: MainViewModel,
    onAlgorithmChange: (AlgorithmsEnum) -> Unit
) {
    Column {
        // 算法选择卡片
        AlgorithmOptionCard(
            vm = viewModel,
            // 算法选择更改事件
            onAlgorithmChange = {
                onAlgorithmChange(it)
            }
        )

        // 日志卡片
        LogCard(viewModel)
    }

}


/**
 * 算法选择卡片
 * @param vm [MainViewModel]
 * @param onAlgorithmChange 算法选项改变事件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmOptionCard(
    vm: MainViewModel,
    onAlgorithmChange: (AlgorithmsEnum) -> Unit
) {
    // 是否弹出算法选择下拉菜单
    var dropMenuExpanded by remember { mutableStateOf(false) }
    // 所有算法集合
    val algorithms = AlgorithmsEnum.values()
    // 当前算法
    var currentAlgo by remember { mutableStateOf(FIRST_FIT) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(CardDefaults.shape)
    ) {
        Column (
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "算法选择",
                style = MaterialTheme.typography.titleLarge
            )
            ExposedDropdownMenuBox(
                expanded = dropMenuExpanded,
                onExpandedChange = {
                    // 如果当前有进程正在加载就不弹出菜单
                    if (!vm.isLoading.value) {
                        dropMenuExpanded = it
                    }
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                ElevatedButton (
                    enabled = !vm.isLoading.value,
                    onClick = {
                        // 如果当前有进程正在加载就不弹出菜单
                        if (!vm.isLoading.value) {
                            dropMenuExpanded = true
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                ) {
                    Text(currentAlgo.algoName)
                }

                ExposedDropdownMenu(
                    expanded = dropMenuExpanded,
                    onDismissRequest = { dropMenuExpanded = false },
                ) {
                    algorithms.forEach { algorithm ->
                        DropdownMenuItem(
                            text = {
                                Text(algorithm.algoName)
                            },
                            onClick = {
                                currentAlgo = algorithm
                                dropMenuExpanded = false
                                onAlgorithmChange(currentAlgo)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 日志卡片
 * @param vm [MainViewModel]
 */
@Composable
fun LogCard(
    vm: MainViewModel
) {
    val logScrollState = rememberScrollState()
    LaunchedEffect(vm.log.size) {
        // 始终滚动到最新日志
        logScrollState.animateScrollTo(logScrollState.maxValue)
    }



    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "日志",
                style = MaterialTheme.typography.titleLarge
            )

            // 展示日志
            Column (
                modifier = Modifier
                    .padding(top = 10.dp)
                    .verticalScroll(logScrollState)
            ) {
                vm.log.forEach { log ->
                    Row (
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = log.logStr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = log.color
                        )
                    }
                }
            }
        }
    }
}

/**
 * 右侧内容 UI
 * @param currentAlgo 当前算法
 * @param vm [MainViewModel]
 */
@Composable
fun RightContent(
    currentAlgo: AlgorithmsEnum,
    vm: MainViewModel
) {
    Card (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(CardDefaults.shape)
    ) {
        Column (
            modifier = Modifier.padding(20.dp)
        ) {
            AlgorithmContent(vm, currentAlgo)
        }
    }
}


/**
 * 算法内容 UI
 * @param vm [MainViewModel]
 * @param currentAlgo 当前算法
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmContent(
    vm: MainViewModel,
    currentAlgo: AlgorithmsEnum
) {
    // 计算内存碎片率
    var totalSize = 0
    var idleSize = 0
    vm.memoryBlocks.value.getMemory().forEach {
        // 只计算已经占用的内存块
        if (it.isOccupied()) {
            totalSize += it.size
            idleSize += it.size - it.used.value
        }
    }
    // 计算内存总碎片路
    val fragmentPercent = if (totalSize == 0) 0 else ((idleSize.toFloat() / totalSize.toFloat()) * 100).toInt()

    // 碎片化率数字动画
    val fragmentPercentAnimate by animateIntAsState(
        targetValue = fragmentPercent,
        animationSpec = tween()
    )


    // 是否弹出初始化数量选择下拉菜单
    var dropMenuExpanded by remember { mutableStateOf(false) }

    Column {
        Row {
            Text(
                text = currentAlgo.algoName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "总碎片率：$fragmentPercentAnimate%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 10.dp)
            )
        }

        Row (
            modifier = Modifier.padding(top = 20.dp)
        ) {
            // 下拉弹出菜单
            ExposedDropdownMenuBox(
                expanded = dropMenuExpanded,
                onExpandedChange = {
                    // 如果当前有进程正在加载就不弹出菜单
                    if (!vm.isLoading.value) {
                        dropMenuExpanded = it
                    }
                },
            ) {
                OutlinedButton(
                    onClick = {},
                    enabled = !vm.isLoading.value,
                    modifier = Modifier.menuAnchor()
                ) {
                    Text("初始化内存")
                }

                ExposedDropdownMenu(
                    expanded = dropMenuExpanded,
                    onDismissRequest = { dropMenuExpanded = false },
                ) {
                    // 内存块数量下拉菜单
                    val memoryCountOption = listOf(5, 10, 15, 20)
                    memoryCountOption.forEach {
                        DropdownMenuItem(
                            text = {
                                Text("$it 个内存块")
                            },
                            onClick = {
                                // 分配指定数量个大小从 50 到 200 的内存块
                                val memoryBlocks = mutableListOf<MemoryBlock>()
                                for (i in 1..it) {
                                    memoryBlocks.add(MemoryBlock(i, Random.nextInt(50, 200)))
                                }
                                // 初始化首次适应算法内存块
                                when (currentAlgo) {
                                    FIRST_FIT -> vm.initMemory(FirstFitAlgorithm(memoryBlocks))
                                    BEST_FIT -> vm.initMemory(BestFitAlgorithm(memoryBlocks))
                                    WORST_FIT -> vm.initMemory(WorstFitAlgorithm(memoryBlocks))
                                }
                                dropMenuExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    vm.allocateMemory()
                },
                modifier = Modifier.padding(start = 10.dp),
                enabled = !vm.isLoading.value
            ) {
                Text("分配内存")
            }

            OutlinedButton(
                onClick = {
                    vm.compaction(CompactionOption.START)
                },
                modifier = Modifier.padding(start = 10.dp),
                enabled = !vm.isLoading.value
            ) {
                Text("上紧凑")
            }

            OutlinedButton(
                onClick = {
                    vm.compaction(CompactionOption.END)
                },
                modifier = Modifier.padding(start = 10.dp),
                enabled = !vm.isLoading.value
            ) {
                Text("下紧凑")
            }
        }

        // 展示内存块
        if (vm.memoryBlocks.value.getMemory().isNotEmpty()) {
            Column (
                modifier = Modifier
                    .padding(top = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                vm.memoryBlocks.value.getMemory().forEach {
                    MemoryBlockCard(it) { memoryBlock ->
                        // 内存块卡片点击事件
                        vm.deallocateMemory(memoryBlock)
                    }
                }
            }
        }
    }
}


/**
 * 窗口头
 */
@Composable
fun AppBar() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(md_theme_dark_background)
    ) {
        Text(
            text = "内存可变分区分配算法的分配和回收",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            IconButton(
                onClick = {
                    exitProcess(0)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * ViewMode
 */
class MainViewModel {
    // 协程作用域
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // 标记当前是否有协程正在进行操作
    var isLoading = mutableStateOf(false)

    // 算法内存块
    private val _memoryBlocks = mutableStateOf<MemoryAlgorithm>(FirstFitAlgorithm())
    val memoryBlocks: State<MemoryAlgorithm> = _memoryBlocks

    // 日志内容
    val log = mutableStateListOf<MyLog>()
    /**
     * 清除 ViewModel 中变量数据
     */
    fun clearData() {
        _memoryBlocks.value = FirstFitAlgorithm()
        log.clear()
    }

    /**
     * 初始化内存
     * @param memoryAlgorithm 要分配算法内存块
     */
    fun initMemory(memoryAlgorithm: MemoryAlgorithm) {
        var totalMemorySize = 0
        memoryAlgorithm.getMemory().forEach {
            totalMemorySize += it.size
        }
        addLog(MyLog("初始化内存，总大小：$totalMemorySize"))
        _memoryBlocks.value = memoryAlgorithm
    }

    /**
     * 分配内存
     */
    fun allocateMemory() {
        isLoading.value = true
        var totalAllocateSize = 0
        coroutineScope.launch {
            // 如果内存块中还有未被占用的内存块就继续进行循环
            while (!_memoryBlocks.value.isAllOccupied()) {
                val allocateSize = Random.nextInt(1, 200)
                val memoryBlock = _memoryBlocks.value.allocateMemory(allocateSize)
                if (memoryBlock == null) {
                    addLog(MyLog("分配内存失败，空间不足。大小：$allocateSize", md_theme_dark_error))
                } else {
                    // 计算当前分配的内存的碎片化率
                    val fragmentPercent = (((memoryBlock.size - memoryBlock.used.value) / memoryBlock.size.toFloat()) * 100).toInt()
                    addLog(MyLog("分配内存大小：$allocateSize，碎片化率：$fragmentPercent%"))
                    totalAllocateSize += allocateSize
                }
                delay(50)
            }
            addLog(MyLog("内存分配完成，总分配大小：$totalAllocateSize", Color.Green))
            isLoading.value = false
        }
    }

    /**
     * 释放内存块
     * @param memoryBlock 要释放的内存块 [MemoryBlock]
     */
    fun deallocateMemory(memoryBlock: MemoryBlock) {
        addLog(MyLog("释放内存 ID：${memoryBlock.id}"))
        _memoryBlocks.value.deallocateMemory(memoryBlock)
    }

    /**
     * 紧凑算法
     */
    fun compaction(compactionOption: CompactionOption) {
        isLoading.value = true
        coroutineScope.launch {
            var count = 1
            while (_memoryBlocks.value.canCompaction(compactionOption)) {
                addLog(MyLog("第 $count 次" +
                        (if (compactionOption == CompactionOption.START) "上" else "下") + "紧凑"))
                count++
                _memoryBlocks.value.compaction(compactionOption)
                delay(100)
            }
            addLog(MyLog("紧凑完成", Color.Green))
            isLoading.value = false
        }
    }

    /**
     * 添加日志
     * @param myLog 日志实体类
     */
    private fun addLog(myLog: MyLog) {
        log.add(myLog)
    }
}




fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        state = WindowState(
            size = DpSize(900.dp, 650.dp)
        )
    ) {
        MaterialTheme (
            colorScheme = darkColors
        ) {
            Column {
                // 窗口拖动区域
                WindowDraggableArea {
                    AppBar()
                }
                App()
            }
        }
    }
}
