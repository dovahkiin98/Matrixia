package net.inferno.matrixia

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.PyException
import kotlinx.coroutines.launch
import net.inferno.matrixia.data.MatrixCalculatorState
import net.inferno.matrixia.data.Operation

@Composable
fun MainActivityUI(
    viewModel: MainViewModel = viewModel(),
) {
    var state by viewModel.state
    val error by viewModel.error.collectAsState()

    MainActivityUI(
        state = state,
        error = error,
        onInputChanged = { newState ->
            state = newState
        },
        onCalculate = viewModel::calculate,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun MainActivityUI(
    state: MatrixCalculatorState,
    onInputChanged: (MatrixCalculatorState) -> Unit,
    onCalculate: (Operation) -> Unit,
    error: Exception? = null,
) {
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    val outputBringIntoViewRequester = remember { BringIntoViewRequester() }

    val topAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    LaunchedEffect(error) {
        when (error) {
            is PyException -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        "Array sizes are not compatible",
                        withDismissAction = true,
                    )
                }
            }

            is Exception -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        error.message ?: error.toString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    val calculate = { op: Operation ->
        onCalculate(op)

        coroutineScope.launch { outputBringIntoViewRequester.bringIntoView() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Matrixia")
                },
                actions = {
                    TextButton(
                        onClick = {
                            onInputChanged(MatrixCalculatorState())
                        },
                    ) {
                        Text("Clear")
                    }
                },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(
                        onClick = {
                            calculate(Operation.ADD)
                        },
                    ) {
                        Text("+", fontSize = 24.sp)
                    }

                    IconButton(
                        onClick = {
                            calculate(Operation.SUBTRACT)
                        },
                    ) {
                        Text("-", fontSize = 24.sp)
                    }

                    IconButton(
                        onClick = {
                            calculate(Operation.MULTIPLY)
                        },
                    ) {
                        Text("×", fontSize = 24.sp)
                    }
                },
                windowInsets = WindowInsets.ime,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(padding)

        ) {
            OutlinedTextField(
                value = state.inputA,
                onValueChange = {
                    onInputChanged(state.copy(inputA = it))
                },
                label = {
                    Text("[A]")
                },
                placeholder = {
                    Text("1 2 3\n4 5 6")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                ),
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.inputB,
                onValueChange = {
                    onInputChanged(state.copy(inputB = it))
                },
                label = {
                    Text("[B]")
                },
                placeholder = {
                    Text("1 2 3\n4 5 6")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                ),
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            if (!state.output.isNullOrEmpty()) {
                OutlinedTextField(
                    value = state.output!!,
                    onValueChange = {
                    },
                    label = {
                        Text("[C] = [A] ${state.operation!!} [B]")
                    },
                    readOnly = true,
                    modifier = Modifier
                        .height(240.dp)
                        .fillMaxWidth()
                        .bringIntoViewRequester(outputBringIntoViewRequester),
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun MainActivityUIPreview() {
    var state by remember { mutableStateOf(MatrixCalculatorState()) }

    MaterialTheme {
        MainActivityUI(
            state = state,
            onInputChanged = { newState ->
                state = newState
            },
            onCalculate = {
                state.output = "1 2 3"
            },
        )
    }
}