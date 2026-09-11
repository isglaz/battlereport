package org.isglaz.battlereport.model

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/app.jsx
 * │ @design-part     likes/toggleLike · comments/addComment/addReply · draft + localStorage "br_draft"
 * │ @design-note     The localStorage key is the same as in the prototype.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

/**
 * Replacement for the React state from br/app.jsx: likes, comments, draft.
 * In-memory + localStorage for now. Once the endpoints appear - replace the method
 * bodies with Api calls (see data/Api.kt) and keep only the cache here.
 */
object AppState {
    private val json = Json { ignoreUnknownKeys = true }

    val reports = mutableStateListOf<Report>()
    val comments = mutableStateMapOf<String, MutableList<Comment>>()

    /** reportId -> (counter, whether the current user liked it) */
    private val likeOverrides = mutableStateMapOf<String, Pair<Int, Boolean>>()

    var loaded by mutableStateOf(false)
        private set

    fun seed(rs: List<Report>, cs: Map<String, List<Comment>>) {
        reports.clear(); reports.addAll(rs)
        comments.clear(); cs.forEach { (k, v) -> comments[k] = v.toMutableStateList() }
        loaded = true
    }

    fun report(id: String) = reports.firstOrNull { it.id == id }
    fun commentsOf(id: String): List<Comment> = comments[id] ?: emptyList()
    fun commentCount(id: String) = commentsOf(id).size

    fun likeCount(r: Report) = likeOverrides[r.id]?.first ?: r.likes
    fun isLiked(r: Report) = likeOverrides[r.id]?.second ?: false

    fun toggleLike(r: Report) {
        val liked = isLiked(r)
        likeOverrides[r.id] = (likeCount(r) + if (liked) -1 else 1) to !liked
    }

    fun addComment(reportId: String, authorId: String, text: String, today: String) {
        val list = comments.getOrPut(reportId) { mutableStateListOf() }
        list.add(Comment("c${nextId()}", authorId, today, text))
    }

    fun addReply(reportId: String, parentId: String, authorId: String, text: String, today: String) {
        val list = comments[reportId] ?: return
        val i = list.indexOfFirst { it.id == parentId }
        if (i < 0) return
        val parent = list[i]
        list[i] = parent.copy(replies = parent.replies + Comment("r${nextId()}", authorId, today, text))
    }

    private fun nextId() = kotlin.js.Date.now().toLong().toString(36)

    /* ---- draft: the same localStorage key as in the prototype ---- */
    private const val DRAFT_KEY = "br_draft"

    fun loadDraft(): Draft = runCatching {
        json.decodeFromString<Draft>(localStorage.getItem(DRAFT_KEY) ?: return Draft())
    }.getOrElse { Draft() }

    fun saveDraft(d: Draft) = runCatching {
        localStorage.setItem(DRAFT_KEY, json.encodeToString(d))
    }.let {}

    fun clearDraft() = localStorage.removeItem(DRAFT_KEY)
}
