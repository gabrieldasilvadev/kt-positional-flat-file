package extensions

import java.text.Normalizer

fun String.removeDotAndComma(): String {
    return this.replace(".", "").replace(",", "")
}

fun String.unaccented(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "")
}