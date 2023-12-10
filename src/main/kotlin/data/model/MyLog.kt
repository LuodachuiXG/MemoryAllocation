package data.model

import androidx.compose.ui.graphics.Color
import ui.theme.md_theme_dark_primary

/**
 * 日志实体类
 * @param logStr 日志内容
 * @param color 日志颜色
 */
data class MyLog(
    val logStr: String,
    val color: Color = md_theme_dark_primary
)