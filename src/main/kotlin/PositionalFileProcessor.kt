import annotations.PositionalField
import com.github.ffpojo.metadata.positional.PaddingAlign
import extensions.removeDotAndComma
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("CAST_NEVER_SUCCEEDS")
class PositionalFileGenerator() {
    fun <T : Any> generateFile(data: List<T>): ByteArray {
        val lines = mutableListOf<String>()

        for (item in data) {
            val line = generatePositionalLine(item)
            lines.add(line)
        }

        val concatenatedLines = lines.joinToString("\n")

        return stringToByteArray(concatenatedLines)
    }

    private fun <T : Any> generatePositionalLine(data: T): String {
        val clazz = data::class
        val properties = clazz.java.declaredFields.sortedBy { field ->
            field.getAnnotation(PositionalField::class.java)?.position ?: Int.MAX_VALUE
        }
        val stringBuilder = StringBuilder()
        for (property in properties) {
            val positionalFieldAnnotation = property.getAnnotation(PositionalField::class.java)
            if (positionalFieldAnnotation != null) {
                property.isAccessible = true
                var fieldValue = property.get(data)?.toString() ?: ""
                val positionStart = positionalFieldAnnotation.positionStart
                val positionEnd = positionalFieldAnnotation.positionEnd
                val length = positionalFieldAnnotation.length
                val paddingChar = positionalFieldAnnotation.paddingChar
                val mask = positionalFieldAnnotation.mask
                val fractionalDigits = positionalFieldAnnotation.fractionalDigits
                val uppercase = positionalFieldAnnotation.uppercase
                val align = positionalFieldAnnotation.paddingAlign
                val regex = positionalFieldAnnotation.regex

                val regexToValidateIfFieldIsLocalDate = """^\d{4}-\d{2}-\d{2}$"""

                val valueNew: String = buildString {
                    if (regex.isNotEmpty()) {
                        fieldValue = fieldValue.replace(Regex(regex), "")
                    }

                    if (fieldValue.matches(Regex(regexToValidateIfFieldIsLocalDate))) {
                        val localDate = LocalDate.parse(fieldValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        fieldValue = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    }

                    if (!uppercase) {
                        fieldValue = fieldValue.lowercase()
                    } else {
                        fieldValue = fieldValue.uppercase()
                    }

                    if (mask.isNotEmpty()) {
                        fieldValue as LocalDate
                        fieldValue = fieldValue.format(mask)
                    }

                    if (fractionalDigits > 0) {
                        val value = BigDecimal(fieldValue)
                        fieldValue = String.format("%.${fractionalDigits}f", value).removeDotAndComma()
                    }

                    append(fieldValue)
                }


                val formattedValue = when (align) {
                    PaddingAlign.LEFT -> valueNew.padEnd(length, paddingChar)
                    PaddingAlign.RIGHT -> valueNew.padStart(length, paddingChar)
                }

                val truncatedValue = if (positionStart >= 0 && positionEnd <= formattedValue.length) {
                    formattedValue.substring(positionStart, positionEnd)
                } else {
                    formattedValue
                }

                stringBuilder.append(truncatedValue)
            }
        }

        return stringBuilder.toString()
    }

    private fun stringToByteArray(inputString: String) = inputString.toByteArray(Charsets.UTF_8)
}


