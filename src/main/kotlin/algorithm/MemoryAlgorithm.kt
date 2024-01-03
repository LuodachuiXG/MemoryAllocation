package algorithm

import data.model.IndexWithData
import data.model.MemoryBlock

/**
 * 内存分配算法接口
 */
interface MemoryAlgorithm {
    /**
     * 分配内存
     * @param size 分配大小
     * @return 返回内存块以及所在的索引位置
     */
    fun allocateMemory(size: Int): IndexWithData<MemoryBlock>

    /**
     * 删除内存块
     * @param index 要删除的内存块索引
     */
    fun delMemory(index: Int)

    /**
     * 重新设置内存块大小
     */
    fun reSizeMemory(index: Int, newSize: Int)

    /**
     * 获取内存块
     * @param index 索引
     */
    fun memoryAt(index: Int): MemoryBlock?

    /**
     * 返回内存
     * @return 返回内存块（ArrayList）
     */
    fun getMemory(): MutableList<MemoryBlock>

    /**
     * 内存块大小
     */
    fun size(): Int


    /**
     * 紧凑算法，使内存往一侧紧凑
     */
    fun compaction()

    /**
     * 判断当前内存款是否还可以进行紧凑操作
     * @return 如果还可以紧凑，就返回 true
     */
    fun canCompaction(): Boolean

    /**
     * 是否所有内存块都被占用
     * @return 所有内存块被占用返回 true
     */
    fun isAllOccupied(): Boolean
}