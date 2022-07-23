package helpers

import java.util.*

/**
 * Created by Aleksandr on 05.10.2015.
 */
object Translit {
    val UPPER = 1
    val LOWER = 2
    val map = makeTranslitMap()

    private fun makeTranslitMap(): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map["nice"] = "найс"
        map["try"] = "трай"
        map["mine"] = "майн"
        map["craft"] = "крафт"
        map["oo"] = "у"
        map["a"] = "а"
        map["b"] = "б"
        map["v"] = "в"
        map["g"] = "г"
        map["d"] = "д"
        map["e"] = "е"
        map["yo"] = "ё"
        map["zh"] = "ж"
        map["z"] = "з"
        map["i"] = "и"
        map["j"] = "й"
        map["k"] = "к"
        map["l"] = "л"
        map["m"] = "м"
        map["n"] = "н"
        map["o"] = "о"
        map["p"] = "п"
        map["r"] = "р"
        map["s"] = "с"
        map["t"] = "т"
        map["u"] = "у"
        map["f"] = "ф"
        map["h"] = "х"
        map["ts"] = "ц"
        map["c"] = "ц"
        map["ch"] = "ч"
        map["sh"] = "ш"
        map["`"] = "ъ"
        map["y"] = "у"
        map["'"] = "ь"
        map["yu"] = "ю"
        map["ya"] = "я"
        map["x"] = "кс"
        map["w"] = "в"
        map["q"] = "к"
        map["iy"] = "ий"
        return map
    }

    private fun charClass(c: Char): Int {
        return if (Character.isUpperCase(c)) UPPER else LOWER
    }

    private operator fun get(s: String): String {
        val charClass = charClass(s[0])
        val result = map[s.lowercase(Locale.getDefault())]
        return if (result == null) "" else if (charClass == UPPER) (result[0].toString() + "").uppercase(Locale.getDefault()) + (if (result.length > 1) result.substring(
            1
        ) else "") else result
    }

    fun translit(text: String?): String {
        if (text == null)
            return ""

        val len = text.length
        if (len == 0) {
            return text
        }
        if (len == 1) {
            return get(text)
        }
        val sb = StringBuilder()
        var i = 0
        while (i < len) {

            // get next 2 symbols
            val toTranslate = text.substring(i, if (i <= len - 2) i + 2 else i + 1)
            // trying to translate
            var translated = get(toTranslate)
            // if these 2 symbols are not connected try to translate one by one
            if (translated.isEmpty()) {
                translated = get(toTranslate[0].toString() + "")
                sb.append(translated.ifEmpty { toTranslate[0] })
                i++
            } else {
                sb.append(translated.ifEmpty { toTranslate })
                i += 2
            }
        }
        return sb.toString()
    }

    fun translitPerWordWithWhitelist(text: String?, whitelist: List<String>): String {
        if (text == null)
            return ""
        return text.split(" ").joinToString(" ") { if (!whitelist.contains(it)) translit(it) else it }
    }
}