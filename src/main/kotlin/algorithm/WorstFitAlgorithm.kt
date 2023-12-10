package algorithm

import androidx.compose.runtime.mutableStateListOf
import data.model.MemoryBlock
import kotlin.math.max

/**
 * 最坏适应算法
 */
class WorstFitAlgorithm(
    private val memory: MutableList<MemoryBlock> = mutableStateListOf()
): FirstFitAlgorithm(memory) {
    /**
     * 尝试分配内存
     * 分配与实际申请大小差距最大的内存块
     * @param size 分配大小
     * @return 如果没有合适的内存块就返回 null
     */
    override  fun allocateMemory(size: Int): MemoryBlock? {
        var maxBlock: MemoryBlock? = null
        for (block in memory) {
            // 判断当前块是否没有被占用，并且当前块大小可以容下申请的大小
            if (!block.isOccupied() && (block.size - block.used.value) >= size) {
                // 找到适合的并且大小差距最大的内存块
                if (maxBlock == null || (maxBlock.size - size) < (block.size - size)) {
                    maxBlock = block
                    continue
                }
            }
        }

        if (maxBlock == null) {
            // 没有找到合适内存块
            return null
        } else {
            // 找到了第一个合适的空闲块
            // 更新内存块大小
            maxBlock.used.value += size
            return maxBlock
        }
    }
}