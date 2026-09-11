package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/md.jsx, br/styles.css
 * │ @design-part     renderMarkdown() · .md
 * │ @design-note     The prototype has its own mini parser; here it is npm marked. The .md styles are in theme/Theme.kt MarkdownStyle.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.dom.Div
import org.isglaz.battlereport.theme.MarkdownStyle

/**
 * Live markdown (the body is written in the editor, so the parser is needed at runtime).
 * kobwebx-markdown is wired up, but it compiles .md FILES into pages at build time -
 * a preview while typing needs a runtime parser, hence the npm library marked:
 *
 *   jsMain.dependencies { implementation(npm("marked", "12.0.2")) }
 */
@JsModule("marked")
@JsNonModule
external object Marked {
    fun parse(src: String): String
}

@Composable
fun MarkdownBody(md: String, modifier: Modifier = Modifier) {
    Div(attrs = MarkdownStyle.toModifier().then(modifier).toAttrs {
        ref { el ->
            el.innerHTML = Marked.parse(md)
            onDispose { }
        }
    })
}
