package net.inferno.matrixia

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.inferno.matrixia.data.MatrixCalculatorState
import net.inferno.matrixia.data.Operation
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {
    val state = mutableStateOf(MatrixCalculatorState())
    val error = MutableStateFlow<Exception?>(null)

    fun calculate(op: Operation) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val arrayA = state.value.inputA.toArray()
                val arrayB = state.value.inputB.toArray()

                val result = withContext(Dispatchers.IO) {
                    when (op) {
                        Operation.ADD -> addPy(arrayA, arrayB)
                        Operation.SUBTRACT -> subtractPy(arrayA, arrayB)
                        Operation.MULTIPLY -> multiplyPy(arrayA, arrayB)
                    }
                }

                state.value = state.value.copy(
                    output = result.joinToString(),
                    operation = op,
                )
            } catch (e: Exception) {
                error.emit(e)
            }
        }
    }

    //region Python
    private fun multiplyPy(
        A: Array<DoubleArray>,
        B: Array<DoubleArray>,
    ): List<List<Double>> {
        val py = Python.getInstance()
        val module = py.getModule("testing")

        val pyObj = module.callAttr(
            "multiply",
            if (A.size > 1) A else A.first(),
            if (B.size > 1) B else B.first(),
        )

        return pyObj.toArray()
    }

    private fun addPy(
        A: Array<DoubleArray>,
        B: Array<DoubleArray>,
    ): List<List<Double>> {
        val py = Python.getInstance()
        val module = py.getModule("testing")

        val pyObj = module.callAttr(
            "plus",
            if (A.size > 1) A else A.first(),
            if (B.size > 1) B else B.first(),
        )

        return pyObj.toArray()
    }

    private fun subtractPy(
        A: Array<DoubleArray>,
        B: Array<DoubleArray>,
    ): List<List<Double>> {
        val py = Python.getInstance()
        val module = py.getModule("testing")

        val pyObj = module.callAttr(
            "minus",
            if (A.size > 1) A else A.first(),
            if (B.size > 1) B else B.first(),
        )

        return pyObj.toArray()
    }

    @Suppress("UNCHECKED_CAST")
    private fun PyObject.toArray(): List<List<Double>> {
        val result = try {
            asList().map {
                try {
                    it.toDouble()
                } catch (e: Exception) {
                    it.asList().map {
                        it.toDouble()
                    }
                }
            }
        } catch (e: Exception) {
            listOf(listOf(toDouble()))
        }

        return if (result.first() !is List<*>) {
            listOf(result) as List<List<Double>>
        } else {
            result as List<List<Double>>
        }
    }
    //endregion

    //region Helpers
    private fun List<List<Double>>.joinToString(): String {
        val formatter = DecimalFormat.getNumberInstance()

        val maxDecimals = maxOf { row ->
            row.maxOf {
                val number = formatter.format(it)
                if (number.contains('.'))
                    number.substring(number.indexOf(".")).substring(1).length
                else 0
            }
        }

        val maxWhole = maxOf { row ->
            row.maxOf { floor(it).roundToInt().toString().length }
        }

        return joinToString(separator = "\n") { row ->
            row.joinToString(separator = " ") {
                val number = formatter.format(it)

                val decimal =
                    if (number.contains('.')) number.substring(number.indexOf("."))
                        .substring(1) else null
                val whole = floor(it).roundToInt().toString()

                buildString {
//                val pad = if (BuildConfig.DEBUG) '-' else ' '
                    val pad = ' '

                    append(whole.padStart(maxWhole, pad))

                    val decimalValue = if (decimal != null) ".$decimal" else ""

                    append(decimalValue.padEnd(maxDecimals + 1, pad))
                }
            }
        }
    }

    private fun String.toArray() = split("\n").map { row ->
        row.split(" ").map { it.toDouble() }.toDoubleArray()
    }.toTypedArray()
    //endregion
}