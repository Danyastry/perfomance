package com.danyastry.performance.feature.rendering.ui

import androidx.annotation.StringRes
import com.danyastry.performance.feature.rendering.R
import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import com.danyastry.performance.feature.rendering.model.PhaseThread

object PipelineLabels {

    @StringRes
    fun phase(phase: FramePhase): Int = when (phase) {
        FramePhase.UNKNOWN_DELAY -> R.string.rendering_phase_unknown_delay
        FramePhase.INPUT -> R.string.rendering_phase_input
        FramePhase.ANIMATION -> R.string.rendering_phase_animation
        FramePhase.LAYOUT -> R.string.rendering_phase_layout
        FramePhase.DRAW -> R.string.rendering_phase_draw
        FramePhase.SYNC -> R.string.rendering_phase_sync
        FramePhase.COMMAND_ISSUE -> R.string.rendering_phase_command_issue
        FramePhase.SWAP_BUFFERS -> R.string.rendering_phase_swap_buffers
        FramePhase.GPU -> R.string.rendering_phase_gpu
        FramePhase.TOTAL -> R.string.rendering_phase_total
    }

    @StringRes
    fun thread(thread: PhaseThread): Int = when (thread) {
        PhaseThread.UI -> R.string.rendering_thread_ui
        PhaseThread.RENDER -> R.string.rendering_thread_render
        PhaseThread.GPU -> R.string.rendering_thread_gpu
        PhaseThread.WHOLE_FRAME -> R.string.rendering_thread_whole_frame
    }

    @StringRes
    fun load(load: PhaseLoad): Int = when (load) {
        PhaseLoad.NONE -> R.string.rendering_load_none
        PhaseLoad.COMPOSITION -> R.string.rendering_load_composition
        PhaseLoad.LAYOUT -> R.string.rendering_load_layout
        PhaseLoad.DRAW -> R.string.rendering_load_draw
        PhaseLoad.RENDER_THREAD -> R.string.rendering_load_render
    }

    @StringRes
    fun loadExplanation(load: PhaseLoad): Int = when (load) {
        PhaseLoad.NONE -> R.string.rendering_load_none_explanation
        PhaseLoad.COMPOSITION -> R.string.rendering_load_composition_explanation
        PhaseLoad.LAYOUT -> R.string.rendering_load_layout_explanation
        PhaseLoad.DRAW -> R.string.rendering_load_draw_explanation
        PhaseLoad.RENDER_THREAD -> R.string.rendering_load_render_explanation
    }
}
