package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Estado del display
data class CalculatorState(
    val display: String = "0"
)

// Eventos de la calculadora
sealed class CalculatorEvent {
    data class Number(val number: String) : CalculatorEvent()
    data class Operator(val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object AllClear : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object Decimal : CalculatorEvent()
}

// ViewModel con la lógica
class CalculatorViewModel : ViewModel() {
    private var number1: String = ""
    private var number2: String = ""
    private var operator: String? = null

    var state by mutableStateOf(CalculatorState())
        private set

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast()
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }

    private fun enterNumber(number: String) {
        if (operator == null) {
            number1 += number
        } else {
            number2 += number
        }
        updateDisplay()
    }

    private fun enterDecimal() {
        val currentNumber = if (operator == null) number1 else number2
        if (!currentNumber.contains(".")) {
            if (operator == null) {
                number1 += "."
            } else {
                number2 += "."
            }
            updateDisplay()
        }
    }

    private fun enterOperator(op: String) {
        if (number1.isNotBlank()) {
            operator = op
            updateDisplay()
        }
    }

    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = null
        state = state.copy(display = "0")
    }

    private fun clearLast() {
        if (operator == null) {
            number1 = number1.dropLast(1)
        } else {
            number2 = number2.dropLast(1)
        }
        updateDisplay()
    }

    private fun performCalculation() {
        val num1 = number1.toDoubleOrNull()
        val num2 = number2.toDoubleOrNull()
        if (num1 != null && num2 != null && operator != null) {
            val result = when (operator) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
                else -> 0.0
            }
            val resultString = if (result.isNaN()) "ERROR" else result.toString().removeSuffix(".0")
            number1 = if (!result.isNaN()) resultString else ""
            number2 = ""
            operator = null
            state = state.copy(display = resultString)
        }
    }

    private fun updateDisplay() {
        val displayText = buildString {
            append(number1)
            if (operator != null) append(" $operator ")
            if (number2.isNotEmpty()) append(number2)
        }
        state = state.copy(display = if (displayText.isBlank()) "0" else displayText)
    }
}
