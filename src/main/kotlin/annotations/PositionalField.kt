package annotations

import enums.PaddingAlign

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PositionalField(
    val position: Int,
    val size: Int,
    val regex: String = "",
    val paddingAlign: PaddingAlign = PaddingAlign.END,
    val mask: String = "",
    val fractionalDigits: Int = 0,
    val upperCase: Boolean = true,
    val paddingChar: Char = ' ',
    val mandatory: Boolean = false
)
