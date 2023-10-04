package annotations

import enums.PadAlign

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PositionalField(
    val position: Int,
    val size: Int,
    val padAlign: PadAlign = PadAlign.END,
    val padChar: Char = ' ',
    val regex: String = "",
    val mask: String = "",
    val fractionDigits: Int = 0,
    val upperCase: Boolean = true,
    val mandatory: Boolean = false
)
