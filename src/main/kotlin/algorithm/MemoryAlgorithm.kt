package algorithm

import data.model.MemoryBlock

/**
 * 内存分配算法接口
 */
interface MemoryAlgorithm {
    /**
     * 分配内存
     * @param size 分配大小
     * @return 如果没有合适的内存块就返回 null
     */
    fun allocateMemory(size: Int): MemoryBlock?

    /**
     * 释放内存块
     * @param block 要释放的内存块
     */
    fun deallocateMemory(block: MemoryBlock)

    /**
     * 返回内存
     * @return 返回内存块（ArrayList）
     */
    fun getMemory(): MutableList<MemoryBlock>


    /**
     * 紧凑算法，使内存往一侧紧凑
     * @param compaction 紧凑选项，向前 / 向后紧凑 [CompactionOption]
     */
    fun compaction(compaction: CompactionOption)

    /**
     * 判断当前内存款是否还可以进行紧凑操作
     * @param compaction 紧凑选项，向前 / 向后紧凑 [CompactionOption]
     * @return 如果还可以紧凑，就返回 true
     */
    fun canCompaction(compaction: CompactionOption): Boolean

    /**
     * 是否所有内存块都被占用
     * @return 所有内存块被占用返回 true
     */
    fun isAllOccupied(): Boolean
}

/**
 * 紧凑选项
 */
enum class CompactionOption {
    // 前紧凑
    START,
    // 后紧凑
    END
}