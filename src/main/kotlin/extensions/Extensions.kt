package extensions

fun String.removeDotAndComma(): String {
    return this.replace(".", "").replace(",", "")
}