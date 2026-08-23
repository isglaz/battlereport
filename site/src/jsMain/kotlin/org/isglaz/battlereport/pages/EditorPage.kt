package org.isglaz.battlereport.pages

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/editor.jsx
 * │ @design-part     Editor · TOOLBAR · DurationInput · FieldChip · AutoComplete · ToolBtn
 * │ @design-note     В TOOLBAR нет code-block; подписи «Markdown supported» нет. Длительность — два поля.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.TextArea
import org.isglaz.battlereport.components.widgets.*
import org.isglaz.battlereport.i18n.L
import org.isglaz.battlereport.i18n.l
import org.isglaz.battlereport.model.*
import org.isglaz.battlereport.theme.*

/**
 * Редактор: заголовок + метачипы, панель инструментов, split-панель markdown / превью.
 * Автосохранение в localStorage с дебаунсом 700ms — как в прототипе.
 * Кнопки code-block в панели НЕТ и подписи «Markdown supported» тоже — убраны в дизайне.
 */
@Page("/new")
@Composable
fun EditorPage() {
    val ctx = rememberPageContext()
    var draft by remember { mutableStateOf(AppState.loadDraft()) }
    var saving by remember { mutableStateOf(false) }
    var showDiscard by remember { mutableStateOf(false) }

    LaunchedEffect(draft) {
        saving = true
        delay(700)
        AppState.saveDraft(draft)
        saving = false
    }

    val canCreate = draft.title.isNotBlank() && draft.wargameId != null && draft.body.isNotBlank()

    Column(Modifier.fillMaxWidth().height(100.vh - T.HeaderH)) {
        /* ---- верхняя панель ---- */
        Box(Modifier.fillMaxWidth().backgroundColor(T.Surface)
            .borderBottom(1.px, LineStyle.Solid, T.Border)) {
            Column(ContainerStyle.toModifier().maxWidth(1320.px).padding(top = 18.px, bottom = 16.px)) {
                Row(Modifier.fillMaxWidth().gap(16.px), verticalAlignment = Alignment.Top) {
                    Input(InputType.Text, attrs = Modifier
                        .flexGrow(1).border(0.px).padding(0.px)
                        .outline(0.px, LineStyle.None, Colors.Transparent)
                        .backgroundColor(Colors.Transparent)
                        .fontSize(30.px).fontWeight(500).letterSpacing((-0.02).em).color(T.Ink)
                        .toAttrs {
                            value(draft.title)
                            attr("placeholder", l("reportTitlePh"))
                            onInput { draft = draft.copy(title = it.value) }
                        })

                    Row(Modifier.gap(12.px).padding(top = 6.px), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.gap(6.px).fontSize(13.px).color(T.Faint).minWidth(64.px)
                            .justifyContent(JustifyContent.End), verticalAlignment = Alignment.CenterVertically) {
                            if (saving) SpanText(L("saving"))
                            else { FaCheck(Modifier.color(T.Accent), size = IconSize.XS); SpanText(L("saved")) }
                        }
                        Btn({ showDiscard = true }, BtnKind.Ghost) { SpanText(L("discard")) }
                        Btn({
                            AppState.clearDraft()
                            ctx.router.navigateTo("/")
                        }, enabled = canCreate) { FaPaperPlane(size = IconSize.SM); SpanText(L("create")) }
                    }
                }

                /* ---- метачипы: варгейм, соперник, дата, длительность ---- */
                Row(Modifier.fillMaxWidth().gap(10.px).margin(top = 16.px).flexWrap(FlexWrap.Wrap),
                    verticalAlignment = Alignment.CenterVertically) {
                    PickerChip(
                        current = draft.wargameId?.let { SampleData.wargames[it]?.title },
                        placeholder = L("addWargame"),
                        hint = l("pickFromDb"),
                        options = SampleData.wargames.values.map { it.id to it.title },
                        onPick = { draft = draft.copy(wargameId = it) },
                        onClear = { draft = draft.copy(wargameId = null) },
                    )
                    PickerChip(
                        current = draft.opponentId?.let { SampleData.users[it]?.name },
                        placeholder = L("addOpponent"),
                        hint = l("searchPlayers"),
                        options = SampleData.users.values.filter { it.id != SampleData.me.id }.map { it.id to it.name },
                        onPick = { draft = draft.copy(opponentId = it) },
                        onClear = { draft = draft.copy(opponentId = null) },
                    )
                    FieldChip {
                        Input(InputType.Date, attrs = chipInputModifier().toAttrs {
                            value(draft.date)
                            onInput { draft = draft.copy(date = it.value) }
                        })
                    }
                    FieldChip { DurationInput(draft.duration) { draft = draft.copy(duration = it) } }
                }
            }
        }

        /* ---- панель инструментов ---- */
        Box(Modifier.fillMaxWidth().backgroundColor(T.Surface)
            .borderBottom(1.px, LineStyle.Solid, T.Border)) {
            Row(ContainerStyle.toModifier().maxWidth(1320.px).height(48.px).gap(2.px),
                verticalAlignment = Alignment.CenterVertically) {
                ToolBtn { FaHeading(size = IconSize.SM) }
                ToolBtn { FaBold(size = IconSize.SM) }
                ToolBtn { FaItalic(size = IconSize.SM) }
                ToolSep()
                ToolBtn { FaListUl(size = IconSize.SM) }
                ToolBtn { FaListOl(size = IconSize.SM) }
                ToolBtn { FaQuoteLeft(size = IconSize.SM) }
                ToolSep()
                ToolBtn { FaMinus(size = IconSize.SM) }
                ToolBtn { FaImage(size = IconSize.SM) }
                ToolBtn { FaLink(size = IconSize.SM) }
            }
        }

        /* ---- split-панель ---- */
        Row(Modifier.fillMaxWidth().flexGrow(1).minHeight(0.px)) {
            Box(Modifier.fillMaxWidth(50.percent).fillMaxHeight().overflow(Overflow.Auto)
                .backgroundColor(T.Surface).borderRight(1.px, LineStyle.Solid, T.Border)) {
                TextArea(draft.body, attrs = Modifier
                    .fillMaxWidth().fillMaxHeight().padding(topBottom = 28.px, leftRight = 44.px)
                    .border(0.px).outline(0.px, LineStyle.None, Colors.Transparent)
                    .backgroundColor(Colors.Transparent).resize(Resize.None)
                    .fontSize(15.5.px).lineHeight(1.7).color(T.Ink2)
                    .fontFamily("ui-monospace", "SF Mono", "Menlo", "monospace")
                    .toAttrs {
                        attr("placeholder", l("bodyPh"))
                        attr("spellcheck", "true")
                        onInput { draft = draft.copy(body = it.value) }
                    })
            }
            Box(Modifier.fillMaxWidth(50.percent).fillMaxHeight().overflow(Overflow.Auto).backgroundColor(T.Bg)) {
                Column(Modifier.padding(topBottom = 28.px, leftRight = 44.px)) {
                    SpanText(
                        draft.title.ifBlank { L("previewTitle") },
                        Modifier.fontSize(32.px).fontWeight(if (draft.title.isBlank()) 500 else 600)
                            .lineHeight(1.14).margin(bottom = 18.px)
                            .then(if (draft.title.isBlank()) Modifier.color(T.Faint) else Modifier)
                    )
                    if (draft.body.isNotBlank()) MarkdownBody(draft.body)
                    else SpanText(L("previewEmpty"), Modifier.color(T.Faint).fontSize(15.5.px))
                }
            }
        }
    }

    if (showDiscard) {
        DiscardDialog(
            onKeep = { showDiscard = false },
            onDiscard = { AppState.clearDraft(); ctx.router.navigateTo("/") },
        )
    }
}

