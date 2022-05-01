package converter

import java.math.BigInteger
import java.math.BigDecimal
import java.math.RoundingMode

val values = mapOf('0' to 0, '1' to 1, '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7,
    '8' to 8, '9' to 9, 'a' to 10, 'b' to 11, 'c' to 12, 'd' to 13, 'e' to 14, 'f' to 15, 'g' to 16,
    'h' to 17, 'i' to 18, 'j' to 19, 'k' to 20, 'l' to 21, 'm' to 22, 'n' to 23, 'o' to 24, 'p' to 25,
    'q' to 26, 'r' to 27, 's' to 28, 't' to 29, 'u' to 30, 'v' to 31, 'w' to 32, 'x' to 33, 'y' to 34,
    'z' to 35)

fun convertBaseToDecimal (number: String, base: String): String {
    var result = BigInteger.ZERO
    var basePower = BigInteger.ONE

    for (i in number.length - 1 downTo 0) {
        result += values.getValue(number[i]).toBigInteger() * basePower
        basePower *= base.toBigInteger()
    }
    return result.toString()
}

fun convertDecimalToBase (number: String, base: String): String {
    val divisor = base.toBigInteger()
    var result = number.toBigInteger()
//    var remainder = BigInteger.ZERO
    val remainders = mutableListOf<Char>()

    while (result >= divisor) {
        val (q,r) = result.divideAndRemainder(divisor)
        result = q
        remainders.add(values.keys.first { r.toInt() == values[it] })
    }
    remainders.add(values.keys.first { result.toInt() == values[it] })
    return remainders.joinToString("").reversed()
}

fun convertFractionFromBaseToDecimal (number: String, base: String): String {
    var result = BigDecimal.ZERO
    var basePower = base.toBigDecimal()

    for (element in number) {
        result += values.getValue(element).toBigDecimal() * "1.0000000000000000000000000".toBigDecimal() / basePower
        basePower *= base.toBigDecimal()
    }
    return result.setScale(5,RoundingMode.HALF_UP).toString()
}

fun convertFractionFromDecimalToBase (number: String, base: String): String {
    var n = number.toBigDecimal()
    val b = base.toBigDecimal()
    val digits = mutableListOf<Char>()
    for (i in 1..20) {
        val x = n * b
        val parteEntera = x.toInt()
        n = x - parteEntera.toBigDecimal()
        digits.add(values.keys.first { parteEntera == values[it] })
    }
    return digits.joinToString("")
}

fun secondMain(sourceBase: String, targetBase: String) {
    var goOn = true
    while (goOn) {
        print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) > ")
        val input = readln().lowercase()
        val integerExp = "[0-9a-z]+".toRegex()
        val decimalExp = "[0-9a-z]+.[0-9a-z]+".toRegex()
        when {
            input == "/back" -> goOn = false
            integerExp.matches(input) -> {
                println(
                    "Conversion result: ${
                        convertDecimalToBase(
                            convertBaseToDecimal(input, sourceBase),
                            targetBase
                        )
                    }"
                )
                println()
            }
            decimalExp.matches(input) -> {
                val (parteEntera, parteDecimal) = input.split(".")
                val integerPart =   convertDecimalToBase(
                    convertBaseToDecimal(parteEntera, sourceBase), targetBase
                )
                val decimalPart = convertFractionFromDecimalToBase(convertFractionFromBaseToDecimal(parteDecimal, sourceBase), targetBase)
                println("Conversion result: $integerPart.${decimalPart.subSequence(0,5)}")
                println()
            }
            else -> {
                println("Wrong input!")
            }
        }
    }
}

fun main() {
    var goOn = true

    while (goOn) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) > ")
        when (val input = readln().lowercase()) {
            "/exit" -> goOn = false
            else -> {
                val reg = """[a-z0-9]+\s+[a-z0-9]+""".toRegex()
                if (reg.matches(input)) {
                    val (sourceBase, targetBase) = input.split("""\s+""".toRegex())
                    secondMain(sourceBase, targetBase)
                } else {
                    println("Wrong input!")
                }
            }
        }
    }
}