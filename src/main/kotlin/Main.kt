import algorithm.*
import algorithm.AlgorithmsEnum.*
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import data.model.MemoryBlock
import data.model.MyLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ui.component.MemoryBlockCard
import ui.theme.darkColors
import ui.theme.md_theme_dark_background
import ui.theme.md_theme_dark_error
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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            LeftContent(viewModel) {
                // 算法选择事件
                currentAlgo = it
                // 切换算法后清除原先的数据
                viewModel.clearData()
            }
        }

        Column(
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(CardDefaults.shape)
    ) {
        Column(
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
                ElevatedButton(
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



    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "日志",
                style = MaterialTheme.typography.titleLarge
            )

            // 展示日志
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .verticalScroll(logScrollState)
            ) {
                vm.log.forEach { log ->
                    Row(
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
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(CardDefaults.shape)
    ) {
        Column(
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
    // 是否弹出初始化数量选择下拉菜单
    var dropMenuExpanded by remember { mutableStateOf(false) }

    // 可分配的内存大小选择下拉菜单
    val memorySizeOption = listOf(500, 700, 1000, 1500, 2000)

    Column {
        Row {
            Text(
                text = currentAlgo.algoName,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
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
                    memorySizeOption.forEach { size ->
                        DropdownMenuItem(
                            text = {
                                Text(size.toString())
                            },
                            onClick = {
                                // 分配一个指定大小的内存块
                                val memoryBlocks = mutableStateListOf(MemoryBlock(addr = 0, size = size))

                                // 根据不同算法初始化内存块
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
                Text("装入作业")
            }

            OutlinedButton(
                onClick = {
                    vm.compaction()
                },
                modifier = Modifier.padding(start = 10.dp),
                enabled = !vm.isLoading.value
            ) {
                Text("紧凑")
            }
        }

        // 展示内存块
        if (vm.algorithm.value.getMemory().isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
            ) {
                vm.algorithm.value.getMemory().forEachIndexed { index, memory ->
                    MemoryBlockCard(memory) { memoryBlock ->
                        // 内存块卡片点击事件
                        vm.deallocateMemory(index, memoryBlock)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(md_theme_dark_background)
    ) {
        Text(
            text = "内存可变分区分配算法的分配和回收",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Row(
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

    // 当前算法算法
    private val _algorithm = mutableStateOf<MemoryAlgorithm>(FirstFitAlgorithm())
    val algorithm: State<MemoryAlgorithm> = _algorithm

    // 日志内容
    val log = mutableStateListOf<MyLog>()

    /**
     * 清除 ViewModel 中变量数据
     */
    fun clearData() {
        _algorithm.value = FirstFitAlgorithm()
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
        _algorithm.value = memoryAlgorithm
    }

    /**
     * 动态分配内存
     */
    fun allocateMemory() {
        // 随机一个分配大小
        val allocateSize = Random.nextInt(50, 300)
        // 尝试分配内存
        val allocated = _algorithm.value.allocateMemory(allocateSize)
        // 分配的内存块
        val allocateMemoryBlock = allocated.data
        // 分配的内存块在内存中的索引
        val allocateIndex = allocated.index
        if (allocateMemoryBlock == null) {
            addLog(MyLog("分配内存失败，空间不足。大小：$allocateSize", Color.Red))
        } else {
            // 成功分配到内存
            // 从分配的内存块中去除当前大小作为一个单独的内存分区
            val memoryBlocks = _algorithm.value.getMemory()
            // 从旧的内存分区中分出新的内存块，因为从前插入，所以这里内存块地址直接设为分配的内存的地址
            val newMemoryBlock = MemoryBlock(addr = allocateMemoryBlock.addr, size = allocateSize)
            newMemoryBlock.occupy()
            // 重新修改被分割的内存块的大小和地址
            allocateMemoryBlock.size -= allocateSize
            allocateMemoryBlock.addr = newMemoryBlock.addr + newMemoryBlock.size

            // 插入分割出来的内存块，插入到被分割的内存块前
            memoryBlocks.add(allocateIndex, newMemoryBlock)

            // 计算当前分配的内存的碎片化率
            addLog(MyLog("装入作业：($allocateSize) -> 块地址 ${newMemoryBlock.addr}"))
        }
    }

    /**
     * 释放内存块，有下面四种情况：
     * 1.回收区与插入点的前一个分区相邻接，两分区合并。
     * 2.回收区与插入点的后一个分区相邻接 ，两分区合并。
     * 3.回收区同时与插入点的前、后两个分区邻接 ，三分区合并。
     * 4.回收区与插入点前、后两个分区都不相邻 ，单独一个分区。
     * 简单理解：有相邻则合并，无相邻则单独为一个分区
     * @param memoryBlock 要释放的内存块 [MemoryBlock]
     * @param index 要释放的内存块的索引
     */
    fun deallocateMemory(index: Int, memoryBlock: MemoryBlock) {
        // 先释放当前内存块
        memoryBlock.unOccupy()
        when (index) {
            0 -> {
                // 当前块是第一个内存块，合并下一个内存块
                mergeBlock(index, 1)
            }
            _algorithm.value.getMemory().lastIndex -> {
                // 当前块是最后一个块，合并上一个内存块
                mergeBlock(index - 1, index)
            }
            else -> {
                // 当前块是中间块，合并上下块
                // 合并上
                if (mergeBlock(index - 1, index)) {
                    // 上合并成功
                    // 合并下
                    mergeBlock(index - 1, index)
                } else {
                    // 上合并失败
                    // 合并下
                    mergeBlock(index, index + 1)
                }
            }
        }
    }

    /**
     * 合并内存块
     * @param currentIndex 当前块索引
     * @param mergeIndex 要合并的快索引
     * @return 是否合并成功
     */
    private fun mergeBlock(currentIndex: Int, mergeIndex: Int): Boolean {
        val currentBlock = _algorithm.value.memoryAt(currentIndex)
        val mergeBlock = _algorithm.value.memoryAt(mergeIndex)

        if (currentBlock == null || mergeBlock == null) {
            return false
        }

        // 如果两个块有一个被占用，取消合并
        if (currentBlock.isOccupied() || mergeBlock.isOccupied()) {
            return false
        }

        if (!mergeBlock.isOccupied()) {
            // 要合并的块如果没有被占用，就合并当前块
            currentBlock.size += mergeBlock.size

            // 删除被合并的块
            _algorithm.value.delMemory(mergeIndex)
            return true
        }

        return false
    }

    /**
     * 紧凑算法
     */
    fun compaction() {
        isLoading.value = true
        // 是否还可以紧凑
        if (_algorithm.value.canCompaction()) {
            _algorithm.value.compaction()
        }
        addLog(MyLog("紧凑完成", Color.Green))
        isLoading.value = false
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
        MaterialTheme(
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
