package ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    val progressAnimDuration = 200
    // 进度条动画
    val progressAnimation by animateFloatAsState(
        targetValue = if (memoryBlock.isOccupied()) 1f else 0f,
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
            // 块起始地址
            Text(
                text = memoryBlock.addr.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // 块大小
                Text(
                    text = "Size: ${memoryBlock.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                // 块尾地址
                Text(
                    text = (memoryBlock.addr + memoryBlock.size - 1).toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}