/* ---------- части редактора ---------- */

private fun chipInputModifier() = Modifier
    .border(0.px).padding(0.px).outline(0.px, LineStyle.None, Colors.Transparent)
    .backgroundColor(Colors.Transparent).fontSize(13.5.px).fontWeight(500).color(T.Ink2)

@Composable
private fun FieldChip(content: @Composable () -> Unit) {
    Row(ChipStyle.toModifier().height(36.px).padding(leftRight = 12.px).gap(7.px),
        verticalAlignment = Alignment.CenterVertically) { content() }
}

/**
 * Длительность = два числовых поля «Xh / YYm».
 * Нули — только плейсхолдеры, минуты ограничены 59, добиваются до двух цифр по blur.
 */
@Composable
fun DurationInput(value: String, onChange: (String) -> Unit) {
    val parsed = Regex("""^(?:(\d+)h)?\s*(?:(\d+)m)?$""").find(value.trim())
    var h by remember(value) { mutableStateOf(parsed?.groupValues?.getOrNull(1).orEmpty()) }
    var m by remember(value) { mutableStateOf(parsed?.groupValues?.getOrNull(2).orEmpty()) }

    fun emit(nh: String, nm: String) {
        onChange(
            if (nh.isBlank() && nm.isBlank()) ""
            else "${nh.ifBlank { "0" }.toInt()}h ${nm.ifBlank { "0" }.toInt().toString().padStart(2, '0')}m"
        )
    }
    fun clean(v: String, max: Int): String {
        val d = v.filter { it.isDigit() }.take(2)
        return if (d.isEmpty()) "" else minOf(d.toInt(), max).toString()
    }

    Row(Modifier.gap(2.px), verticalAlignment = Alignment.CenterVertically) {
        NumBox(h, "0", l("hours")) { h = clean(it, 99); emit(h, m) }
        SpanText("h", Modifier.color(T.Faint).fontSize(12.5.px))
        NumBox(m, "00", l("minutes"), Modifier.margin(left = 5.px), onBlur = {
            if (m.isNotBlank()) { m = m.toInt().toString().padStart(2, '0'); emit(h, m) }
        }) { m = clean(it, 59); emit(h, m) }
        SpanText("m", Modifier.color(T.Faint).fontSize(12.5.px))
    }
}

