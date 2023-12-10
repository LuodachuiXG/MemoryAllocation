package ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.model.MemoryBlock

/**
 * 内存块卡片，用于将内存块实体类可视化
 * @param memoryBlock 内存块实体类
 * @param onClick 内存块卡片点击事件，将内存块实体类回调
 */
@Composable
fun MemoryBlockCard(
    memoryBlock: MemoryBlock,
    onClick: (MemoryBlock) -> Unit
) {
    // 进度条动画持续时间
    val progressAnimDuration = 400
    // 进度条动画
    val progressAnimation by animateFloatAsState(
        targetValue = memoryBlock.used.value.toFloat() / memoryBlock.size.toFloat(),
        animationSpec = tween(
            durationMillis = progressAnimDuration,
            easing = FastOutSlowInEasing
        )
    )

    // 已使用大小加载动画
    val usedSizeAnimation by animateIntAsState(
        targetValue = memoryBlock.used.value,
        animationSpec = tween(
            durationMillis = progressAnimDuration,
            easing = FastOutSlowInEasing
        )
    )
    Box (
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clip(CardDefaults.shape)
            .clickable {
                onClick(memoryBlock)
            }
    ) {
        LinearProgressIndicator(
            progress = progressAnimation,
            modifier = Modifier.height(28.dp).fillMaxWidth()
        )
        Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = memoryBlock.id.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$usedSizeAnimation / ${memoryBlock.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${(progressAnimation * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}