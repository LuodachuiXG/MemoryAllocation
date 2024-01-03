package data.model

/**
 * 实体类，包含一个 index 索引和数据
 */
data class IndexWithData<T>(
    val index: Int = -1,
    val data: T? = null
)
