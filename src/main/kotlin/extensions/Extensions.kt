package extensions

import java.math.BigDecimal

fun BigDecimal.removeDotAndComma(): String {
    return this.toString().replace(".", "").replace(",", "")
}

fun String.removeDotAndComma(): String {
    return this.replace(".", "").replace(",", "")
}