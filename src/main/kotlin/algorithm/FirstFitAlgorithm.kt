package algorithm

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
     */
    override fun compaction() {
        // 已占用的块总大小
        var totalSize = 0
        memory.forEach {
            if (it.isOccupied()) {
                totalSize += it.size
                // 同时取消每个块的占用状态
                it.unOccupy()
            }
        }

        var currentIndex = 0
        var currentBlock = memory[currentIndex]
        while (totalSize - currentBlock.size > 0) {
            totalSize -= currentBlock.size
            currentBlock.occupy()
            currentBlock = memory[++currentIndex]
        }

        if (totalSize > 0) {
            // 当前剩余未分配大小填不满当前块
            // 需要分割当前块
            val newMemoryBlock = MemoryBlock(addr = currentBlock.addr, size = totalSize)
            newMemoryBlock.occupy()
            // 重新修改被分割的内存块的大小和地址
            currentBlock.size -= totalSize
            currentBlock.addr = newMemoryBlock.addr + newMemoryBlock.size

            // 插入分割出来的内存块，插入到被分割的内存块前
            memory.add(currentIndex, newMemoryBlock)
        }
    }

    /**
     * 判断当前内存款是否还可以进行紧凑操作
     */
    override fun canCompaction(): Boolean {
        var index = 0
        var block = memory[index]
        // 从上往下找到第一个未被占用的块
        while (block.isOccupied() && index != memory.lastIndex) {
            index++
            block = memory[index]
        }

        if (index == memory.lastIndex) {
            // 第一个不满的内存块是最后一个，无法紧凑
            return false
        }

        // 判断从上往下第一个未被占用的内存块下方是否还有未被占用的内存块
        return if (memory.checkStartWith(index + 1) { !it.isOccupied() }) {
            // 下方已经没有未被占用的内存块了，无法紧凑
            false
        } else {
            // 下方依旧还有未被占用的内存块，可以紧凑
            true
        }
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