@Composable
private fun NumBox(
    value: String,
    placeholder: String,
    aria: String,
    modifier: Modifier = Modifier,
    onBlur: () -> Unit = {},
    onChange: (String) -> Unit,
) {
    Input(InputType.Text, attrs = chipInputModifier().then(modifier)
        .width(22.px).textAlign(TextAlign.Center).toAttrs {
            value(value)
            attr("placeholder", placeholder)
            attr("aria-label", aria)
            attr("inputmode", "numeric")
            attr("maxlength", "2")
            onInput { onChange(it.value) }
            onBlur { onBlur() }
        })
}

@Composable
private fun ToolBtn(icon: @Composable () -> Unit) {
    Box(ToolBtnStyle.toModifier(), contentAlignment = Alignment.Center) { icon() }
}

private val ToolBtnStyle = com.varabyte.kobweb.silk.style.CssStyle {
    base {
        Modifier.size(34.px).borderRadius(9.px).color(T.Ink2).cursor(Cursor.Pointer)
            .transition(Transition.group(listOf("background", "color"), 120.ms))
    }
    hover { Modifier.backgroundColor(T.Surface2).color(T.Ink) }
}

@Composable
private fun ToolSep() {
    Box(Modifier.width(1.px).height(22.px).margin(leftRight = 6.px).backgroundColor(T.Border))
}

