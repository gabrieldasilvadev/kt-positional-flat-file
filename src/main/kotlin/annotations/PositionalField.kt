package annotations

import enums.PaddingAlign

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PositionalField(
    val position: Int,
    val size: Int,
    val regex: String = "",
    val paddingAlign: PaddingAlign = PaddingAlign.LEFT,
    val mask: String = "",
    val fractionalDigits: Int = 0,
    val uppercase: Boolean = true,
    val paddingChar: Char = ' '
)
