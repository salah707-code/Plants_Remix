package com.example.plantencyclopedia.domain

object ArabicTextNormalizer {

    /**
     * Normalizes Arabic text for tolerant search matching:
     * - Unifies all forms of Alef (أ, إ, آ, ٱ) to simple 'ا'
     * - Normalizes Taa Marbuta (ة) to 'ه'
     * - Normalizes Alef Maqsura (ى) to 'ي'
     * - Normalizes Waw with Hamza (ؤ) and Yaa with Hamza (ئ)
     * - Removes Arabic Tashkeel / Harakat (diacritics)
     * - Removes Tatweel / Kashida (ـ)
     * - Trims and converts to lowercase
     */
    fun normalize(text: String): String {
        if (text.isBlank()) return ""

        val sb = StringBuilder(text.length)
        for (ch in text) {
            when (ch) {
                // Remove Tashkeel / Harakat
                '\u064B', // Fathatan
                '\u064C', // Dammatan
                '\u064D', // Kasratan
                '\u064E', // Fatha
                '\u064F', // Damma
                '\u0650', // Kasra
                '\u0651', // Shadda
                '\u0652', // Sukun
                '\u0640'  // Tatweel
                -> continue

                // Normalize Alef
                '\u0622', '\u0623', '\u0625', '\u0671' -> sb.append('\u0627')

                // Normalize Taa Marbuta to Haa
                '\u0629' -> sb.append('\u0647')

                // Normalize Alef Maqsura to Yaa
                '\u0649' -> sb.append('\u064A')

                // Normalize Hamza variants
                '\u0624', '\u0626' -> sb.append('\u0621')

                else -> sb.append(ch.lowercaseChar())
            }
        }

        return sb.toString().trim()
    }

    /**
     * Checks whether [candidate] contains [query] considering Arabic normalization.
     */
    fun containsNormalized(candidate: String, query: String): Boolean {
        if (query.isBlank()) return true
        val normCandidate = normalize(candidate)
        val normQuery = normalize(query)
        return normCandidate.contains(normQuery)
    }
}
