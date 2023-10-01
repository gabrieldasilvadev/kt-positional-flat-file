package exceptions

class MandatoryException(fieldName: String) : Exception("Field '$fieldName' is mandatory")