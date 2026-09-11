package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/ui.jsx, br/styles.css
 * │ @design-part     Avatar · BoxArt · initials() · .btn/.card/.chip/.hr
 * │ @design-note     Avatar.avatarColor is the only color in the whole design.
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
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.isglaz.battlereport.model.User
import org.isglaz.battlereport.theme.*

enum class BtnKind { Primary, Ghost, Quiet, Soft, Text }

@Composable
fun Btn(
    onClick: () -> Unit,
    kind: BtnKind = BtnKind.Primary,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val variant = when (kind) {
        BtnKind.Primary -> BtnPrimaryStyle
        BtnKind.Ghost -> BtnGhostStyle
        BtnKind.Quiet -> BtnQuietStyle
        BtnKind.Soft -> BtnSoftStyle
        BtnKind.Text -> null
    }
    val base = if (kind == BtnKind.Text) BtnTextStyle.toModifier()
    else BtnStyle.toModifier().then(variant!!.toModifier())

    Button(attrs = base.then(modifier)
        .thenIf(!enabled) { Modifier.opacity(0.45).cursor(Cursor.NotAllowed) }
        .toAttrs {
            if (enabled) onClick { onClick() } else attr("disabled", "")
        }) { content() }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    hover: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        CardStyle.toModifier()
            .thenIf(hover) { CardHoverStyle.toModifier() }
            .thenIf(onClick != null) { Modifier.cursor(Cursor.Pointer).onClick { onClick!!() } }
            .then(modifier),
        content = content,
    )
}

@Composable
fun Chip(text: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    SpanText(text, ChipStyle.toModifier()
        .thenIf(onClick != null) { Modifier.cursor(Cursor.Pointer).onClick { onClick!!() } }
        .then(modifier))
}

/** Avatar: initials on a colored circle - the only spot of color in the design. */
@Composable
fun Avatar(user: User?, size: Int = 40, ring: Boolean = false, onClick: (() -> Unit)? = null) {
    Box(
        Modifier.size(size.px).borderRadius(50.percent)
            .backgroundColor(Color(user?.avatarColor ?: "oklch(0.27 0 0)"))
            .color(Colors.White).fontWeight(500)
            .fontSize((size * 0.4).px).letterSpacing((-0.02).em)
            .flexShrink(0).userSelect(UserSelect.None)
            .thenIf(ring) { Modifier.styleModifier { property("box-shadow", "0 0 0 3px ${T.Surface}, 0 0 0 4px ${T.Border}") } }
            .thenIf(onClick != null) { Modifier.cursor(Cursor.Pointer).onClick { onClick!!() } },
        contentAlignment = Alignment.Center,
    ) { SpanText(initials(user?.name)) }
}

fun initials(name: String?): String {
    val parts = (name ?: "?").replace(Regex("[@_]"), " ").trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (parts.isEmpty()) return "?"
    return if (parts.size == 1) parts[0].take(2).uppercase()
    else "${parts.first().first()}${parts.last().first()}".uppercase()
}

/** Striped placeholder instead of box art / board photo (.ph in styles.css). */
@Composable
fun Placeholder(label: String, modifier: Modifier = Modifier) {
    Box(
        Modifier.backgroundColor(Color("oklch(0.955 0 0)"))
            .styleModifier {
                property("background-image",
                    "repeating-linear-gradient(-45deg, oklch(0.9 0 0) 0 11px, oklch(0.945 0 0) 11px 22px)")
            }
            .color(Color("oklch(0.5 0 0)")).fontSize(11.px)
            .overflow(Overflow.Hidden).then(modifier),
        contentAlignment = Alignment.Center,
    ) { SpanText(label) }
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().height(1.px).backgroundColor(T.Border).then(modifier))
}

private fun Color(v: String) = org.jetbrains.compose.web.css.Color(v)
