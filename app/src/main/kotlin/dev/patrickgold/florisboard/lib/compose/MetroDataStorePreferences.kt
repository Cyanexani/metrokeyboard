/*
 * Copyright (C) 2021-2025 The FlorisBoard Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package dev.patrickgold.florisboard.lib.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ListPreferenceEntry
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefEnabled
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefVisible
import kotlinx.coroutines.launch
import org.florisboard.lib.compose.MetroCheckboxPreferenceItem
import org.florisboard.lib.compose.MetroOptionPicker
import org.florisboard.lib.compose.MetroNavigationPreferenceItem
import org.florisboard.lib.compose.MetroSwitchPreferenceItem

/**
 * Datastore-backed WP8.1 list preference.
 *
 * Small choice sets stay visible inline; larger sets use the outlined Metro
 * picker. This replaces the Material dialog list used by JetPref while keeping
 * its preference conditions and storage behavior.
 */
@Composable
fun <V : Any> MetroListPreference(
    listPref: PreferenceData<V>,
    modifier: Modifier = Modifier,
    switchPref: PreferenceData<Boolean>? = null,
    @Suppress("UNUSED_PARAMETER") icon: ImageVector? = null,
    title: String,
    summarySwitchDisabled: String = "",
    entries: List<ListPreferenceEntry<V>>,
    isMajorFeatureGate: Boolean = true,
    enabledIf: @Composable PreferenceDataEvaluatorScope.() -> Boolean = { true },
    visibleIf: @Composable PreferenceDataEvaluatorScope.() -> Boolean = { true },
) {
    val evaluator = PreferenceDataEvaluatorScope
    val isVisible = LocalIsPrefVisible.current && visibleIf(evaluator)
    if (!isVisible) return

    val scope = rememberCoroutineScope()
    val value by listPref.collectAsState()
    val switchState = switchPref?.collectAsState()
    val switchValue = switchState?.value ?: true
    val isEnabled = LocalIsPrefEnabled.current && enabledIf(evaluator)
    val pickerEntries = entries.map { it.key to it.label }
    val currentLabel = entries.firstOrNull { it.key == value }?.label

    if (switchPref != null) {
        if (isMajorFeatureGate) {
            MetroSwitchPreferenceItem(
                title = title,
                summary = if (switchValue) currentLabel else summarySwitchDisabled.ifBlank { null },
                checked = switchValue,
                onCheckedChange = { checked -> scope.launch { switchPref.set(checked) } },
                modifier = modifier,
                enabled = isEnabled,
            )
        } else {
            MetroCheckboxPreferenceItem(
                title = title,
                summary = if (switchValue) currentLabel else summarySwitchDisabled.ifBlank { null },
                checked = switchValue,
                onCheckedChange = { checked -> scope.launch { switchPref.set(checked) } },
                modifier = modifier,
                enabled = isEnabled,
            )
        }
        if (switchValue) {
            MetroOptionPicker(
                title = "",
                selectedValue = value,
                entries = pickerEntries,
                onValueSelected = { selected -> scope.launch { listPref.set(selected) } },
                enabled = isEnabled,
            )
        }
    } else {
        MetroOptionPicker(
            title = title,
            selectedValue = value,
            entries = pickerEntries,
            onValueSelected = { selected -> scope.launch { listPref.set(selected) } },
            modifier = modifier,
            enabled = isEnabled,
        )
    }
}

/** Datastore-aware wrapper for an icon-free Windows Phone settings row. */
@Composable
fun MetroNavigationPreference(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabledIf: @Composable PreferenceDataEvaluatorScope.() -> Boolean = { true },
    visibleIf: @Composable PreferenceDataEvaluatorScope.() -> Boolean = { true },
) {
    val evaluator = PreferenceDataEvaluatorScope
    if (!LocalIsPrefVisible.current || !visibleIf(evaluator)) return

    MetroNavigationPreferenceItem(
        title = title,
        summary = summary,
        onClick = onClick,
        modifier = modifier,
        enabled = LocalIsPrefEnabled.current && enabledIf(evaluator),
    )
}
