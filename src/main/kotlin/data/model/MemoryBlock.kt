package data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * 内存块
 * @param id 块 ID
 * @param size 块大小
 * @param used 已使用大小
 */
data class MemoryBlock(
    val id: Int,
    val size: Int,
    var used: MutableState<Int> = mutableStateOf(0),
) {
    /**
     * 当前内存块是否已经被占用
     * @return 已经占用就返回 true
     */
    fun isOccupied(): Boolean {
        return used.value != 0
    }

    /**
     * 当前内存块空间是否已满
     * @return 满了返回 true
     */
    fun isFull(): Boolean {
        return used.value >= size
    }
}