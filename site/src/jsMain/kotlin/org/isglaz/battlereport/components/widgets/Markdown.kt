package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/md.jsx, br/styles.css
 * │ @design-part     renderMarkdown() · .md
 * │ @design-note     В прототипе свой мини-парсер; здесь npm marked. Стили .md — в theme/Theme.kt MarkdownStyle.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.dom.Div
import org.isglaz.battlereport.theme.MarkdownStyle

/**
 * Живой markdown (лента пишется в редакторе, значит парсер нужен в рантайме).
 * kobwebx-markdown у вас подключён, но он компилирует .md ФАЙЛЫ в страницы на этапе сборки —
 * для превью по мере набора нужен рантайм-парсер, поэтому берём npm-библиотеку marked:
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
