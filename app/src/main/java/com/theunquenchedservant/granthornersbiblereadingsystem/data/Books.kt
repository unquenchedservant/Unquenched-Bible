package com.theunquenchedservant.granthornersbiblereadingsystem.data

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log

object Books {
    val BOOK_CHAPTERS : Map<String, Int> = mapOf(
            "genesis" to 50, "exodus" to 40, "leviticus" to 27, "numbers" to 36,
            "deuteronomy" to 34, "joshua" to 24, "judges" to 21, "ruth" to 4,
            "samuel1" to 31, "samuel2" to 24, "kings1" to 22, "kings2" to 25,
            "chronicles1" to 29, "chronicles2" to 36, "ezra" to 10, "nehemiah" to 13,
            "esther" to 10, "job" to 42, "psalm" to 150, "proverbs" to 31,
            "ecclesiastes" to 12, "song" to 8, "isaiah" to 66, "jeremiah" to 52,
            "lamentations" to 5, "ezekiel" to 48, "daniel" to 12, "hosea" to 14,
            "joel" to 3, "amos" to 9, "obadiah" to 1, "jonah" to 4,
            "micah" to 7, "nahum" to 3, "habakkuk" to 3, "zephaniah" to 3,
            "haggai" to 2, "zechariah" to 14, "malachi" to 4, "matthew" to 28,
            "mark" to 16, "luke" to 24, "john" to 21, "acts" to 28,
            "romans" to 16, "corinthians1" to 16, "corinthians2" to 13, "galatians" to 6,
            "ephesians" to 6, "philippians" to 4, "colossians" to 4, "thessalonians1" to 5,
            "thessalonians2" to 3, "timothy1" to 6, "timothy2" to 4, "titus" to 3,
            "philemon" to 1, "hebrews" to 13, "james" to 5, "peter1" to 5,
            "peter2" to 3, "john1" to 5, "john2" to 1, "john3" to 1,
            "jude" to 1, "revelation" to 22
    )
    val BOOK_NAMES : Map<String, String> = mapOf(
            "genesis" to "Genesis", "exodus" to "Exodus", "leviticus" to "Leviticus", "numbers" to "Numbers",
            "deuteronomy" to "Deuteronomy", "joshua" to "Joshua", "judges" to "Judges", "ruth" to "Ruth",
            "samuel1" to "1 Samuel", "samuel2" to "2 Samuel", "kings1" to "1 Kings", "kings2" to "2 Kings",
            "chronicles1" to "1 Chronicles", "chronicles2" to "2 Chronicles", "ezra" to "Ezra", "nehemiah" to "Nehemiah",
            "esther" to "Esther", "job" to "Job", "psalm" to "Psalm", "proverbs" to "Proverbs",
            "ecclesiastes" to "Ecclesiastes", "song" to "Song of Solomon", "isaiah" to "Isaiah", "jeremiah" to "Jeremiah",
            "lamentations" to "Lamentations", "ezekiel" to "Ezekiel", "daniel" to "Daniel", "hosea" to "Hosea",
            "joel" to "Joel", "amos" to "Amos", "obadiah" to "Obadiah", "jonah" to "Jonah",
            "micah" to "Micah", "nahum" to "Nahum", "habakkuk" to "Habakkuk", "zephaniah" to "Zephaniah",
            "haggai" to "Haggai", "zechariah" to "Zechariah", "malachi" to "Malachi", "matthew" to "Matthew",
            "mark" to "Mark", "luke" to "Luke", "john" to "John", "acts" to "Acts",
            "romans" to "Romans", "corinthians1" to "1 Corinthians", "corinthians2" to "2 Corinthians", "galatians" to "Galatians",
            "ephesians" to "Ephesians", "philippians" to "Philippians", "colossians" to "Colossians", "thessalonians1" to "1 Thessalonians",
            "thessalonians2" to "2 Thessalonians", "timothy1" to "1 Timothy", "timothy2" to "2 Timothy", "titus" to "Titus",
            "philemon" to "Philemon", "hebrews" to "Hebrews", "james" to "James", "peter1" to "1 Peter",
            "peter2" to "2 Peter", "john1" to "1 John",  "john2" to "2 John", "john3" to "3 John",
            "jude" to "Jude", "revelation" to "Revelation"
    )
    val BOOK_NAMES_CODED : Map<String, String> = mapOf(
            "Genesis" to "genesis", "Exodus" to "exodus", "Leviticus" to "leviticus", "Numbers" to "numbers",
            "Deuteronomy" to "deuteronomy", "Joshua" to "joshua", "Judges" to "judges", "Ruth" to "ruth",
            "1 Samuel" to "samuel1", "2 Samuel" to "samuel2", "1 Kings" to "kings1", "2 Kings" to "kings2",
            "1 Chronicles" to "chronicles1", "2 Chronicles" to "chronicles2", "Ezra" to "ezra", "Nehemiah" to "nehemiah",
            "Esther" to "esther", "Job" to "job", "Psalm" to "psalm", "Psalms" to "psalm", "Proverbs" to "proverbs",
            "Ecclesiastes" to "ecclesiastes", "Song of Solomon" to "song", "Isaiah" to "isaiah", "Jeremiah" to "jeremiah",
            "Lamentations" to "lamentations", "Ezekiel" to "ezekiel", "Daniel" to "daniel", "Hosea" to "hosea",
            "Joel" to "joel", "Amos" to "amos", "Obadiah" to "obadiah", "Jonah" to "jonah",
            "Micah" to "micah", "Nahum" to "nahum", "Habakkuk" to "habakkuk", "Zephaniah" to "zephaniah",
            "Haggai" to "haggai", "Zechariah" to "zechariah", "Malachi" to "malachi", "Matthew" to "matthew",
            "Mark" to "mark", "Luke" to "luke", "John" to "john", "Acts" to "acts",
            "Romans" to "romans", "1 Corinthians" to "corinthians1", "2 Corinthians" to "corinthians2", "Galatians" to "galatians",
            "Ephesians" to "ephesians", "Philippians" to "philippians", "Colossians" to "colossians", "1 Thessalonians" to "thessalonians1",
            "2 Thessalonians" to "thessalonians2", "1 Timothy" to "timothy1", "2 Timothy" to "timothy2", "Titus" to "titus",
            "Philemon" to "philemon", "Hebrews" to "hebrews", "James" to "james", "1 Peter" to "peter1",
            "2 Peter" to "peter2", "1 John" to "john1",  "2 John" to "john2", "3 John" to "john3",
            "Jude" to "jude", "Revelation" to "revelation"
    )
    val OT_BOOKS = arrayOf("genesis", "exodus", "leviticus", "numbers", "deuteronomy",
            "joshua", "judges", "ruth", "samuel1", "samuel2", "kings1", "kings2",
            "chronicles1", "chronicles2", "ezra", "nehemiah", "esther", "job",
            "psalm", "proverbs", "ecclesiastes", "song", "isaiah", "jeremiah",
            "lamentations", "ezekiel", "daniel", "hosea", "joel", "amos", "obadiah",
            "jonah", "micah", "nahum", "habakkuk", "zephaniah", "haggai", "zechariah", "malachi")
    val NT_BOOKS = arrayOf("matthew", "mark", "luke", "john", "acts", "romans", "corinthians1",
            "corinthians2", "galatians", "ephesians", "philippians", "colossians", "thessalonians1",
            "thessalonians2", "timothy1", "timothy2", "titus", "philemon", "hebrews", "james",
            "peter1", "peter2", "john1", "john2", "john3", "jude", "revelation")
    val ALL_BOOKS = arrayOf("genesis", "exodus", "leviticus", "numbers", "deuteronomy",
            "joshua", "judges", "ruth", "samuel1", "samuel2", "kings1", "kings2",
            "chronicles1", "chronicles2", "ezra", "nehemiah", "esther", "job",
            "psalm", "proverbs", "ecclesiastes", "song", "isaiah", "jeremiah",
            "lamentations", "ezekiel", "daniel", "hosea", "joel", "amos", "obadiah",
            "jonah", "micah", "nahum", "habakkuk", "zephaniah", "haggai", "zechariah", "malachi",
            "matthew", "mark", "luke", "john", "acts", "romans", "corinthians1",
            "corinthians2", "galatians", "ephesians", "philippians", "colossians", "thessalonians1",
            "thessalonians2", "timothy1", "timothy2", "titus", "philemon", "hebrews", "james",
            "peter1", "peter2", "john1", "john2", "john3", "jude", "revelation")


