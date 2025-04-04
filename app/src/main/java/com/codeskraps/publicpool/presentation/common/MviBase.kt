package com.codeskraps.publicpool.presentation.common

/**
 * Represents the state of a UI screen.
 * It should be an immutable data class.
 */
interface UiState

/**
 * Represents user actions or events triggered from the UI.
 */
interface UiEvent

/**
 * Represents side effects that the ViewModel needs to trigger,
 * such as navigation, showing toasts/snackbars, etc.
 */
interface UiEffect 