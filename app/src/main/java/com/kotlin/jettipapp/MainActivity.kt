package com.kotlin.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotlin.jettipapp.component.InputField
import com.kotlin.jettipapp.ui.theme.JetTipAppTheme
import com.kotlin.jettipapp.util.calculateTotalBill
import com.kotlin.jettipapp.util.calculateTotalTip
import com.kotlin.jettipapp.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(context: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            context()
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    Surface(modifier = Modifier.padding(12.dp)) {
        Column() {
            BillForm {
            }
        }
    }

}

@ExperimentalComposeUiApi
@Composable
fun TopHeader(count: Double = 16.0) {
    Surface(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(150.dp),
        color = Color(0xFFE9D7F7),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val value = "%.2f".format(count)
            Text(text = "Total Per Person", style = MaterialTheme.typography.h5)
            Text(
                text = "$$value",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm( modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val splitByState = remember {
        mutableStateOf(1)
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val range = IntRange(start = 1, endInclusive = 100)
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    TopHeader(count = totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(modifier = Modifier,
                valueState = totalBillState,
                labelId = "Enter Bill Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            splitByState.value =
                                if (splitByState.value > 1) splitByState.value - 1 else 1
                            totalPerPersonState.value = calculateTotalBill(
                                totalBillState.value.toDouble(),
                                tipPercentage,
                                splitByState.value
                            )
                        })
                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .padding(horizontal = 9.dp)
                                .align(Alignment.CenterVertically)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if (splitByState.value < range.last) splitByState.value += 1
                            totalPerPersonState.value = calculateTotalBill(
                                totalBillState.value.toDouble(),
                                tipPercentage,
                                splitByState.value
                            )
                        })
                    }
                }
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 3.dp)) {
                    Text(text = "Tip", modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.h6)
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        value = sliderPositionState.value,
                        steps = 5,
                        onValueChangeFinished = {

                        },
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(totalBillState.value.toDouble(), tipPercentage)
                            totalPerPersonState.value = calculateTotalBill(
                                totalBillState.value.toDouble(), tipPercentage, splitByState.value
                            )
                        })
                }
            } else {
                Box {}
            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Text(text = "hi")
        }
    }
}