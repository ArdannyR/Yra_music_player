package com.example.yra.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

object YraMotion {
    const val DURATION_MEDIUM = 300
    const val DURATION_SHORT = 200

    val enterTransition = fadeIn(animationSpec = tween(DURATION_MEDIUM)) + 
        slideInHorizontally(
            initialOffsetX = { it / 8 },
            animationSpec = tween(DURATION_MEDIUM)
        )
        
    val exitTransition = fadeOut(animationSpec = tween(DURATION_MEDIUM)) + 
        slideOutHorizontally(
            targetOffsetX = { -it / 8 },
            animationSpec = tween(DURATION_MEDIUM)
        )
        
    val popEnterTransition = fadeIn(animationSpec = tween(DURATION_MEDIUM)) + 
        slideInHorizontally(
            initialOffsetX = { -it / 8 },
            animationSpec = tween(DURATION_MEDIUM)
        )
        
    val popExitTransition = fadeOut(animationSpec = tween(DURATION_MEDIUM)) + 
        slideOutHorizontally(
            targetOffsetX = { it / 8 },
            animationSpec = tween(DURATION_MEDIUM)
        )
        
    val modalEnterTransition = fadeIn(animationSpec = tween(DURATION_MEDIUM)) + 
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(DURATION_MEDIUM)
        )
        
    val modalExitTransition = fadeOut(animationSpec = tween(DURATION_MEDIUM)) + 
        slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(DURATION_MEDIUM)
        )
}
