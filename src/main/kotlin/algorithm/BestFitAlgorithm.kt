package algorithm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import data.model.IndexWithData
import data.model.MemoryBlock
import java.util.LinkedList

/**
 * 最佳适应算法
 */
class BestFitAlgorithm(
    private val memory: SnapshotStateList<MemoryBlock> = mutableStateListOf()
) : FirstFitAlgorithm(memory) {
    /**
     * 分配内存
     * @param size 分配大小
     * @return 返回内存块以及所在的索引位置
     */
    override fun allocateMemory(size: Int): IndexWithData<MemoryBlock> {
        var minBlock: MemoryBlock? = null
        var minIndex: Int = -1

        memory.forEachIndexed { index, block ->
            // 判断当前块是否没有被占用，并且当前块大小可以容下申请的大小
            if (!block.isOccupied() && block.size >= size) {
                // 找到适合的并且大小最小的内存块
                if (minBlock == null || minBlock!!.size > block.size) {
                    minBlock = block
                    minIndex = index
                }
            }
        }

        return if (minBlock == null) {
            // 没有找到合适内存块
            IndexWithData()
        } else {
            // 找到了第一个合适的空闲块
            IndexWithData(minIndex, minBlock)
        }
    }
}
