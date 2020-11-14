package ui.settings.reducer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import ui.settings.SettingsEffect
import ui.settings.SettingsState
import ui.settings.renderSampleCode
import ui.settings.renderSampleFileName

interface SelectScreenElementReducer {
    operator fun invoke(index: Int)
}

class SelectScreenElementReducerImpl(
    state: MutableStateFlow<SettingsState>,
    effect: MutableSharedFlow<SettingsEffect>,
    scope: CoroutineScope
) : BaseReducer(state, effect, scope), SelectScreenElementReducer {

    override fun invoke(index: Int) = pushState {
        val selectedIndex =
            if (screenElements.isNotEmpty() && index in screenElements.indices) {
                index
            } else {
                null
            }
        val selectedElement = selectedIndex?.let { screenElements[selectedIndex] }
        val fileName = selectedElement?.renderSampleFileName(activityBaseClass) ?: ""
        val sampleCode = selectedElement?.renderSampleCode(activityBaseClass) ?: ""
        copy(
            selectedElementIndex = selectedIndex,
            fileNameRendered = fileName,
            sampleCode = sampleCode
        )
    }
}
