package org.iglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/ui.jsx
 * │ @design-part     StatRow
 * │ @design-note     Порядок метрик: лайки, комментарии, просмотры, дата справа через margin-left:auto.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.iglaz.battlereport.theme.T

/** Ряд метрик: лайки / комментарии / просмотры / дата справа. */
@Composable
fun StatRow(
    likes: Int,
    comments: Int,
    views: Int,
    date: String? = null,
    liked: Boolean = false,
    onLike: (() -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth().gap(20.px), verticalAlignment = Alignment.CenterVertically) {
        Stat(fmtNum(likes), liked, onLike) {
            if (liked) FaHeart(style = IconStyle.FILLED) else FaHeart(style = IconStyle.OUTLINE)
        }
        Stat(fmtNum(comments)) { FaComment(style = IconStyle.OUTLINE) }
        Stat(fmtNum(views)) { FaEye() }
        date?.let {
            SpanText(timeAgo(it), Modifier.margin(left = autoLength).color(T.Faint).fontSize(13.px))
        }
    }
}

@Composable
private fun Stat(value: String, active: Boolean = false, onClick: (() -> Unit)? = null, icon: @Composable () -> Unit) {
    Row(
        Modifier.gap(6.px).fontSize(13.px).fontWeight(500)
            .color(if (active) T.Accent else T.Muted)
            .thenIf(onClick != null) { Modifier.cursor(Cursor.Pointer).onClick { onClick!!() } },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        SpanText(value)
    }
}
