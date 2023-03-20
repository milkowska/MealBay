package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import androidx.compose.ui.graphics.painter.Painter

/**
 * The data class for an Icon containing a painter for both filled and outlined icon and a label describing the purpose
 * of the icon group.
 */
data class IconGroup(
    val filledIcon: Painter,
    val outlinedIcon: Painter,
    val label: String
)