package net.inferno.matrixia.data

import androidx.compose.runtime.Stable

@Stable
data class MatrixCalculatorState(
    var inputA: String = "",
    var inputB: String = "",
    var output: String? = null,
    var operation: Operation? = null,
)