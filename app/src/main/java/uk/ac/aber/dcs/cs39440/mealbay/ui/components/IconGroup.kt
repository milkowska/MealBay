package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import androidx.compose.ui.graphics.painter.Painter

data class IconGroup(
    val filledIcon: Painter,
    val outlinedIcon: Painter,
    val label: String
)