package exceptions

class InvalidFieldSizeException(fieldName: String, expectedLength: Int, actualLength: Int) :
    Exception("The field '$fieldName' must have a length of $expectedLength characters, but it has $actualLength characters.")