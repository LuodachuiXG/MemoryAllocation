package tool

/**
 * 对 List 数组中给定索引之后的 Item 进行条件判定
 * @param startIndex 开始索引
 * @param condition 判断条件
 * @return 如果给定索引后所有 Item 都满足要求就返回 true，反之有一个不满足就返回 false
 */
fun <T> List<T>.checkStartWith(
    startIndex: Int,
    condition: (T) -> Boolean
): Boolean {
    if (startIndex > lastIndex || startIndex < 0) {
        // 索引异常
        return false
    }
    // 从给定索引开始遍历
    for (i in startIndex .. lastIndex) {
        // 如果有一个不满足条件就直接返回 false
        if (!condition(this[i])) {
            return false
        }
    }
    // 检索的数据都满足条件，返回 true
    return true
}

/**
 * 对 List 数组中给定索引之前的 Item 进行条件判定
 * @param downIndex 开始索引
 * @param condition 判断条件
 * @return 如果给定索引之前所有 Item 都满足要求就返回 true，反之有一个不满足就返回 false
 */
fun <T> List<T>.checkDownWith(
    downIndex: Int,
    condition: (T) -> Boolean
): Boolean {
    if (downIndex > lastIndex || downIndex < 0) {
        // 索引异常
        return false
    }
    // 从给定索引往前遍历开始遍历
    for (i in downIndex downTo 0) {
        // 如果有一个不满足条件就直接返回 false
        if (!condition(this[i])) {
            return false
        }
    }
    // 检索的数据都满足条件，返回 true
    return true
}