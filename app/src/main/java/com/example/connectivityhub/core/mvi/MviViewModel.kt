package com.example.connectivityhub.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Base MVI ViewModel.
 *
 * All feature ViewModels extend this class and must define:
 *  - [State]  — a data class / sealed class representing the full UI state
 *  - [Intent] — a sealed interface of all possible user / system intents
 *
 * Usage:
 * ```kotlin
 * class WifiViewModel : MviViewModel<WifiIntent, WifiState>() {
 *     override val state: StateFlow<WifiState> = ...
 *     override fun onIntent(intent: WifiIntent) { ... }
 * }
 * ```
 */
abstract class MviViewModel<Intent, State> : ViewModel() {

    /** The current UI state, exposed as a hot [StateFlow]. */
    abstract val state: StateFlow<State>

    /**
     * Entry point for all user or system actions.
     * Called from the UI layer (Compose) when the user interacts with the screen
     * or a lifecycle event triggers a new intent.
     */
    abstract fun onIntent(intent: Intent)
}
