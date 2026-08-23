package org.isglaz.battlereport

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/app.jsx, BattleReport.html
 * │ @design-part     App · .app-root · <main>
 * │ @design-note     Роутинг из useState заменён файловым @Page. Модалка discard уехала в EditorPage.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.components.sections.Footer
import org.isglaz.battlereport.components.sections.Header
import org.isglaz.battlereport.data.Api
import org.isglaz.battlereport.model.AppState
import org.isglaz.battlereport.theme.AppRootStyle

/**
 * Заменяет ваш текущий AppEntry.kt. Базовые стили переехали в theme/InitTheme.kt
 * (@InitSilk там), поэтому здесь остаётся только каркас: шапка, контент, футер.
 */
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val ctx = rememberPageContext()
        val scope = rememberCoroutineScope()

        // единственная загрузка данных на всё приложение
        LaunchedEffect(Unit) {
            if (!AppState.loaded) {
                scope.launch { AppState.seed(Api.reports(), Api.comments()) }
            }
        }

        // в редакторе футер мешает split-панели
        val isEditor = ctx.route.path == "/new"

        Surface(SmoothColorStyle.toModifier().then(AppRootStyle.toModifier()).minHeight(100.vh)) {
            Header()
            Column(Modifier.fillMaxWidth().flexGrow(1)) { content() }
            if (!isEditor) Footer()
        }
    }
}
