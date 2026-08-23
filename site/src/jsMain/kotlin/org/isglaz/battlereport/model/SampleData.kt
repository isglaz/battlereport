package org.isglaz.battlereport.model

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/data.jsx
 * │ @design-part     USERS/WARGAMES/REPORTS/COMMENTS · AVATAR_COLORS · BODY_ASL…BODY_SL
 * │ @design-note     Тексты отчётов в прототипе полные — при желании копируются как есть в raw strings.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

/**
 * Сид-данные из br/data.jsx — чтобы страницы можно было верстать до появления API.
 * Полные тексты отчётов лежат в прототипе (BODY_ASL и т.д.); здесь оставлены
 * первые абзацы, дописать можно копипастой.
 */
object SampleData {

    val users: Map<String, User> = listOf(
        User("greywolf", "GreyWolf", "@greywolf", "2021",
            "Eastern Front grognard. ASL since '04. Will defend a stone building to the last squad.",
            avatarColor = "oklch(0.55 0.12 290)"),
        User("oberstk", "OberstK", "@oberstk", "2019",
            "Combat Commander evangelist. Card-driven or it didn't happen.",
            avatarColor = "oklch(0.58 0.11 30)"),
        User("hexhammer", "HexHammer", "@hexhammer", "2022",
            "Solo gamer, AAR writer, hex purist. Grinding through the whole CoH series.",
            avatarColor = "oklch(0.55 0.1 155)"),
        User("mapfolder", "MapFolder", "@mapfolder", "2020",
            "I photograph boards more than I play them. Memoir '44 league organizer.",
            avatarColor = "oklch(0.56 0.11 230)"),
        User("saunders", "Sgt_Saunders", "@saunders", "2018",
            "Squad Leader old guard. Teaching my kid the original rulebook.",
            avatarColor = "oklch(0.55 0.12 60)"),
        User("redtithe", "RedTithe", "@redtithe", "2023",
            "New to the hobby, big on Twilight Struggle. Soviet apologist.",
            avatarColor = "oklch(0.55 0.12 350)"),
    ).associateBy { it.id }

    val wargames: Map<String, Wargame> = listOf(
        Wargame("asl", "Advanced Squad Leader", 1985, "Avalon Hill / MMP", "Don Greenwood, John Hill",
            "ASL core box art", "The definitive tactical WWII system.",
            "The granddaddy of tactical hex-and-counter wargaming, and still the deepest."),
        Wargame("cce", "Combat Commander: Europe", 2006, "GMT Games", "Chad Jensen",
            "Combat Commander box art", "Card-driven tactical chaos.",
            "A card-driven tactical game of WWII infantry combat in the European theatre."),
        Wargame("coh", "Conflict of Heroes: Awakening the Bear", 2008, "Academy Games", "Uwe Eickert",
            "Conflict of Heroes box art", "Action-point tactics on the Eastern Front.",
            "Russia 1941–42, rendered through an elegant action-point system."),
        Wargame("ts", "Twilight Struggle", 2005, "GMT Games", "Ananda Gupta, Jason Matthews",
            "Twilight Struggle box art", "The entire Cold War in two hands of cards.",
            "A two-player card-driven game covering the 45-year Cold War struggle."),
        Wargame("m44", "Memoir '44", 2004, "Days of Wonder", "Richard Borg",
            "Memoir '44 box art", "Command & Colors goes to Normandy.",
            "A light, scenario-driven WWII game built on the Command & Colors engine."),
        Wargame("sl", "Squad Leader", 1977, "Avalon Hill", "John Hill",
            "Squad Leader box art", "The game that started a system.",
            "The 1977 original that spawned an entire genre."),
    ).associateBy { it.id }

    val reports = listOf(
        Report("r1", "greywolf", "asl", "Last Stand at the Kreuzberg Farmhouse",
            "2026-06-04", 47, 1208, "oberstk", "6h 30m",
            "Played Scenario A12 — \"Hill 253\" with @oberstk last Saturday. He took the Germans; I drew the Soviet attackers…",
            """
            Played **Scenario A12 — "Hill 253"** with @oberstk last Saturday. He took the Germans;
            I drew the Soviet attackers. Six and a half hours, two pots of coffee, one heated rules
            argument about wall advantage.

            ## The opening
            My plan was simple: pin the MG nest in the stone building with smoke, then roll two squads
            up the eastern gully. It did not survive contact.
            """.trimIndent()),
        Report("r2", "oberstk", "cce", "Fate Deck Betrayal at the Crossroads",
            "2026-06-02", 63, 1944, "hexhammer", "2h 10m",
            "There is no game that punishes a good plan quite like Combat Commander…",
            "There is no game that punishes a good plan quite like Combat Commander."),
        Report("r3", "hexhammer", "coh", "Teaching My Brother the Eastern Front",
            "2026-05-29", 38, 902, null, "3h 00m",
            "Conflict of Heroes is the game I reach for when someone says wargames look too complicated…",
            "Conflict of Heroes is the game I reach for when someone says wargames look too complicated."),
        Report("r4", "redtithe", "ts", "DEFCON 2 and a Prayer",
            "2026-05-27", 71, 2310, "oberstk", "3h 45m",
            "My first proper Twilight Struggle game against @oberstk and I am hooked…",
            "My first proper Twilight Struggle game against @oberstk and I am hooked."),
        Report("r5", "mapfolder", "m44", "Omaha Beach, One More Time",
            "2026-05-24", 29, 760, "saunders", "0h 55m",
            "League night. The Omaha Beach scenario never gets easier for the Allies…",
            "League night. The Omaha Beach scenario never gets easier for the Allies."),
        Report("r6", "saunders", "sl", "Programmed Instruction, 49 Years On",
            "2026-05-20", 55, 1402, null, "1h 40m",
            "Dug the 1977 Squad Leader out of the closet to teach my son Scenario 1…",
            "Dug the 1977 Squad Leader out of the closet to teach my son Scenario 1."),
    )

    val comments: Map<String, List<Comment>> = mapOf(
        "r1" to listOf(
            Comment("c1", "oberstk", "2026-06-04",
                "Great write-up. The wall advantage rule absolutely supports my reading. Rematch is on.",
                listOf(Comment("r1a", "greywolf", "2026-06-05", "Bring the Soviets. I'll bring the rulebook."))),
            Comment("c2", "hexhammer", "2026-06-05", "That 47mm AT gun saving the day is so ASL."),
            Comment("c3", "mapfolder", "2026-06-05", "Need more board photos! What mapboards did you use?"),
        ),
        "r2" to listOf(
            Comment("c4", "greywolf", "2026-06-02", "Sniper! on turn two is just cruel."),
            Comment("c5", "redtithe", "2026-06-03", "This is the report that makes me buy Combat Commander."),
        ),
        "r3" to listOf(Comment("c6", "saunders", "2026-05-30", "CoH is exactly what I'd hand a new player today.")),
        "r4" to listOf(
            Comment("c7", "oberstk", "2026-05-27", "You held those scoring cards WAY too long in the Mid War."),
            Comment("c8", "greywolf", "2026-05-28", "DEFCON suicide is a rite of passage. Congrats."),
        ),
        "r5" to emptyList(),
        "r6" to listOf(Comment("c9", "mapfolder", "2026-05-21", "The original boards have such character.")),
    )

    /** Текущий пользователь — заглушка до появления аутентификации. */
    val me get() = users.getValue("greywolf")
}
