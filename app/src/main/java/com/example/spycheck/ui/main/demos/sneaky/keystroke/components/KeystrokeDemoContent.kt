package com.example.spycheck.ui.main.demos.sneaky.keystroke.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.keystroke.KeystrokePhase
import com.example.spycheck.ui.main.demos.sneaky.keystroke.KeystrokeDemoViewModel

@Composable
fun KeystrokeDemoContent(viewModel: KeystrokeDemoViewModel) {
    val context = LocalContext.current
    val phase by viewModel.phase.collectAsState()
    val calibrationText by viewModel.calibrationText.collectAsState()
    val testText by viewModel.testText.collectAsState()
    val detectedKeys by viewModel.detectedKeys.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (phase) {
            KeystrokePhase.INTRO -> {
                IntroContent(
                    onStart = { viewModel.setPhase(KeystrokePhase.CALIBRATION_READY) }
                )
            }

            KeystrokePhase.CALIBRATION_READY -> {
                CalibrationReadyContent(
                    onStart = {
                        viewModel.setPhase(KeystrokePhase.CALIBRATING)
                        viewModel.startCalibration()
                    }
                )
            }

            KeystrokePhase.CALIBRATING -> {
                CalibratingContent(
                    userText = calibrationText,
                    onTextChange = { viewModel.updateCalibrationText(it) },
                    detectedKeys = detectedKeys,
                    sensorData = sensorData,
                    onComplete = {
                        viewModel.finishCalibration(calibrationText)
                        viewModel.setPhase(KeystrokePhase.CALIBRATION_DONE)
                    }
                )
            }

            KeystrokePhase.CALIBRATION_DONE -> {
                CalibrationDoneContent(
                    onNext = { viewModel.setPhase(KeystrokePhase.TEST_READY) }
                )
            }

            KeystrokePhase.TEST_READY -> {
                TestReadyContent(
                    onStartTest = {
                        viewModel.updateTestText("")
                        viewModel.setPhase(KeystrokePhase.TESTING)
                        viewModel.startTest()
                    }
                )
            }

            KeystrokePhase.TESTING -> {
                TestingContent(
                    userText = testText,
                    onTextChange = { viewModel.updateTestText(it) },
                    detectedKeys = detectedKeys,
                    sensorData = sensorData,
                    onComplete = {
                        viewModel.stopMonitoring()
                        viewModel.setPhase(KeystrokePhase.RESULTS)
                    }
                )
            }

            KeystrokePhase.RESULTS -> {
                ResultsContent(
                    userText = testText,
                    detectedKeys = detectedKeys,
                    onTryAgain = {
                        viewModel.setPhase(KeystrokePhase.INTRO)
                        viewModel.updateCalibrationText("")
                        viewModel.updateTestText("")
                    }
                )
            }
        }
    }
}

@Composable
private fun IntroContent(onStart: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_intro_title),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFF6B6B)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.keystroke_intro_desc),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.keystroke_how_it_works),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4ECDC4)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.keystroke_steps),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9D4EDD)
            )
        ) {
            Icon(Icons.Default.Keyboard, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.keystroke_start_demo))
        }
    }
}

@Composable
private fun CalibrationReadyContent(onStart: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_calibration_title),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.keystroke_calibration_desc),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFBE0B).copy(alpha = 0.15f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.keystroke_youll_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFBE0B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"the quick brown fox\"",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.keystroke_start_calibration))
        }
    }
}

@Composable
private fun CalibratingContent(
    userText: String,
    onTextChange: (String) -> Unit,
    detectedKeys: List<com.example.spycheck.ui.main.demos.sneaky.keystroke.utils.KeystrokeMatch>,
    sensorData: Triple<Float, Float, Float>?,
    onComplete: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_type_hello),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Real-time sensor display
        if (sensorData != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.keystroke_sensor_reading),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.keystroke_sensor_values,
                            sensorData.first, sensorData.second, sensorData.third),
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.keystroke_detected_count, detectedKeys.size),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userText,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.keystroke_type_here)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(12.dp))



        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            enabled = userText.isNotEmpty()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.keystroke_finish_calibration))
        }
    }
}

@Composable
private fun CalibrationDoneContent(onNext: () -> Unit) {
    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF06FFA5).copy(alpha = 0.15f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.keystroke_calibration_complete),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF06FFA5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.keystroke_calibration_complete_desc),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.keystroke_proceed_to_test))
        }
    }
}

@Composable
private fun TestReadyContent(onStartTest: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_test_title),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.keystroke_test_desc),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStartTest,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6B)
            )
        ) {
            Text(stringResource(R.string.keystroke_start_test))
        }
    }
}

@Composable
private fun TestingContent(
    userText: String,
    onTextChange: (String) -> Unit,
    detectedKeys: List<com.example.spycheck.ui.main.demos.sneaky.keystroke.utils.KeystrokeMatch>,
    sensorData: Triple<Float, Float, Float>?,
    onComplete: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_type_anything),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = userText,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.keystroke_your_secret)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (detectedKeys.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.keystroke_detected_label),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detectedKeys.joinToString("") { it.detectedChar.toString() },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            enabled = userText.length >= 3
        ) {
            Text(stringResource(R.string.keystroke_see_results))
        }
    }
}

@Composable
private fun ResultsContent(
    userText: String,
    detectedKeys: List<com.example.spycheck.ui.main.demos.sneaky.keystroke.utils.KeystrokeMatch>,
    onTryAgain: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.keystroke_results_title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.keystroke_you_typed),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = userText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.keystroke_we_detected),
                    fontSize = 12.sp,
                    color = Color(0xFFFF6B6B)
                )
                Text(
                    text = if (detectedKeys.isEmpty()) {
                        stringResource(R.string.keystroke_no_matches)
                    } else {
                        detectedKeys.joinToString("") { it.detectedChar.toString() }
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )

                if (detectedKeys.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val accuracy = (detectedKeys.size.toFloat() / userText.length * 100).toInt()
                    Text(
                        text = stringResource(R.string.keystroke_accuracy, accuracy),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.keystroke_real_world_note),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onTryAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.keystroke_try_again))
        }
    }
}