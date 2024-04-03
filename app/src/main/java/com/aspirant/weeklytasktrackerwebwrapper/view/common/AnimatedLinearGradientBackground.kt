package com.aspirant.weeklytasktrackerwebwrapper.view.common

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedLinearGradientBackground(
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 7000, // 15 seconds
    colors: List<Color> = listOf(Color.White),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rememberInfiniteColorScrollAnimation")
    val color by infiniteTransition.animateColor(
        initialValue = colors.first(),
        targetValue = colors.last(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "infiniteColorScrollAnimation"
    )

    Box(
        modifier = modifier.background(color)
    ) {
        content()
    }
}
