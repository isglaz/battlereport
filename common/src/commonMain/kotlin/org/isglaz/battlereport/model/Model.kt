package org.isglaz.battlereport.model

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/data.jsx
 * │ @design-part     USERS · WARGAMES · REPORTS · COMMENTS
 * │ @design-note     Fields match the object fields in data.jsx (opponent -> opponentId).
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import kotlinx.serialization.Serializable

/**
 * Frontend models. Deliberately flat and matching the shape of the JSON the Ktor server will
 * return (see server/src/main/kotlin/UsersService.kt as a sample Exposed service).
 */
@Serializable
data class User(
    val id: String,
    val name: String,
    val handle: String,
    val joined: String,
    val bio: String = "",
    val avatarKey: String? = null,
    val avatarColor: String = "oklch(0.27 0 0)",
)

@Serializable
data class Wargame(
    val id: String,
    val title: String,
    val year: Int,
    val publisher: String,
    val designer: String,
    val boxLabel: String,
    val tagline: String,
    val desc: String,
    val coverKey: String? = null,
)

@Serializable
data class Report(
    val id: String,
    val authorId: String,
    val wargameId: String,
    val title: String,
    val date: String,
    val likes: Int = 0,
    val views: Int = 0,
    val opponentId: String? = null,
    val duration: String = "",
    val preview: String = "",
    val body: String = "",
    val images: List<ReportImage> = emptyList(),
)

@Serializable
data class ReportImage(
    val id: String,
    val storageKey: String,
    val position: Int = 0,
    val caption: String = "",
)

@Serializable
data class Comment(
    val id: String,
    val authorId: String,
    val date: String,
    val text: String,
    val replies: List<Comment> = emptyList(),
)

@Serializable
data class Draft(
    val title: String = "",
    val body: String = "",
    val wargameId: String? = null,
    val opponentId: String? = null,
    val date: String = "",
    val duration: String = "",
)
