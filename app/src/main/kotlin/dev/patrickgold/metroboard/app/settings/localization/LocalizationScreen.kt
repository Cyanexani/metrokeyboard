/*
 * Copyright (C) 2021-2025 The MetroboardBoard Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.metroboard.app.settings.localization

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.core.DisplayLanguageNamesIn
import dev.patrickgold.metroboard.ime.core.Subtype
import dev.patrickgold.metroboard.ime.keyboard.LayoutType
import dev.patrickgold.metroboard.keyboardManager
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.metroboard.subtypeManager
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.metroboard.lib.compose.MetroboardWarningCard
import org.metroboard.lib.compose.MetroButtonItem
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroNavigationPreferenceItem
import org.metroboard.lib.compose.stringRes
import org.metroboard.lib.compose.metroDialogEnterAnimation

internal val SubtypeSaver = Saver<MutableState<Subtype?>, String>(
    save = {
        Json.encodeToString<Subtype?>(it.value)
    },
    restore = {
        mutableStateOf(Json.decodeFromString(it))
    },
)

@Composable
fun LocalizationScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__localization__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    val navController = LocalNavController.current
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()
    val subtypeManager by context.subtypeManager()
    var chosenSubtypeToDelete: Subtype? by rememberSaveable(saver = SubtypeSaver) { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    floatingActionButton {
        MetroButtonItem(
            text = "+ " + stringRes(R.string.settings__localization__subtype_add_title),
            onClick = { navController.navigate(Routes.Settings.SubtypeAdd) },
            modifier = Modifier.padding(16.dp),
        )
    }

    content {
        MetroListPreference(
            prefs.localization.displayLanguageNamesIn,
            title = stringRes(R.string.settings__localization__display_language_names_in__label),
            entries = enumDisplayEntriesOf(DisplayLanguageNamesIn::class),
        )
        val displayKeyboardLabelsInSubtypeLanguage by prefs.localization.displayKeyboardLabelsInSubtypeLanguage.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.settings__localization__display_keyboard_labels_in_subtype_language),
            checked = displayKeyboardLabelsInSubtypeLanguage,
            onCheckedChange = { scope.launch { prefs.localization.displayKeyboardLabelsInSubtypeLanguage.set(it) } },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__localization__language_pack_title),
            summary = stringRes(R.string.settings__localization__language_pack_summary),
            onClick = {
                navController.navigate(Routes.Settings.LanguagePackManager(LanguagePackManagerScreenAction.MANAGE))
            },
        )
        MetroHeader(title = stringRes(R.string.settings__localization__group_subtypes__label))
        val subtypes by subtypeManager.subtypesFlow.collectAsState()
        if (subtypes.isEmpty()) {
            MetroboardWarningCard(
                modifier = Modifier.padding(all = 8.dp),
                text = stringRes(R.string.settings__localization__subtype_no_subtypes_configured_warning),
            )
        } else {
            val currencySets by keyboardManager.resources.currencySets.collectAsState()
            val layouts by keyboardManager.resources.layouts.collectAsState()
            val displayLanguageNamesIn by prefs.localization.displayLanguageNamesIn.collectAsState()
            for (subtype in subtypes) {
                val cMeta = layouts[LayoutType.CHARACTERS]?.get(subtype.layoutMap.characters)
                val sMeta = layouts[LayoutType.SYMBOLS]?.get(subtype.layoutMap.symbols)
                val currMeta = currencySets[subtype.currencySet]
                val summary = stringRes(
                    id = R.string.settings__localization__subtype_summary,
                    "characters_name" to (cMeta?.label ?: "null"),
                    "symbols_name" to (sMeta?.label ?: "null"),
                    "currency_set_name" to (currMeta?.label ?: "null"),
                )
                MetroNavigationPreferenceItem(
                    title = when (displayLanguageNamesIn) {
                        DisplayLanguageNamesIn.SYSTEM_LOCALE -> subtype.primaryLocale.displayName()
                        DisplayLanguageNamesIn.NATIVE_LOCALE -> subtype.primaryLocale.displayName(subtype.primaryLocale)
                    },
                    summary = summary,
                    onClick = {
                        navController.navigate(Routes.Settings.SubtypeEdit(subtype.id))
                    },
                    onLongClick = { chosenSubtypeToDelete = subtype },
                )
            }
        }
    }

    DeleteSubtypeConfirmationDialog(
        subtypeToDelete = chosenSubtypeToDelete,
        onDismiss = {
            chosenSubtypeToDelete = null
        },
        onConfirm = {
            chosenSubtypeToDelete?.let { subtypeManager.removeSubtype(subtypeToRemove = it) }
            chosenSubtypeToDelete = null
        }
    )
}

@Composable
fun DeleteSubtypeConfirmationDialog(
    subtypeToDelete: Subtype?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
)   {
    subtypeToDelete?.let {
        JetPrefAlertDialog(
            modifier = Modifier.metroDialogEnterAnimation(),
            title = stringRes(R.string.settings__localization__subtype_delete_confirmation_title),
            confirmLabel = stringRes(R.string.action__yes),
            dismissLabel = stringRes(R.string.action__no),
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            ) {
                Text(stringRes(R.string.settings__localization__subtype_delete_confirmation_warning))
            }
    }
}
