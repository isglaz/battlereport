package org.iglaz.battlereport.model

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/data.jsx
 * │ @design-part     USERS · WARGAMES · REPORTS · COMMENTS
 * │ @design-note     Поля соответствуют полям объектов в data.jsx (opponent → opponentId).
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import kotlinx.serialization.Serializable

/**
 * Модели фронта. Специально плоские и совпадают с формой JSON, который отдаст
 * ваш Ktor-сервер (см. server/src/main/kotlin/UsersService.kt как образец Exposed-сервиса).
 */
@Serializable
data class User(
    val id: String,
    val name: String,
    val handle: String,
    val joined: String,
    val bio: String = "",
    /** ключ в облачном хранилище, напр. "users/42/avatar.webp"; null → рисуем avatarColor */
    val avatarKey: String? = null,
    /** oklch-строка: единственный цвет во всём интерфейсе. Fallback, когда avatarKey == null */
    val avatarColor: String = "oklch(0.27 0 0)",
)

@Serializable
data class Wargame(
    val id: String,
    val title: String,
    val year: Int,
    val publisher: String,
    val designer: String,
    /** Fallback, когда coverKey == null */
    val boxLabel: String,
    val tagline: String,
    val desc: String,
    /** ключ в облачном хранилище; null → рисуем boxLabel */
    val coverKey: String? = null,
)

@Serializable
data class Report(
    val id: String,
    val authorId: String,
    val wargameId: String,
    val title: String,
    /** ISO yyyy-MM-dd */
    val date: String,
    val likes: Int = 0,
    val views: Int = 0,
    val opponentId: String? = null,
    val duration: String = "",
    val preview: String = "",
    /** markdown */
    val body: String = "",
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
