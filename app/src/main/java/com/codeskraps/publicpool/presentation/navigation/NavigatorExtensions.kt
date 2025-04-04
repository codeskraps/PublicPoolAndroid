package com.codeskraps.publicpool.presentation.navigation

import cafe.adriel.voyager.navigator.Navigator

/**
 * Get the parent or root navigator from the current navigator
 * This allows us to navigate to screens at a higher level,
 * escaping tab-based navigation
 */
fun Navigator.getParentOrSelf(): Navigator {
    return this.parent ?: this
} 