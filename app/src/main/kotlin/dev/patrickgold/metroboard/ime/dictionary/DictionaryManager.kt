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

package dev.patrickgold.metroboard.ime.dictionary

import android.content.Context
import androidx.room.Room
import dev.patrickgold.metroboard.app.MetroboardPreferenceStore
import dev.patrickgold.metroboard.ime.nlp.SuggestionCandidate
import dev.patrickgold.metroboard.ime.nlp.WordSuggestionCandidate
import dev.patrickgold.metroboard.lib.MetroboardLocale
import java.lang.ref.WeakReference

/**
 * TODO: document
 */
class DictionaryManager private constructor(context: Context) {
    private val applicationContext: WeakReference<Context> = WeakReference(context.applicationContext ?: context)
    private val prefs by MetroboardPreferenceStore

    private var metroboardUserDictionaryDatabase: MetroboardUserDictionaryDatabase? = null
    private var systemUserDictionaryDatabase: SystemUserDictionaryDatabase? = null

    companion object {
        private var defaultInstance: DictionaryManager? = null

        fun init(applicationContext: Context): DictionaryManager {
            val instance = DictionaryManager(applicationContext)
            defaultInstance = instance
            return instance
        }

        fun default(): DictionaryManager {
            val instance = defaultInstance
            if (instance != null) {
                return instance
            } else {
                throw UninitializedPropertyAccessException(
                    "${DictionaryManager::class.simpleName} has not been initialized previously. Make sure to call init(applicationContext) before using default()."
                )
            }
        }
    }

    fun queryUserDictionary(word: String, locale: MetroboardLocale): List<SuggestionCandidate> {
        val metroboardDao = metroboardUserDictionaryDao()
        val systemDao = systemUserDictionaryDao()
        if (metroboardDao == null && systemDao == null) {
            return emptyList()
        }
        return buildList {
            if (prefs.dictionary.enableMetroboardUserDictionary.get()) {
                metroboardDao?.query(word, locale)?.let {
                    for (entry in it) {
                        add(WordSuggestionCandidate(entry.word, confidence = entry.freq / 255.0))
                    }
                }
                metroboardDao?.queryShortcut(word, locale)?.let {
                    for (entry in it) {
                        add(WordSuggestionCandidate(entry.word, confidence = entry.freq / 255.0))
                    }
                }
            }
            if (prefs.dictionary.enableSystemUserDictionary.get()) {
                systemDao?.query(word, locale)?.let {
                    for (entry in it) {
                        add(WordSuggestionCandidate(entry.word, confidence = entry.freq / 255.0))
                    }
                }
                systemDao?.queryShortcut(word, locale)?.let {
                    for (entry in it) {
                        add(WordSuggestionCandidate(entry.word, confidence = entry.freq / 255.0))
                    }
                }
            }
        }

    }

    fun spell(word: String, locale: MetroboardLocale): Boolean {
        val metroboardDao = metroboardUserDictionaryDao()
        val systemDao = systemUserDictionaryDao()
        if (metroboardDao == null && systemDao == null) {
            return false
        }
        var ret = false
        if (prefs.dictionary.enableMetroboardUserDictionary.get()) {
            ret = ret || metroboardDao?.queryExactFuzzyLocale(word, locale)?.isNotEmpty() ?: false
            ret = ret || metroboardDao?.queryShortcut(word, locale)?.isNotEmpty() ?: false
        }
        if (prefs.dictionary.enableSystemUserDictionary.get()) {
            ret = ret || systemDao?.queryExactFuzzyLocale(word, locale)?.isNotEmpty() ?: false
            ret = ret || systemDao?.queryShortcut(word, locale)?.isNotEmpty() ?: false
        }
        return ret
    }

    @Synchronized
    fun metroboardUserDictionaryDao(): UserDictionaryDao? {
        return if (prefs.dictionary.enableMetroboardUserDictionary.get()) {
            metroboardUserDictionaryDatabase?.userDictionaryDao()
        } else {
            null
        }
    }

    @Synchronized
    fun metroboardUserDictionaryDatabase(): MetroboardUserDictionaryDatabase? {
        return if (prefs.dictionary.enableMetroboardUserDictionary.get()) {
            metroboardUserDictionaryDatabase
        } else {
            null
        }
    }

    @Synchronized
    fun systemUserDictionaryDao(): UserDictionaryDao? {
        return if (prefs.dictionary.enableSystemUserDictionary.get()) {
            systemUserDictionaryDatabase?.userDictionaryDao()
        } else {
            null
        }
    }

    @Synchronized
    fun systemUserDictionaryDatabase(): SystemUserDictionaryDatabase? {
        return if (prefs.dictionary.enableSystemUserDictionary.get()) {
            systemUserDictionaryDatabase
        } else {
            null
        }
    }

    @Synchronized
    fun loadUserDictionariesIfNecessary() {
        val context = applicationContext.get() ?: return

        if (metroboardUserDictionaryDatabase == null && prefs.dictionary.enableMetroboardUserDictionary.get()) {
            metroboardUserDictionaryDatabase = Room.databaseBuilder(
                context,
                MetroboardUserDictionaryDatabase::class.java,
                MetroboardUserDictionaryDatabase.DB_FILE_NAME
            ).allowMainThreadQueries().build()
        }
        if (systemUserDictionaryDatabase == null && prefs.dictionary.enableSystemUserDictionary.get()) {
            systemUserDictionaryDatabase = SystemUserDictionaryDatabase(context)
        }
    }

    @Synchronized
    fun unloadUserDictionariesIfNecessary() {
        if (metroboardUserDictionaryDatabase != null) {
            metroboardUserDictionaryDatabase?.close()
            metroboardUserDictionaryDatabase = null
        }
        if (systemUserDictionaryDatabase != null) {
            systemUserDictionaryDatabase = null
        }
    }
}