    val genesis_verses = mapOf(1 to 31, 2 to 25, 3 to 24, 4 to 26, 5 to 32, 6 to 22, 7 to 24, 8 to 22, 9 to 29, 10 to 32,
            11 to 32, 12 to 20, 13 to 18, 14 to 24, 15 to 21, 16 to 16, 17 to 27, 18 to 33, 19 to 38, 20 to 18,
            21 to 34, 22 to 24, 23 to 20, 24 to 67, 25 to 34, 26 to 35, 27 to 46, 28 to 22, 29 to 35, 30 to 43,
            31 to 55, 32 to 32, 33 to 20, 34 to 31, 35 to 29, 36 to 43, 37 to 36, 38 to 30, 39 to 23, 40 to 23,
            41 to 57, 42 to 38, 43 to 34, 44 to 34, 45 to 28, 46 to 34, 47 to 31, 48 to 22, 49 to 33, 50 to 26)

    val exodus_verses = mapOf(1 to 22, 2 to 25, 3 to 22, 4 to 31, 5 to 23, 6 to 30, 7 to 24, 8 to 32, 9 to 35, 10 to 29,
            11 to 10, 12 to 51, 13 to 22, 14 to 31, 15 to 27, 16 to 36, 17 to 16, 18 to 27, 19 to 25, 20 to 26,
            21 to 36, 22 to 31, 23 to 33, 24 to 18, 25 to 40, 26 to 37, 27 to 21, 28 to 43, 29 to 46, 30 to 38,
            31 to 18, 32 to 35, 33 to 23, 34 to 35, 35 to 35, 36 to 38, 37 to 29, 38 to 31, 39 to 43, 40 to 38)

