/*
 * Copyright (C) 2021-2025 The Metroboard Contributors
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

package org.metroboard

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import androidx.core.os.UserManagerCompat
import org.metroboard.app.MetroboardPreferenceModel
import org.metroboard.app.MetroboardPreferenceStore
import org.metroboard.ime.clipboard.ClipboardManager
import org.metroboard.ime.core.SubtypeManager
import org.metroboard.ime.dictionary.DictionaryManager
import org.metroboard.ime.editor.EditorInstance
import org.metroboard.ime.keyboard.KeyboardManager
import org.metroboard.ime.media.emoji.MetroboardEmojiCompat
import org.metroboard.ime.nlp.NlpManager
import org.metroboard.ime.text.gestures.GlideTypingManager
import org.metroboard.ime.theme.ThemeManager
import org.metroboard.lib.cache.CacheManager
import org.metroboard.lib.crashutility.CrashUtility
import org.metroboard.lib.devtools.Flog
import org.metroboard.lib.devtools.LogTopic
import org.metroboard.lib.devtools.flogError
import org.metroboard.lib.ext.ExtensionManager
import dev.patrickgold.jetpref.datastore.runtime.initAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.metroboard.lib.kotlin.io.deleteContentsRecursively
import org.metroboard.lib.kotlin.tryOrNull
import org.metroboard.libnative.dummyAdd
import java.lang.ref.WeakReference

/**
 * Global weak reference for the [MetroboardApplication] class. This is needed as in certain scenarios an application
 * reference is needed, but the Android framework hasn't finished setting up
 */
private var MetroboardApplicationReference = WeakReference<MetroboardApplication?>(null)

@Suppress("unused")
class MetroboardApplication : Application() {
    companion object {
        init {
            try {
                System.loadLibrary("fl_native")
            } catch (_: Exception) {
            }
        }
    }

    private val mainHandler by lazy { Handler(mainLooper) }
    private val scope = CoroutineScope(Dispatchers.Default)
    val preferenceStoreLoaded = MutableStateFlow(false)

    val cacheManager = lazy { CacheManager(this) }
    val clipboardManager = lazy { ClipboardManager(this) }
    val editorInstance = lazy { EditorInstance(this) }
    val extensionManager = lazy { ExtensionManager(this) }
    val glideTypingManager = lazy { GlideTypingManager(this) }
    val keyboardManager = lazy { KeyboardManager(this) }
    val nlpManager = lazy { NlpManager(this) }
    val subtypeManager = lazy { SubtypeManager(this) }
    val themeManager = lazy { ThemeManager(this) }

    override fun onCreate() {
        super.onCreate()
        MetroboardApplicationReference = WeakReference(this)
        try {
            Flog.install(
                context = this,
                isFloggingEnabled = BuildConfig.DEBUG,
                flogTopics = LogTopic.ALL,
                flogLevels = Flog.LEVEL_ALL,
                flogOutputs = Flog.OUTPUT_CONSOLE,
            )
            CrashUtility.install(this)
            MetroboardEmojiCompat.init(this)
            flogError { "dummy result: ${dummyAdd(3,4)}" }

            if (!UserManagerCompat.isUserUnlocked(this)) {
                cacheDir?.deleteContentsRecursively()
                extensionManager.value.init()
                registerReceiver(BootComplete(), IntentFilter(Intent.ACTION_USER_UNLOCKED))
                return
            }

            init()
        } catch (e: Exception) {
            CrashUtility.stageException(e)
            return
        }
    }

    fun init() {
        cacheDir?.deleteContentsRecursively()
        scope.launch {
            val result = MetroboardPreferenceStore.initAndroid(
                context = this@MetroboardApplication,
                datastoreName = MetroboardPreferenceModel.NAME,
            )
            Log.i("PREFS", result.toString())
            preferenceStoreLoaded.value = true
        }
        extensionManager.value.init()
        clipboardManager.value.initializeForContext(this)
        DictionaryManager.init(this)
    }

    private inner class BootComplete : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (intent.action == Intent.ACTION_USER_UNLOCKED) {
                try {
                    unregisterReceiver(this)
                } catch (e: Exception) {
                    flogError { e.toString() }
                }
                mainHandler.post { init() }
            }
        }
    }
}

private tailrec fun Context.metroboardApplication(): MetroboardApplication {
    return when (this) {
        is MetroboardApplication -> this
        is ContextWrapper -> when {
            this.baseContext != null -> this.baseContext.metroboardApplication()
            else -> MetroboardApplicationReference.get()!!
        }
        else -> tryOrNull { this.applicationContext as MetroboardApplication } ?: MetroboardApplicationReference.get()!!
    }
}

fun Context.appContext() = lazyOf(this.metroboardApplication())

fun Context.cacheManager() = this.metroboardApplication().cacheManager

fun Context.clipboardManager() = this.metroboardApplication().clipboardManager

fun Context.editorInstance() = this.metroboardApplication().editorInstance

fun Context.extensionManager() = this.metroboardApplication().extensionManager

fun Context.glideTypingManager() = this.metroboardApplication().glideTypingManager

fun Context.keyboardManager() = this.metroboardApplication().keyboardManager

fun Context.nlpManager() = this.metroboardApplication().nlpManager

fun Context.subtypeManager() = this.metroboardApplication().subtypeManager

fun Context.themeManager() = this.metroboardApplication().themeManager
