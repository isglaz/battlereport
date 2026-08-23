package org.isglaz.battlereport.i18n

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/i18n.jsx
 * │ @design-part     STRINGS.en / STRINGS.ru · L() · useLang()/setLang()
 * │ @design-note     Ключи совпадают буква-в-букву. Новая строка в интерфейсе = новый ключ в ОБОИХ файлах.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import kotlinx.browser.localStorage

/**
 * Порт br/i18n.jsx. В React язык хранится в модульной переменной + Set подписчиков;
 * в Compose это обычный MutableState — перерисовка дерева происходит сама.
 */
enum class Lang(val id: String, val label: String) {
    RU("ru", "Русский"),
    EN("en", "English");

    companion object {
        fun of(id: String?) = entries.firstOrNull { it.id == id } ?: EN
    }
}

private const val LANG_KEY = "br_lang"

object I18n {
    var lang by mutableStateOf(Lang.of(runCatching { localStorage.getItem(LANG_KEY) }.getOrNull()))
        private set

    fun setLang(l: Lang) {
        lang = l
        runCatching { localStorage.setItem(LANG_KEY, l.id) }
        kotlinx.browser.document.documentElement?.setAttribute("lang", l.id)
    }
}

/**
 * L("key") из прототипа. Читает I18n.lang → любой композабл, который её вызывает,
 * автоматически подписан на смену языка.
 */
@Composable
@ReadOnlyComposable
fun L(key: String, vararg vars: Pair<String, Any>): String = format(strings(I18n.lang), key, vars)

/** Не-композабельный вариант, для строк вне composition (aria-label, title). */
fun l(key: String, vararg vars: Pair<String, Any>): String = format(strings(I18n.lang), key, vars)

private fun format(dict: Map<String, String>, key: String, vars: Array<out Pair<String, Any>>): String {
    var s = dict[key] ?: EN[key] ?: key
    vars.forEach { (k, v) -> s = s.replace("{$k}", v.toString()) }
    return s
}

private fun strings(l: Lang) = if (l == Lang.RU) RU else EN

private val EN = mapOf(
    "newReport" to "New Report",
    "myProfile" to "My profile",
    "notifications" to "Notifications",
    "logout" to "Log out",
    "support" to "Support",
    "backToReports" to "Back to reports",
    "postedOn" to "Posted {date}",
    "views" to "{n} views",
    "commentsCount" to "{n} comments",
    "comments" to "Comments",
    "commentPlaceholder" to "Share your thoughts on this battle…",
    "cancel" to "Cancel",
    "comment" to "Comment",
    "noComments" to "No comments yet — be the first to weigh in.",
    "reply" to "Reply",
    "replyTo" to "Reply to {name}…",
    "wargame" to "Wargame",
    "viewWargame" to "View wargame",
    "sessionDetails" to "Session details",
    "played" to "Played",
    "duration" to "Duration",
    "opponent" to "Opponent",
    "solo" to "Solo / not listed",
    "editProfile" to "Edit profile",
    "joined" to "Joined {year}",
    "yourReports" to "Your reports",
    "reportsBy" to "Reports by {name}",
    "noReportsYet" to "No reports published yet.",
    "allWargames" to "All wargames",
    "released" to "Released",
    "reports" to "Reports",
    "publisher" to "Publisher",
    "designer" to "Designer",
    "reportsFor" to "Reports for {title}",
    "reportCount" to "{n} reports",
    "noReportsForGame" to "No reports for this wargame yet.",
    "searchWargames" to "Search wargames",
    "noWargamesMatch" to "No wargames match “{q}”.",
    "reportTitlePh" to "Report title…",
    "saving" to "Saving…",
    "saved" to "Saved",
    "discard" to "Discard",
    "create" to "Create",
    "addWargame" to "+ Wargame",
    "addOpponent" to "+ Opponent",
    "pickFromDb" to "Pick from the database",
    "searchPlayers" to "Search players",
    "bodyPh" to "Describe the setup, the turning points, and how it ended. Drop in a board photo with the image button.",
    "previewTitle" to "Report title",
    "previewEmpty" to "Your formatted report will appear here as you type.",
    "noMatches" to "No matches.",
    "searchPh" to "Search…",
    "hours" to "Hours",
    "minutes" to "Minutes",
    "discardTitle" to "Discard this report?",
    "discardText" to "Your draft and autosaved progress will be permanently deleted. This can't be undone.",
    "keepEditing" to "Keep editing",
    "today" to "today",
    "yesterday" to "yesterday",
    "daysAgo" to "{n}d ago",
    "weeksAgo" to "{n}w ago",
)

private val RU = mapOf(
    "newReport" to "Новый отчёт",
    "myProfile" to "Мой профиль",
    "notifications" to "Уведомления",
    "logout" to "Выйти",
    "support" to "Поддержка",
    "backToReports" to "Ко всем отчётам",
    "postedOn" to "Опубликовано {date}",
    "views" to "{n} просмотров",
    "commentsCount" to "Комментариев: {n}",
    "comments" to "Комментарии",
    "commentPlaceholder" to "Поделитесь мыслями об этой партии…",
    "cancel" to "Отмена",
    "comment" to "Отправить",
    "noComments" to "Комментариев пока нет — будьте первым.",
    "reply" to "Ответить",
    "replyTo" to "Ответ игроку {name}…",
    "wargame" to "Варгейм",
    "viewWargame" to "Открыть варгейм",
    "sessionDetails" to "О партии",
    "played" to "Сыграно",
    "duration" to "Длительность",
    "opponent" to "Соперник",
    "solo" to "Соло / не указан",
    "editProfile" to "Редактировать профиль",
    "joined" to "С нами с {year}",
    "yourReports" to "Ваши отчёты",
    "reportsBy" to "Отчёты игрока {name}",
    "noReportsYet" to "Отчётов пока нет.",
    "allWargames" to "Все варгеймы",
    "released" to "Год выхода",
    "reports" to "Отчёты",
    "publisher" to "Издатель",
    "designer" to "Дизайнер",
    "reportsFor" to "Отчёты по игре {title}",
    "reportCount" to "Отчётов: {n}",
    "noReportsForGame" to "По этому варгейму отчётов пока нет.",
    "searchWargames" to "Поиск по варгеймам",
    "noWargamesMatch" to "Ничего не найдено по запросу «{q}».",
    "reportTitlePh" to "Заголовок отчёта…",
    "saving" to "Сохранение…",
    "saved" to "Сохранено",
    "discard" to "Удалить",
    "create" to "Опубликовать",
    "addWargame" to "+ Варгейм",
    "addOpponent" to "+ Соперник",
    "pickFromDb" to "Выберите из базы",
    "searchPlayers" to "Поиск игроков",
    "bodyPh" to "Опишите расстановку, переломные моменты и чем всё закончилось. Фото поля добавьте кнопкой с картинкой.",
    "previewTitle" to "Заголовок отчёта",
    "previewEmpty" to "Здесь появится отформатированный отчёт по мере набора.",
    "noMatches" to "Ничего не найдено.",
    "searchPh" to "Поиск…",
    "hours" to "Часы",
    "minutes" to "Минуты",
    "discardTitle" to "Удалить этот отчёт?",
    "discardText" to "Черновик и автосохранённый прогресс будут удалены безвозвратно. Отменить это будет нельзя.",
    "keepEditing" to "Продолжить правку",
    "today" to "сегодня",
    "yesterday" to "вчера",
    "daysAgo" to "{n} дн. назад",
    "weeksAgo" to "{n} нед. назад",
)
