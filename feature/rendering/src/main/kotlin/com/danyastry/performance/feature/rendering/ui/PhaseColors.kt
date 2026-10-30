package com.danyastry.performance.feature.rendering.ui

import androidx.compose.ui.graphics.Color
import com.danyastry.performance.feature.rendering.model.FramePhase

object PhaseColors {

    fun of(phase: FramePhase): Color = when (phase) {
        FramePhase.UNKNOWN_DELAY -> Color(0xFF9E9E9E)
        FramePhase.INPUT -> Color(0xFF8D6E63)
        FramePhase.ANIMATION -> Color(0xFF7E57C2)
        FramePhase.LAYOUT -> Color(0xFF29B6F6)
        FramePhase.DRAW -> Color(0xFF66BB6A)
        FramePhase.SYNC -> Color(0xFFFFCA28)
        FramePhase.COMMAND_ISSUE -> Color(0xFFFF7043)
        FramePhase.SWAP_BUFFERS -> Color(0xFFEC407A)
        FramePhase.GPU -> Color(0xFF26A69A)
        FramePhase.TOTAL -> Color(0xFF424242)
    }
}