/** Выпадающий выбор варгейма / соперника — порт AutoComplete из editor.jsx. */
@Composable
private fun PickerChip(
    current: String?,
    placeholder: String,
    hint: String,
    options: List<Pair<String, String>>,
    onPick: (String) -> Unit,
    onClear: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    var q by remember { mutableStateOf("") }

    if (current != null) {
        Row(ChipStyle.toModifier().height(36.px).padding(left = 12.px, right = 6.px).gap(7.px),
            verticalAlignment = Alignment.CenterVertically) {
            SpanText(current, Modifier.fontWeight(500))
            FaXmark(Modifier.cursor(Cursor.Pointer).color(T.Accent).onClick { onClear() }, size = IconSize.XS)
        }
        return
    }

    Box {
        Row(ChipStyle.toModifier().height(36.px).padding(leftRight = 12.px).gap(7.px)
            .cursor(Cursor.Pointer).onClick { open = !open },
            verticalAlignment = Alignment.CenterVertically) {
            SpanText(placeholder)
        }
        if (open) {
            val filtered = options.filter { it.second.lowercase().contains(q.lowercase()) }
            Column(CardStyle.toModifier().position(Position.Absolute).top(40.px).left(0.px)
                .width(268.px).padding(8.px).zIndex(30)
                .styleModifier { property("box-shadow", T.ShLg) }) {
                Input(InputType.Text, attrs = InputStyle.toModifier()
                    .margin(bottom = 6.px).padding(topBottom = 8.px, leftRight = 12.px).fontSize(14.px)
                    .toAttrs {
                        value(q)
                        attr("placeholder", hint)
                        attr("autofocus", "")
                        onInput { q = it.value }
                    })
                Column(Modifier.maxHeight(220.px).overflow(Overflow.Auto)) {
                    if (filtered.isEmpty()) {
                        SpanText(L("noMatches"), Modifier.padding(topBottom = 10.px, leftRight = 12.px)
                            .color(T.Faint).fontSize(13.5.px))
                    }
                    filtered.forEach { (id, label) ->
                        SpanText(label, PickerItemStyle.toModifier().onClick {
                            onPick(id); open = false; q = ""
                        })
                    }
                }
            }
        }
    }
}

private val PickerItemStyle = com.varabyte.kobweb.silk.style.CssStyle {
    base {
        Modifier.fillMaxWidth().padding(topBottom = 9.px, leftRight = 11.px).borderRadius(8.px)
            .fontSize(14.px).fontWeight(500).cursor(Cursor.Pointer)
    }
    hover { Modifier.backgroundColor(T.Surface2) }
}

/** Модалка подтверждения удаления черновика. */
@Composable
private fun DiscardDialog(onKeep: () -> Unit, onDiscard: () -> Unit) {
    Box(
        Modifier.position(Position.Fixed).top(0.px).left(0.px).fillMaxSize().zIndex(100)
            .backgroundColor(Color("oklch(0.3 0.02 290 / 0.4)"))
            .styleModifier { property("backdrop-filter", "blur(3px)") }
            .onClick { onKeep() },
        contentAlignment = Alignment.Center,
    ) {
        Column(CardStyle.toModifier().width(100.percent).maxWidth(420.px).padding(28.px)
            .styleModifier { property("box-shadow", T.ShLg) }) {
            SpanText(L("discardTitle"), Modifier.fontSize(20.px).fontWeight(600).margin(bottom = 8.px))
            SpanText(L("discardText"), Modifier.margin(bottom = 22.px).color(T.Muted)
                .fontSize(15.px).lineHeight(1.55))
            Row(Modifier.fillMaxWidth().gap(10.px).justifyContent(JustifyContent.End)) {
                Btn(onKeep, BtnKind.Ghost) { SpanText(L("keepEditing")) }
                Btn(onDiscard, BtnKind.Primary, Modifier.backgroundColor(T.Danger)) {
                    FaTrash(size = IconSize.SM); SpanText(L("discard"))
                }
            }
        }
    }
}

private fun Color(v: String) = org.jetbrains.compose.web.css.Color(v)
