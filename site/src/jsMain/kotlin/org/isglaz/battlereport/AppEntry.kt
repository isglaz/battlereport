package org.isglaz.battlereport

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/app.jsx, BattleReport.html
 * │ @design-part     App · .app-root · <main>
 * │ @design-note     Routing from useState replaced by file-based @Page. The discard modal moved to EditorPage.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
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
 * Replaces your current AppEntry.kt. The base styles moved to theme/InitTheme.kt
 * (@InitSilk lives there), so only the skeleton remains here: header, content, footer.
 */
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val ctx = rememberPageContext()
        val scope = rememberCoroutineScope()

        // the single data load for the whole application
        LaunchedEffect(Unit) {
            if (!AppState.loaded) {
                scope.launch { AppState.seed(Api.reports(), Api.comments()) }
            }
        }

        // in the editor the footer gets in the way of the split pane
        val isEditor = ctx.route.path == "/new"

        Surface(SmoothColorStyle.toModifier().then(AppRootStyle.toModifier()).minHeight(100.vh)) {
            Header()
            Column(Modifier.fillMaxWidth().flexGrow(1)) { content() }
            if (!isEditor) Footer()
        }
    }
}