    val leviticus_verses = mapOf(1 to 17, 2 to 16, 3 to 17, 4 to 35, 5 to 19, 6 to 30, 7 to 38, 8 to 36, 9 to 24, 10 to 20,
            11 to 47, 12 to 8, 13 to 59, 14 to 57, 15 to 33, 16 to 34, 17 to 16, 18 to 30, 19 to 37, 20 to 27,
            21 to 24, 22 to 33, 23 to 44, 24 to 23, 25 to 55, 26 to 46, 27 to 34)

    val numbers_verses = mapOf(1 to 54, 2 to 34, 3 to 51, 4 to 49, 5 to 31, 6 to 27, 7 to 89, 8 to 26, 9 to 23, 10 to 36,
            11 to 35, 12 to 16, 13 to 33, 14 to 45, 15 to 41, 16 to 50, 17 to 13, 18 to 32, 19 to 22, 20 to 29,
            21 to 35, 22 to 41, 23 to 30, 24 to 25, 25 to 18, 26 to 65, 27 to 23, 28 to 31, 29 to 40, 30 to 16,
            31 to 54, 32 to 42, 33 to 56, 34 to 29, 35 to 34, 36 to 13)

    val deuteronomy_verses = mapOf(1 to 46, 2 to 37, 3 to 29, 4 to 49, 5 to 33, 6 to 25, 7 to 26, 8 to 20, 9 to 29, 10 to 22,
            11 to 32, 12 to 32, 13 to 18, 14 to 29, 15 to 23, 16 to 22, 17 to 20, 18 to 22, 19 to 21, 20 to 20,
            21 to 23, 22 to 30, 23 to 25, 24 to 22, 25 to 19, 26 to 19, 27 to 26, 28 to 68, 29 to 29, 30 to 20,
            31 to 30, 32 to 52, 33 to 29, 34 to 12)

    val joshua_verses = mapOf(1 to 18, 2 to 24, 3 to 17, 4 to 24, 5 to 15, 6 to 27, 7 to 26, 8 to 35, 9 to 27, 10 to 43,
            11 to 23, 12 to 24, 13 to 33, 14 to 15, 15 to 63, 16 to 10, 17 to 18, 18 to 28, 19 to 51, 20 to 9,
            21 to 45, 22 to 34, 23 to 16, 24 to 33)

    val judges_verses = mapOf(1 to 36, 2 to 23, 3 to 31, 4 to 24, 5 to 31, 6 to 40, 7 to 25, 8 to 35, 9 to 57, 10 to 18,
            11 to 40, 12 to 15, 13 to 25, 14 to 20, 15 to 20, 16 to 31, 17 to 13, 18 to 31, 19 to 30, 20 to 48,
            21 to 25)

    val ruth_verses = mapOf(1 to 22, 2 to 23, 3 to 18, 4 to 17)

