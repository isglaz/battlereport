package org.isglaz.battlereport.pages

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/report.jsx
 * │ @design-part     ReportView · CommentItem · CommentRow · metaItem()
 * │ @design-note     Layout: padding-left 340 / right 120, sticky 340px sidebar. NOT ContainerStyle.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.TextArea
import org.isglaz.battlereport.components.widgets.*
import org.isglaz.battlereport.i18n.L
import org.isglaz.battlereport.i18n.l
import org.isglaz.battlereport.model.*
import org.isglaz.battlereport.theme.*

/**
 * Report page. IMPORTANT: the centered ContainerStyle is not used here -
 * the design asks for a wide empty field on the left and a sidebar pinned to the right edge:
 * padding-left 340px / padding-right 120px, sidebar fixed at 340px.
 */
@Page("/reports/{id}")
@Composable
fun ReportPage() {
    val ctx = rememberPageContext()
    val id = ctx.route.params["id"] ?: return
    val report = AppState.report(id) ?: return
    val author = SampleData.users[report.authorId]
    val game = SampleData.wargames[report.wargameId]

    Column(Modifier.fillMaxWidth().padding(left = 340.px, right = 120.px, top = 24.px, bottom = 90.px)) {
        Btn({ ctx.router.navigateTo("/") }, BtnKind.Quiet, Modifier.margin(bottom = 18.px).alignSelf(AlignSelf.Start)) {
            FaArrowLeft(size = IconSize.SM)
            SpanText(L("backToReports"))
        }

        Row(Modifier.fillMaxWidth().gap(24.px), verticalAlignment = Alignment.Top) {
            /* ---- main column ---- */
            Column(Modifier.flexGrow(1).minWidth(0.px)) {
                Card(Modifier.padding(topBottom = 38.px, leftRight = 44.px)) {
                    game?.let {
                        Chip(it.title, Modifier.margin(bottom = 16.px).alignSelf(AlignSelf.Start),
                            onClick = { ctx.router.navigateTo("/wargames/${it.id}") })
                    }
                    SpanText(report.title, Modifier.fontSize(38.px).fontWeight(600).lineHeight(1.12).margin(bottom = 20.px))

                    Row(
                        Modifier.fillMaxWidth().gap(14.px).padding(bottom = 24.px).margin(bottom = 28.px)
                            .borderBottom(1.px, LineStyle.Solid, T.Border),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Avatar(author, 46) { ctx.router.navigateTo("/users/${report.authorId}") }
                        Column {
                            SpanText(author?.name.orEmpty(), Modifier.fontSize(16.px).fontWeight(500)
                                .cursor(Cursor.Pointer).onClick { ctx.router.navigateTo("/users/${report.authorId}") })
                            SpanText(
                                "${L("postedOn", "date" to formatDate(report.date))} · ${L("views", "n" to fmtNum(report.views))}",
                                Modifier.fontSize(13.5.px).color(T.Faint)
                            )
                        }
                    }

                    MarkdownBody(report.body.replace(Regex("^\\s*#\\s+.*\\n+"), ""))

                    Divider(Modifier.margin(top = 32.px, bottom = 20.px))
                    Row(Modifier.gap(14.px), verticalAlignment = Alignment.CenterVertically) {
                        val liked = AppState.isLiked(report)
                        Btn({ AppState.toggleLike(report) }, if (liked) BtnKind.Soft else BtnKind.Ghost) {
                            if (liked) FaHeart(style = IconStyle.FILLED) else FaHeart(style = IconStyle.OUTLINE)
                            SpanText(fmtNum(AppState.likeCount(report)))
                        }
                        Row(Modifier.gap(7.px).color(T.Muted).fontSize(14.5.px),
                            verticalAlignment = Alignment.CenterVertically) {
                            FaComment(style = IconStyle.OUTLINE)
                            SpanText(L("commentsCount", "n" to AppState.commentCount(report.id)))
                        }
                    }
                }

                CommentsSection(report.id)
            }

            /* ---- sidebar ---- */
            Column(
                Modifier.width(340.px).flexShrink(0).gap(16.px)
                    .position(Position.Sticky).top(88.px)
            ) {
                game?.let { g ->
                    Card(Modifier.overflow(Overflow.Hidden)) {
                        Placeholder(g.boxLabel, Modifier.fillMaxWidth().height(150.px))
                        Column(Modifier.padding(18.px)) {
                            SpanText(L("wargame"), EyebrowStyle.toModifier().margin(bottom = 4.px))
                            SpanText(g.title, Modifier.fontSize(17.px).fontWeight(600).margin(bottom = 4.px))
                            SpanText("${g.year} · ${g.publisher}", Modifier.fontSize(13.5.px).color(T.Muted).margin(bottom = 14.px))
                            Btn({ ctx.router.navigateTo("/wargames/${g.id}") }, BtnKind.Soft,
                                Modifier.fillMaxWidth().justifyContent(JustifyContent.Center)) {
                                SpanText(L("viewWargame"))
                                FaArrowUpRightFromSquare(size = IconSize.XS)
                            }
                        }
                    }
                }

                Card(Modifier.padding(18.px).gap(16.px)) {
                    SpanText(L("sessionDetails"), EyebrowStyle.toModifier())
                    MetaItem(L("played"), formatDate(report.date)) { FaCalendar() }
                    MetaItem(L("duration"), report.duration) { FaClock() }
                    val opp = report.opponentId?.let { SampleData.users[it] }
                    MetaItem(L("opponent"), opp?.name ?: L("solo"),
                        onClick = opp?.let { { ctx.router.navigateTo("/users/${it.id}") } }) { FaUser() }
                }
            }
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String, onClick: (() -> Unit)? = null, icon: @Composable () -> Unit) {
    Row(Modifier.gap(10.px), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.px).borderRadius(10.px).backgroundColor(T.Surface2)
                .border(1.px, LineStyle.Solid, T.Border).color(T.Muted),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Column(Modifier.lineHeight(1.25)) {
            SpanText(label, EyebrowStyle.toModifier())
            SpanText(value, Modifier.fontSize(14.5.px).fontWeight(500).whiteSpace(WhiteSpace.NoWrap)
                .then(if (onClick != null) Modifier.cursor(Cursor.Pointer).onClick { onClick() } else Modifier))
        }
    }
}

/** Comments: a flat list, replies indented under their parent. */
@Composable
private fun CommentsSection(reportId: String) {
    val me = SampleData.me
    val list = AppState.commentsOf(reportId)
    var draft by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<String?>(null) }
    var replyText by remember { mutableStateOf("") }
    val today = "2026-06-06"

    Column(Modifier.fillMaxWidth().margin(top = 28.px)) {
        Row(Modifier.gap(6.px).margin(bottom = 16.px), verticalAlignment = Alignment.Bottom) {
            SpanText(L("comments"), Modifier.fontSize(19.px).fontWeight(600))
            SpanText("· ${list.size}", Modifier.color(T.Faint).fontSize(19.px).fontWeight(400))
        }

        // new comment form
        Card(Modifier.padding(16.px).margin(bottom = 18.px)) {
            Row(Modifier.fillMaxWidth().gap(12.px)) {
                Avatar(me, 38)
                Column(Modifier.flexGrow(1).gap(10.px)) {
                    TextArea(draft, attrs = InputStyle.toModifier().minHeight(44.px).lineHeight(1.5)
                        .resize(Resize.Vertical).toAttrs {
                            attr("rows", if (draft.isEmpty()) "1" else "3")
                            attr("placeholder", l("commentPlaceholder"))
                            onInput { draft = it.value }
                        })
                    if (draft.isNotBlank()) {
                        Row(Modifier.fillMaxWidth().gap(8.px).justifyContent(JustifyContent.End)) {
                            Btn({ draft = "" }, BtnKind.Quiet) { SpanText(L("cancel")) }
                            Btn({
                                AppState.addComment(reportId, me.id, draft.trim(), today)
                                draft = ""
                            }) { FaPaperPlane(size = IconSize.SM); SpanText(L("comment")) }
                        }
                    }
                }
            }
        }

        if (list.isEmpty()) {
            SpanText(L("noComments"), Modifier.fillMaxWidth().textAlign(TextAlign.Center)
                .padding(topBottom = 28.px).color(T.Faint).fontSize(14.5.px))
        }

        list.forEach { c ->
            Column(Modifier.fillMaxWidth().borderBottom(1.px, LineStyle.Solid, T.Border)) {
                CommentRow(c, 38)

                Column(Modifier.fillMaxWidth().padding(left = 50.px)) {
                    Row(Modifier.gap(6.px).padding(topBottom = 2.px).color(T.Muted)
                        .fontSize(13.px).fontWeight(500).cursor(Cursor.Pointer)
                        .onClick { replyTo = if (replyTo == c.id) null else c.id; replyText = "" },
                        verticalAlignment = Alignment.CenterVertically) {
                        FaReply(size = IconSize.XS)
                        SpanText(L("reply"))
                    }

                    // replies - with a line on the left, as in the prototype
                    if (c.replies.isNotEmpty()) {
                        Column(Modifier.fillMaxWidth().margin(bottom = 6.px).padding(left = 14.px)
                            .borderLeft(1.px, LineStyle.Solid, T.Border)) {
                            c.replies.forEach { CommentRow(it, 32) }
                        }
                    }

                    if (replyTo == c.id) {
                        Row(Modifier.fillMaxWidth().gap(10.px).margin(bottom = 14.px)) {
                            Avatar(me, 32)
                            Column(Modifier.flexGrow(1).gap(8.px)) {
                                TextArea(replyText, attrs = InputStyle.toModifier()
                                    .minHeight(40.px).fontSize(14.5.px).resize(Resize.Vertical).toAttrs {
                                        attr("rows", "2")
                                        attr("placeholder",
                                            l("replyTo", "name" to (SampleData.users[c.authorId]?.name ?: me.name)))
                                        onInput { replyText = it.value }
                                    })
                                Row(Modifier.fillMaxWidth().gap(8.px).justifyContent(JustifyContent.End)) {
                                    Btn({ replyTo = null; replyText = "" }, BtnKind.Quiet) { SpanText(L("cancel")) }
                                    Btn({
                                        AppState.addReply(reportId, c.id, me.id, replyText.trim(), today)
                                        replyTo = null; replyText = ""
                                    }, enabled = replyText.isNotBlank()) {
                                        FaPaperPlane(size = IconSize.XS); SpanText(L("reply"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(c: Comment, avatarSize: Int) {
    val ctx = rememberPageContext()
    val author = SampleData.users[c.authorId]
    Row(Modifier.fillMaxWidth().gap(12.px).padding(topBottom = 14.px, leftRight = 4.px)) {
        Avatar(author, avatarSize) { author?.let { ctx.router.navigateTo("/users/${it.id}") } }
        Column(Modifier.flexGrow(1).minWidth(0.px).gap(3.px)) {
            Row(Modifier.gap(8.px), verticalAlignment = Alignment.Bottom) {
                SpanText(author?.name.orEmpty(), Modifier.fontSize(14.5.px).fontWeight(500))
                SpanText(timeAgo(c.date), Modifier.fontSize(12.5.px).color(T.Faint))
            }
            SpanText(c.text, Modifier.fontSize(15.px).color(T.Ink2).lineHeight(1.55))
        }
    }
}
