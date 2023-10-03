import annotations.PositionalField
import enums.PaddingAlign
import exceptions.InvalidFieldSizeException
import exceptions.MandatoryException
import extensions.removeDotAndComma
import extensions.unaccented
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

class PositionalFileGenerator {
    companion object {
        private const val BATCH_SIZE_DEFAULT = 10_000
        fun <T : Any> generateFile(data: List<T>, batchSize: Int? = null): ByteArrayInputStream {
            val concatenatedLines = StringBuilder()
            val batchSizeFinal = batchSize ?: BATCH_SIZE_DEFAULT
            val totalBatches = (data.size + batchSizeFinal - 1) / batchSizeFinal

            for (batchIndex in 0..<totalBatches) {
                val start = batchIndex * batchSizeFinal
                val end = min(start + batchSizeFinal, data.size)

                for (index in start..<end) {
                    val line = generatePositionalLine(data[index])
                    concatenatedLines.append(line).append('\n')
                }
            }

            return stringToByteArray(concatenatedLines.toString())
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
                    val length = positionalFieldAnnotation.size
                    val paddingChar = positionalFieldAnnotation.paddingChar
                    val mask = positionalFieldAnnotation.mask
                    val fractionalDigits = positionalFieldAnnotation.fractionalDigits
                    val uppercase = positionalFieldAnnotation.upperCase
                    val align = positionalFieldAnnotation.paddingAlign
                    val regex = positionalFieldAnnotation.regex
                    val mandatory = positionalFieldAnnotation.mandatory

                    val regexToValidateFieldIsLocalDate = """^\d{4}-\d{2}-\d{2}$""".toRegex()
                    val regexToValidateFieldIsMoney = """^\d{1,3}(\.\d+)${'$'}""".toRegex()

                    if (mandatory && fieldValue.isBlank()) {
                        throw MandatoryException(property.name)
                    }

                    val valueNew: String = buildString {

                        if (regex.isNotEmpty()) {
                            fieldValue = fieldValue.replace(Regex(regex), "")
                        }

                        if (fieldValue.matches(regexToValidateFieldIsLocalDate)) {
                            val localDate = LocalDate.parse(fieldValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            fieldValue = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        }

                        fieldValue = if (!uppercase) {
                            fieldValue.lowercase()
                        } else {
                            fieldValue.uppercase()
                        }

                        if (mask.isNotEmpty()) {
                            val localDate = LocalDate.parse(fieldValue, DateTimeFormatter.ofPattern("yyyyMMdd"))
                            fieldValue = localDate.format(DateTimeFormatter.ofPattern(mask))
                        }

                        if (fractionalDigits > 0) {
                            if (fieldValue.isNotBlank()) {
                                val value = BigDecimal(fieldValue)
                                val scale = value.scale()
                                val zerosToAdd = fractionalDigits - scale

                                fieldValue = if (zerosToAdd > 0) {
                                    val scaledValue = value.setScale(fractionalDigits, RoundingMode.HALF_UP)
                                    scaledValue.toString().removeDotAndComma()
                                } else {
                                    fieldValue.removeDotAndComma()
                                }
                            }
                        }

                        if (fieldValue.matches(regexToValidateFieldIsMoney)) {
                            fieldValue = fieldValue.removeDotAndComma()
                        }

                        append(fieldValue.take(length))
                    }

                    val formattedValue = when (align) {
                        PaddingAlign.END -> valueNew.padEnd(length, paddingChar)
                        PaddingAlign.START -> valueNew.padStart(length, paddingChar)
                    }

                    if (length > 0 && formattedValue.length != length) {
                        throw InvalidFieldSizeException(
                            property.name,
                            length,
                            length
                        )
                    }
                    stringBuilder.append(formattedValue.unaccented())
                }
            }

            return stringBuilder.toString()
        }

        private fun stringToByteArray(inputString: String) = inputString.toByteArray(Charsets.UTF_8).inputStream()
    }
}
