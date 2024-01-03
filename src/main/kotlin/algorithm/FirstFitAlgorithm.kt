package algorithm

import algorithm.CompactionOption.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import data.model.IndexWithData
import data.model.MemoryBlock
import tool.checkDownWith
import tool.checkStartWith


/**
 * 首次适应算法
 * @param memory 内存区（使用 ArrayList 模拟）
 */
open class FirstFitAlgorithm(
    private val memory: SnapshotStateList<MemoryBlock> = mutableStateListOf()
) : MemoryAlgorithm {
    /**
     * 尝试分配内存
     * @param size 分配大小
     * @return 如果没有合适的内存块就返回 null
     */
    override fun allocateMemory(size: Int): IndexWithData<MemoryBlock> {
        memory.forEachIndexed { index, block ->
            // 判断当前块是否没有被占用，并且当前块大小可以容下申请的大小

            if (!block.isOccupied() && block.size >= size) {
                // 找到了第一个合适的空闲块
                return IndexWithData(index, block)
            }
        }
        // 没有找到合适的空闲块
        return IndexWithData()
    }

    /**
     * 删除内存块
     * @param index 要删除的内存块索引
     */
    override fun delMemory(index: Int) {
        memory.removeAt(index)
    }

    /**
     * 获取内存块
     * @param index 索引
     */
    override fun memoryAt(index: Int): MemoryBlock? {
        return if (index < 0 || index > memory.lastIndex) {
            null
        } else {
            memory[index]
        }
    }

    /**
     * 重新设置内存块大小
     */
    override fun reSizeMemory(index: Int, newSize: Int) {
        if (index >= 0 && index <= memory.lastIndex) {
            memory[index].size = newSize
        }
    }

    /**
     * 返回内存
     * @return 返回内存块（ArrayList）
     */
    override fun getMemory(): SnapshotStateList<MemoryBlock> {
        return memory
    }

    /**
     * 获取内存块大小
     */
    override fun size(): Int {
        return memory.size
    }

    /**
     * 紧凑内存块
     * @param compaction 紧凑选项 [CompactionOption]
     */
    override fun compaction(compaction: CompactionOption) {
//        when (compaction) {
//            // 向前紧凑
//            START -> {
//                for (index in 0 until memory.size - 1) {
//                    val memoryBlock = memory[index]
//                    // 当前块剩余空间
//                    val currentBlockRemain = memoryBlock.size.value - memoryBlock.used.value
//
//                    // 下一个块
//                    val nextBlock = memory[index + 1]
//                    if (currentBlockRemain >= nextBlock.used.value) {
//                        // 当前块的剩余空间大于等于下一个块的已用空间
//                        // 直接将下一个块的已用空间转移到当前块
//                        memoryBlock.used.value += nextBlock.used.value
//                        nextBlock.used.value = 0
//                    } else {
//                        // 当前块的剩余空间小于下一个块的已用空间
//                        // 转移下一个块的部分已用空间，填满当前块的所有空间
//                        memoryBlock.used.value += currentBlockRemain
//                        nextBlock.used.value -= currentBlockRemain
//                    }
//                }
//            }
//            // 向后紧凑
//            END -> {
//                // 从后往前遍历
//                for (index in memory.lastIndex downTo 1) {
//                    val memoryBlock = memory[index]
//                    // 当前块剩余空间
//                    val currentBlockRemain = memoryBlock.size.value - memoryBlock.used.value
//
//                    // 上一个块
//                    val preBlock = memory[index - 1]
//                    if (currentBlockRemain >= preBlock.used.value) {
//                        // 当前块的剩余空间大于等于上一个块的已用空间
//                        // 直接将上一个块的已用空间转移到当前块
//                        memoryBlock.used.value += preBlock.used.value
//                        preBlock.used.value = 0
//                    } else {
//                        // 当前块的剩余空间小于上一个块的已用空间
//                        // 转移上一个块的部分已用空间，填满当前块的所有空间
//                        memoryBlock.used.value += currentBlockRemain
//                        preBlock.used.value -= currentBlockRemain
//                    }
//                }
//            }
//        }
    }

    /**
     * 判断当前内存款是否还可以进行紧凑操作
     * @param compaction 紧凑选项，向前 / 向后紧凑 [CompactionOption]
     */
    override fun canCompaction(compaction: CompactionOption): Boolean {
//        when (compaction) {
//            // 向前紧凑
//            START -> {
//                var index = 0
//                var block = memory[index]
//                // 从上往下找到第一个不满的内存块
//                while (block.isFull()) {
//                    index++
//                    block = memory[index]
//                }
//
//                if (index == memory.lastIndex) {
//                    // 第一个不满的内存块是最后一个，无法紧凑
//                    return false
//                }
//
//                // 判断从上往下第一个不为空的内存块下方是否还有非空内存块
//                return if (memory.checkStartWith(index + 1) { !it.isOccupied() }) {
//                    // 下方已经没有不为空的内存块了，无法紧凑
//                    false
//                } else {
//                    // 下方依旧还有不为空的内存块，可以紧凑
//                    true
//                }
//            }
//            // 向后紧凑
//            END -> {
//                var index = memory.lastIndex
//                var block = memory[index]
//                // 从下往上找到第一个不满的内存块
//                while (block.isFull()) {
//                    index--
//                    block = memory[index]
//                }
//
//                if (index == 0) {
//                    // 第一个不满的内存块是第一个，无法紧凑
//                    return false
//                }
//
//                // 判断从下往上第一个不为空的内存块上方是否还有非空内存块
//                return if (memory.checkDownWith(index - 1) { !it.isOccupied() }) {
//                    // 上方已经没有不为空的内存块了，无法紧凑
//                    false
//                } else {
//                    // 上方依旧还有不为空的内存块，可以紧凑
//                    true
//                }
//            }
//        }
        return false
    }

    /**
     * 是否所有内存块都被占用
     * @return 所有内存块被占用返回 true
     */
    override fun isAllOccupied(): Boolean {
        var count = memory.size
        memory.forEach {
            if (it.isOccupied()) {
                count--
            }
        }

        return count == 0
    }

}
