package net.inferno.matrixia.data

import androidx.compose.runtime.Stable

@Stable
enum class Operation {
    ADD,
    SUBTRACT,
    MULTIPLY,
    ;

    override fun toString() = when (this) {
        ADD -> "+"
        SUBTRACT -> "-"
        MULTIPLY -> "*"
    }
}