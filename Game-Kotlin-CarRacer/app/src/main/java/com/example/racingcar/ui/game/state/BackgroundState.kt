package com.example.racingcar.ui.game.state

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

data class BackgroundState(
    val image: ImageBitmap,
    val onGameScoreIncrease: () -> Unit,
    val initialPos: Int = 0,
) {
    private var currentPosY = initialPos

    fun move(velocity: Int) {
        currentPosY += velocity
    }

    fun draw(drawScope: DrawScope) {
        drawScope.apply {
            if (currentPosY > size.height) {
                currentPosY = 0
                onGameScoreIncrease()
            }
            drawImage(
                image = image,
                srcOffset = IntOffset(0, 0),
                dstOffset = IntOffset(0, currentPosY),
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )
            drawImage(
                image = image,
                srcOffset = IntOffset(0, 0),
                dstOffset = IntOffset(0, -size.height.toInt() + currentPosY),
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )
        }
    }
}