    val samuel1_verses = mapOf(1 to 28, 2 to 36, 3 to 21, 4 to 22, 5 to 12, 6 to 21, 7 to 17, 8 to 22, 9 to 27, 10 to 27,
            11 to 15, 12 to 25, 13 to 23, 14 to 52, 15 to 35, 16 to 23, 17 to 58, 18 to 30, 19 to 24, 20 to 42,
            21 to 15, 22 to 23, 23 to 29, 24 to 22, 25 to 44, 26 to 25, 27 to 12, 28 to 25, 29 to 11, 30 to 30,
            31 to 13)

    val samuel2_verses = mapOf(1 to 27, 2 to 32, 3 to 39, 4 to 12, 5 to 25, 6 to 23, 7 to 29, 8 to 18, 9 to 13, 10 to 19,
            11 to 27, 12 to 31, 13 to 39, 14 to 33, 15 to 37, 16 to 23, 17 to 29, 18 to 33, 19 to 43, 20 to 26,
            21 to 22, 22 to 51, 23 to 39, 24 to 25)

    val kings1_verses = mapOf(1 to 53, 2 to 46, 3 to 28, 4 to 34, 5 to 18, 6 to 38, 7 to 51, 8 to 66, 9 to 28, 10 to 29,
            11 to 43, 12 to 33, 13 to 34, 14 to 31, 15 to 34, 16 to 34, 17 to 24, 18 to 46, 19 to 21, 20 to 43,
            21 to 29, 22 to 53)

    val kings2_verses = mapOf(1 to 18, 2 to 25, 3 to 27, 4 to 44, 5 to 27, 6 to 33, 7 to 20, 8 to 29, 9 to 37, 10 to 36,
            11 to 21, 12 to 21, 13 to 25, 14 to 29, 15 to 38, 16 to 20, 17 to 41, 18 to 37, 19 to 37, 20 to 21,
            21 to 26, 22 to 20, 23 to 37, 24 to 20, 25 to 30)

    val chronicles1_verses = mapOf(1 to 54, 2 to 55, 3 to 24, 4 to 43, 5 to 26, 6 to 81, 7 to 40, 8 to 40, 9 to 44, 10 to 14,
            11 to 47, 12 to 40, 13 to 14, 14 to 17, 15 to 29, 16 to 43, 17 to 27, 18 to 17, 19 to 19, 20 to 8,
            21 to 30, 22 to 19, 23 to 32, 24 to 31, 25 to 31, 26 to 32, 27 to 34, 28 to 21, 29 to 30)

    val chronicles2_verses = mapOf(1 to 17, 2 to 18, 3 to 17, 4 to 22, 5 to 14, 6 to 42, 7 to 22, 8 to 18, 9 to 31, 10 to 19,
            11 to 23, 12 to 16, 13 to 22, 14 to 15, 15 to 19, 16 to 14, 17 to 19, 18 to 34, 19 to 11, 20 to 37,
            21 to 20, 22 to 12, 23 to 21, 24 to 27, 25 to 28, 26 to 23, 27 to 9, 28 to 27, 29 to 36, 30 to 27,
            31 to 21, 32 to 33, 33 to 25, 34 to 33, 35 to 27, 36 to 23)

    val ezra_verses = mapOf(1 to 11, 2 to 70, 3 to 13, 4 to 24, 5 to 17, 6 to 22, 7 to 28, 8 to 36, 9 to 15, 10 to 44)


    val nehemiah_verses = mapOf(1 to 11, 2 to 20, 3 to 32, 4 to 23, 5 to 19, 6 to 19, 7 to 73, 8 to 18, 9 to 38, 10 to 39,
            11 to 36, 12 to 47, 13 to 31)

    val esther_verses = mapOf(1 to 22, 2 to 23, 3 to 15, 4 to 17, 5 to 14, 6 to 14, 7 to 10, 8 to 17, 9 to 32, 10 to 3)


    val job_verses = mapOf(1 to 22, 2 to 13, 3 to 26, 4 to 21, 5 to 27, 6 to 30, 7 to 21, 8 to 22, 9 to 35, 10 to 22,
            11 to 20, 12 to 25, 13 to 28, 14 to 22, 15 to 35, 16 to 22, 17 to 16, 18 to 21, 19 to 29, 20 to 29,
            21 to 34, 22 to 30, 23 to 17, 24 to 25, 25 to 6, 26 to 14, 27 to 23, 28 to 28, 29 to 25, 30 to 31,
            31 to 40, 32 to 22, 33 to 33, 34 to 37, 35 to 16, 36 to 33, 37 to 24, 38 to 41, 39 to 30, 40 to 24,
            41 to 34, 42 to 17)

