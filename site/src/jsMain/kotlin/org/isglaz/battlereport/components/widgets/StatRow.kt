package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/ui.jsx
 * │ @design-part     StatRow
 * │ @design-note     Metric order: likes, comments, views, date on the right via margin-left:auto.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
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
import org.isglaz.battlereport.theme.T

/** Row of metrics: likes / comments / views / date on the right. */
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
