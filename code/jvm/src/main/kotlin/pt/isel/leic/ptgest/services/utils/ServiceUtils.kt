package pt.isel.leic.ptgest.services.utils

fun validateInts(vararg args: Int?) {
    for (i in args) {
        if (i != null && i < 0) {
            throw ValidationErrors.InvalidParameter.InvalidInt
        }
    }
}

fun validateStrings(vararg strings: String) {
    for (str in strings) {
        if (str.isEmpty() || str.isBlank()) {
            throw ValidationErrors.InvalidParameter.InvalidString
        }
    }
}