    val psalm_verses = mapOf(1 to 6, 2 to 12, 3 to 8, 4 to 8, 5 to 12, 6 to 10, 7 to 17, 8 to 9, 9 to 20, 10 to 18,
            11 to 7, 12 to 8, 13 to 6, 14 to 7, 15 to 5, 16 to 11, 17 to 15, 18 to 50, 19 to 14, 20 to 9,
            21 to 13, 22 to 31, 23 to 6, 24 to 10, 25 to 22, 26 to 12, 27 to 14, 28 to 9, 29 to 11, 30 to 12,
            31 to 24, 32 to 11, 33 to 22, 34 to 22, 35 to 28, 36 to 12, 37 to 40, 38 to 22, 39 to 13, 40 to 17,
            41 to 13, 42 to 11, 43 to 5, 44 to 26, 45 to 17, 46 to 11, 47 to 9, 48 to 14, 49 to 20, 50 to 23,
            51 to 19, 52 to 9, 53 to 6, 54 to 7, 55 to 23, 56 to 13, 57 to 11, 58 to 11, 59 to 17, 60 to 12,
            61 to 8, 62 to 12, 63 to 11, 64 to 10, 65 to 13, 66 to 20, 67 to 7, 68 to 35, 69 to 36, 70 to 5,
            71 to 24, 72 to 20, 73 to 28, 74 to 23, 75 to 10, 76 to 12, 77 to 20, 78 to 72, 79 to 13, 80 to 19,
            81 to 16, 82 to 8, 83 to 18, 84 to 12, 85 to 13, 86 to 17, 87 to 7, 88 to 18, 89 to 52, 90 to 17,
            91 to 16, 92 to 15, 93 to 5, 94 to 23, 95 to 11, 96 to 13, 97 to 12, 98 to 9, 99 to 9, 100 to 5,
            101 to 8, 102 to 28, 103 to 22, 104 to 35, 105 to 45, 106 to 48, 107 to 43, 108 to 13, 109 to 31, 110 to 7,
            111 to 10, 112 to 10, 113 to 9, 114 to 8, 115 to 18, 116 to 19, 117 to 2, 118 to 29, 119 to 176, 120 to 7,
            121 to 8, 122 to 9, 123 to 4, 124 to 8, 125 to 5, 126 to 6, 127 to 5, 128 to 6, 129 to 8, 130 to 8,
            131 to 3, 132 to 18, 133 to 3, 134 to 3, 135 to 21, 136 to 26, 137 to 9, 138 to 8, 139 to 24, 140 to 13,
            141 to 10, 142 to 7, 143 to 12, 144 to 15, 145 to 21, 146 to 10, 147 to 20, 148 to 14, 149 to 9, 150 to 6)

    val proverbs_verses = mapOf(1 to 33, 2 to 22, 3 to 35, 4 to 27, 5 to 23, 6 to 35, 7 to 27, 8 to 36, 9 to 18, 10 to 32,
            11 to 31, 12 to 28, 13 to 25, 14 to 35, 15 to 33, 16 to 33, 17 to 28, 18 to 24, 19 to 29, 20 to 30,
            21 to 31, 22 to 29, 23 to 35, 24 to 34, 25 to 28, 26 to 28, 27 to 27, 28 to 28, 29 to 27, 30 to 33,
            31 to 31)

    val ecclesiastes_verses = mapOf(1 to 18, 2 to 26, 3 to 22, 4 to 16, 5 to 20, 6 to 12, 7 to 29, 8 to 17, 9 to 18, 10 to 20,
            11 to 10, 12 to 14)

    val song_verses = mapOf(1 to 17, 2 to 17, 3 to 11, 4 to 16, 5 to 16, 6 to 13, 7 to 13, 8 to 14)

