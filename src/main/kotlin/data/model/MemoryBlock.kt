package data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * 内存块
 * @param addr 块地址
 * @param size 块大小
 */
data class MemoryBlock(
    var addr: Int,
    var size: Int,
    private var occupied: MutableState<Boolean> = mutableStateOf(false)
) {
    /**
     * 当前内存块是否已经被占用
     * @return 已经占用就返回 true
     */
    fun isOccupied(): Boolean {
        return occupied.value
    }

    /**
     * 使当前块为占用状态
     */
    fun occupy() {
        occupied.value = true
    }

    /**
     * 使当前块为未占用状态
     */
    fun unOccupy() {
        occupied.value = false
    }
}