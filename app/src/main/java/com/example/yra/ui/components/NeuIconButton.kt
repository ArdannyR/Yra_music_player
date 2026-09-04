package com.example.yra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yra.ui.theme.YraTheme
import com.example.yra.ui.theme.neumorphic

@Composable
fun NeuIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    forcePressed: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressedState by interactionSource.collectIsPressedAsState()
    val isPressed = forcePressed || isPressedState

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .neumorphic(
                shape = shape,
                elevation = elevation,
                isPressed = isPressed
            )
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple
                onClick = onClick
            )
            .padding(8.dp) // Standard icon padding
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides iconTint
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NeuIconButtonLightPreview() {
    YraTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            NeuIconButton(
                onClick = {},
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NeuIconButtonDarkPreview() {
    YraTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            NeuIconButton(
                onClick = {},
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
        }
    }
}