    val isaiah_verses = mapOf(1 to 31, 2 to 22, 3 to 26, 4 to 6, 5 to 30, 6 to 13, 7 to 25, 8 to 22, 9 to 21, 10 to 34,
            11 to 16, 12 to 6, 13 to 22, 14 to 32, 15 to 9, 16 to 14, 17 to 14, 18 to 7, 19 to 25, 20 to 6,
            21 to 17, 22 to 25, 23 to 18, 24 to 23, 25 to 12, 26 to 21, 27 to 13, 28 to 29, 29 to 24, 30 to 33,
            31 to 9, 32 to 20, 33 to 24, 34 to 17, 35 to 10, 36 to 22, 37 to 38, 38 to 22, 39 to 8, 40 to 31,
            41 to 29, 42 to 25, 43 to 28, 44 to 28, 45 to 25, 46 to 13, 47 to 15, 48 to 22, 49 to 26, 50 to 11,
            51 to 23, 52 to 15, 53 to 12, 54 to 17, 55 to 13, 56 to 12, 57 to 21, 58 to 14, 59 to 21, 60 to 22,
            61 to 11, 62 to 12, 63 to 19, 64 to 12, 65 to 25, 66 to 24)

    val jeremiah_verses = mapOf(1 to 19, 2 to 37, 3 to 25, 4 to 31, 5 to 31, 6 to 30, 7 to 34, 8 to 22, 9 to 26, 10 to 25,
            11 to 23, 12 to 17, 13 to 27, 14 to 22, 15 to 21, 16 to 21, 17 to 27, 18 to 23, 19 to 15, 20 to 18,
            21 to 14, 22 to 30, 23 to 40, 24 to 10, 25 to 38, 26 to 24, 27 to 22, 28 to 17, 29 to 32, 30 to 24,
            31 to 40, 32 to 44, 33 to 26, 34 to 22, 35 to 19, 36 to 32, 37 to 21, 38 to 28, 39 to 18, 40 to 16,
            41 to 18, 42 to 22, 43 to 13, 44 to 30, 45 to 5, 46 to 28, 47 to 7, 48 to 47, 49 to 39, 50 to 46,
            51 to 64, 52 to 34)

    val lamentations_verses = mapOf(1 to 22, 2 to 22, 3 to 66, 4 to 22, 5 to 22)

    val ezekiel_verses = mapOf(1 to 28, 2 to 10, 3 to 27, 4 to 17, 5 to 17, 6 to 14, 7 to 27, 8 to 18, 9 to 11, 10 to 22,
            11 to 25, 12 to 28, 13 to 23, 14 to 23, 15 to 8, 16 to 63, 17 to 24, 18 to 32, 19 to 14, 20 to 49,
            21 to 32, 22 to 31, 23 to 49, 24 to 27, 25 to 17, 26 to 21, 27 to 36, 28 to 26, 29 to 21, 30 to 26,
            31 to 18, 32 to 32, 33 to 33, 34 to 31, 35 to 15, 36 to 38, 37 to 28, 38 to 23, 39 to 29, 40 to 49,
            41 to 26, 42 to 20, 43 to 27, 44 to 31, 45 to 25, 46 to 24, 47 to 23, 48 to 35)

    val daniel_verses = mapOf(1 to 21, 2 to 49, 3 to 30, 4 to 37, 5 to 31, 6 to 28, 7 to 28, 8 to 27, 9 to 27, 10 to 21,
            11 to 45, 12 to 13)

    val hosea_verses = mapOf(1 to 11, 2 to 23, 3 to 5, 4 to 19, 5 to 15, 6 to 11, 7 to 16, 8 to 14, 9 to 17, 10 to 15,
            11 to 12, 12 to 14, 13 to 16, 14 to 9)

    val joel_verses = mapOf(1 to 20, 2 to 32, 3 to 21)

    val amos_verses = mapOf(1 to 15, 2 to 16, 3 to 15, 4 to 13, 5 to 27, 6 to 14, 7 to 17, 8 to 14, 9 to 15)

    val obadiah_verses = mapOf(1 to 21)

    val jonah_verses = mapOf(1 to 16, 2 to 10, 3 to 10, 4 to 11)

    val micah_verses = mapOf(1 to 16, 2 to 13, 3 to 12, 4 to 13, 5 to 15, 6 to 16, 7 to 20)

    val nahum_verses = mapOf(1 to 15, 2 to 13, 3 to 19)

    val habakkuk_verses = mapOf(1 to 17, 2 to 20, 3 to 19)

    val zephaniah_verses = mapOf(1 to 18, 2 to 15, 3 to 20)

    val haggai_verses = mapOf(1 to 15, 2 to 23)

    val zechariah_verses = mapOf(1 to 21, 2 to 13, 3 to 10, 4 to 14, 5 to 11, 6 to 15, 7 to 14, 8 to 23, 9 to 17, 10 to 12,
            11 to 17, 12 to 14, 13 to 9, 14 to 21)

    val malachi_verses = mapOf(1 to 14, 2 to 17, 3 to 18, 4 to 6)

    val matthew_verses = mapOf(1 to 25, 2 to 23, 3 to 17, 4 to 25, 5 to 48, 6 to 34, 7 to 29, 8 to 34, 9 to 38, 10 to 42,
            11 to 30, 12 to 50, 13 to 58, 14 to 36, 15 to 39, 16 to 28, 17 to 27, 18 to 35, 19 to 30, 20 to 34,
            21 to 46, 22 to 46, 23 to 39, 24 to 51, 25 to 46, 26 to 75, 27 to 66, 28 to 20)

    val mark_verses = mapOf(1 to 45, 2 to 28, 3 to 35, 4 to 41, 5 to 43, 6 to 56, 7 to 37, 8 to 38, 9 to 50, 10 to 52,
            11 to 33, 12 to 44, 13 to 37, 14 to 72, 15 to 47, 16 to 20)

    val luke_verses = mapOf(1 to 80, 2 to 52, 3 to 38, 4 to 44, 5 to 39, 6 to 49, 7 to 50, 8 to 56, 9 to 62, 10 to 42,
            11 to 54, 12 to 59, 13 to 35, 14 to 35, 15 to 32, 16 to 31, 17 to 37, 18 to 43, 19 to 48, 20 to 47,
            21 to 38, 22 to 71, 23 to 56, 24 to 53)

    val john_verses = mapOf(1 to 51, 2 to 25, 3 to 36, 4 to 54, 5 to 47, 6 to 71, 7 to 53, 8 to 59, 9 to 41, 10 to 42,
            11 to 57, 12 to 50, 13 to 38, 14 to 31, 15 to 27, 16 to 33, 17 to 26, 18 to 40, 19 to 42, 20 to 31,
            21 to 25)

    val acts_verses = mapOf(1 to 26, 2 to 47, 3 to 26, 4 to 37, 5 to 42, 6 to 15, 7 to 60, 8 to 40, 9 to 43, 10 to 48,
            11 to 30, 12 to 24, 13 to 52, 14 to 28, 15 to 41, 16 to 40, 17 to 34, 18 to 28, 19 to 41, 20 to 38,
            21 to 40, 22 to 30, 23 to 35, 24 to 27, 25 to 22, 26 to 32, 27 to 44, 28 to 31)

    val romans_verses = mapOf(1 to 32, 2 to 29, 3 to 31, 4 to 25, 5 to 21, 6 to 23, 7 to 25, 8 to 39, 9 to 33, 10 to 21,
            11 to 36, 12 to 21, 13 to 14, 14 to 23, 15 to 33, 16 to 27)

    val corinthians1_verses = mapOf(1 to 31, 2 to 16, 3 to 23, 4 to 21, 5 to 13, 6 to 20, 7 to 40, 8 to 13, 9 to 27, 10 to 33,
            11 to 34, 12 to 31, 13 to 13, 14 to 40, 15 to 58, 16 to 24)

    val corinthians2_verses = mapOf(1 to 24, 2 to 17, 3 to 18, 4 to 18, 5 to 21, 6 to 18, 7 to 16, 8 to 24, 9 to 15, 10 to 18,
            11 to 33, 12 to 21, 13 to 14)

    val galatians_verses = mapOf(1 to 24, 2 to 21, 3 to 29, 4 to 31, 5 to 26, 6 to 18)

    val ephesians_verses = mapOf(1 to 23, 2 to 22, 3 to 21, 4 to 32, 5 to 33, 6 to 24)

    val philippians_verses = mapOf(1 to 30, 2 to 30, 3 to 21, 4 to 23)

    val colossians_verses = mapOf(1 to 29, 2 to 23, 3 to 25, 4 to 18)

    val thessalonians1_verses = mapOf(1 to 10, 2 to 20, 3 to 13, 4 to 18, 5 to 28)

    val thessalonians2_verses = mapOf(1 to 12, 2 to 17, 3 to 18)

    val timothy1_verses = mapOf(1 to 20, 2 to 15, 3 to 16, 4 to 16, 5 to 25, 6 to 21)

    val timothy2_verses = mapOf(1 to 18, 2 to 26, 3 to 17, 4 to 22)

    val titus_verses = mapOf(1 to 16, 2 to 15, 3 to 15)

    val philemon_verses = mapOf(1 to 25)

    val hebrews_verses = mapOf(1 to 14, 2 to 18, 3 to 19, 4 to 16, 5 to 14, 6 to 20, 7 to 28, 8 to 13, 9 to 28, 10 to 39,
            11 to 40, 12 to 29, 13 to 25)

    val james_verses = mapOf(1 to 27, 2 to 26, 3 to 18, 4 to 17, 5 to 20)

    val peter1_verses = mapOf(1 to 25, 2 to 25, 3 to 22, 4 to 19, 5 to 14)

    val peter2_verses = mapOf(1 to 21, 2 to 22, 3 to 18)

    val john1_verses = mapOf(1 to 10, 2 to 29, 3 to 24, 4 to 21, 5 to 21)

    val john2_verses = mapOf(1 to 13)

    val john3_verses = mapOf(1 to 14)

    val jude_verses = mapOf(1 to 25)

    val revelation_verses = mapOf(1 to 20, 2 to 29, 3 to 22, 4 to 11, 5 to 14, 6 to 17, 7 to 17, 8 to 13, 9 to 21, 10 to 11,
            11 to 19, 12 to 17, 13 to 18, 14 to 20, 15 to 8, 16 to 21, 17 to 18, 18 to 24, 19 to 21, 20 to 15,
            21 to 27, 22 to 21)



    fun getVerses(book: String, Chapter:Int): Int{
        val bookMap = when(book){
            "genesis" -> genesis_verses
            "exodus" -> exodus_verses
            "leviticus" -> leviticus_verses
            "numbers" -> numbers_verses
            "deuteronomy" -> deuteronomy_verses
            "joshua" -> joshua_verses
            "judges" -> judges_verses
            "ruth" -> ruth_verses
            "samuel1" -> samuel1_verses
            "samuel2" -> samuel2_verses
            "kings1" -> kings1_verses
            "kings2" -> kings2_verses
            "chronicles1" -> chronicles1_verses
            "chronicles2" -> chronicles2_verses
            "ezra" -> ezra_verses
            "nehemiah" -> nehemiah_verses
            "esther" -> esther_verses
            "job" -> job_verses
            "psalm" -> psalm_verses
            "proverbs" -> proverbs_verses
            "ecclesiastes" -> ecclesiastes_verses
            "song" -> song_verses
            "isaiah" -> isaiah_verses
            "jeremiah" -> jeremiah_verses
            "lamentations" -> lamentations_verses
            "ezekiel" -> ezekiel_verses
            "daniel" -> daniel_verses
            "hosea" -> hosea_verses
            "joel" -> joel_verses
            "amos" -> amos_verses
            "obadiah" -> obadiah_verses
            "jonah" -> jonah_verses
            "micah" -> micah_verses
            "nahum" -> nahum_verses
            "habakkuk" -> habakkuk_verses
            "zephaniah" -> zephaniah_verses
            "haggai" -> haggai_verses
            "zechariah" -> zechariah_verses
            "malachi" -> malachi_verses
            "matthew" -> matthew_verses
            "mark" -> mark_verses
            "luke" -> luke_verses
            "john" -> john_verses
            "acts" -> acts_verses
            "romans" -> romans_verses
            "corinthians1" -> corinthians1_verses
            "corinthians2" -> corinthians2_verses
            "galatians" -> galatians_verses
            "ephesians" -> ephesians_verses
            "philippians" -> philippians_verses
            "colossians" -> colossians_verses
            "thessalonians1" -> thessalonians1_verses
            "thessalonians2" -> thessalonians2_verses
            "timothy1" -> timothy1_verses
            "timothy2" -> timothy2_verses
            "titus" -> titus_verses
            "philemon" -> philemon_verses
            "hebrews" -> hebrews_verses
            "james" -> james_verses
            "peter1" -> peter1_verses
            "peter2" -> peter2_verses
            "john1" -> john1_verses
            "john2" -> john2_verses
            "john3" -> john3_verses
            "jude" -> jude_verses
            "revelation" -> revelation_verses
            else -> {
                log("WRONG BOOK ${book}")
                genesis_verses
            }
        }
        return bookMap[Chapter]!!
    }
    fun getBooks(testament: String) : Array<String>?{
        return when(testament){
            "old"-> OT_BOOKS
            "new"-> NT_BOOKS
            "all"-> ALL_BOOKS
            else-> null
        }
    }
}