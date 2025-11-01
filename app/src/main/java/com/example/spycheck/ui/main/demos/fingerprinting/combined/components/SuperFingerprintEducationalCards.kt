package com.example.spycheck.ui.main.demos.fingerprinting.combined.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R

@Composable
fun SuperFingerprintExplanationCard() {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.fp_super_edu_complete_tracking),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorResource(R.color.danger_red)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.fp_super_edu_what_is),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorResource(R.color.warning)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.fp_super_edu_companies_combine),
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val combinations = context.resources.getStringArray(R.array.fp_super_edu_combinations)

            combinations.forEach { combo ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stringResource(R.string.fp_super_edu_bullet), color = colorResource(R.color.info_blue), fontSize = 12.sp)
                    Text(
                        text = combo,
                        fontSize = 11.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_super_edu_math_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colorResource(R.color.info_blue)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.fp_super_edu_math_text),
                        fontSize = 11.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_super_edu_companies_warning),
                    fontSize = 11.sp,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun SuperFingerprintRealWorldCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.fp_super_edu_real_world_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.danger_red)
                )
                Text(
                    text = if (expanded) stringResource(R.string.fp_super_edu_arrow_down) else stringResource(R.string.fp_super_edu_arrow_right),
                    color = colorResource(R.color.text_primary).copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SuperRealWorldScenario(
                        number = stringResource(R.string.fp_super_edu_scenario_1_num),
                        title = stringResource(R.string.fp_super_edu_scenario_1_title),
                        story = stringResource(R.string.fp_super_edu_scenario_1_story),
                        impact = stringResource(R.string.fp_super_edu_scenario_1_impact)
                    )

                    SuperRealWorldScenario(
                        number = stringResource(R.string.fp_super_edu_scenario_2_num),
                        title = stringResource(R.string.fp_super_edu_scenario_2_title),
                        story = stringResource(R.string.fp_super_edu_scenario_2_story),
                        impact = stringResource(R.string.fp_super_edu_scenario_2_impact)
                    )

                    SuperRealWorldScenario(
                        number = stringResource(R.string.fp_super_edu_scenario_3_num),
                        title = stringResource(R.string.fp_super_edu_scenario_3_title),
                        story = stringResource(R.string.fp_super_edu_scenario_3_story),
                        impact = stringResource(R.string.fp_super_edu_scenario_3_impact)
                    )

                    SuperRealWorldScenario(
                        number = stringResource(R.string.fp_super_edu_scenario_4_num),
                        title = stringResource(R.string.fp_super_edu_scenario_4_title),
                        story = stringResource(R.string.fp_super_edu_scenario_4_story),
                        impact = stringResource(R.string.fp_super_edu_scenario_4_impact)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.card_background)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fp_super_edu_reality_warning),
                            fontSize = 11.sp,
                            color = colorResource(R.color.warning),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.fp_super_edu_tap_to_see),
                    fontSize = 11.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SuperRealWorldScenario(
    number: String,
    title: String,
    story: String,
    impact: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_background)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(colorResource(R.color.danger_red), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorResource(R.color.white)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story,
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.info_blue).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stringResource(R.string.fp_super_edu_lightbulb), fontSize = 12.sp)
                    Text(
                        text = impact,
                        fontSize = 10.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SuperFingerprintProtectionCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.info_blue).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.fp_super_edu_protection_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(R.color.info_blue)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.fp_super_edu_harsh_truth),
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProtectionMethodCard(
                method = stringResource(R.string.fp_super_edu_method_vpn),
                effectiveness = stringResource(R.string.fp_super_edu_effectiveness_0),
                reason = stringResource(R.string.fp_super_edu_reason_vpn),
                color = colorResource(R.color.danger_red)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = stringResource(R.string.fp_super_edu_method_private),
                effectiveness = stringResource(R.string.fp_super_edu_effectiveness_0),
                reason = stringResource(R.string.fp_super_edu_reason_private),
                color = colorResource(R.color.danger_red)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = stringResource(R.string.fp_super_edu_method_adblocker),
                effectiveness = stringResource(R.string.fp_super_edu_effectiveness_5),
                reason = stringResource(R.string.fp_super_edu_reason_adblocker),
                color = colorResource(R.color.danger_red)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = stringResource(R.string.fp_super_edu_method_reset),
                effectiveness = stringResource(R.string.fp_super_edu_effectiveness_30),
                reason = stringResource(R.string.fp_super_edu_reason_reset),
                color = colorResource(R.color.warning)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = stringResource(R.string.fp_super_edu_method_device),
                effectiveness = stringResource(R.string.fp_super_edu_effectiveness_90),
                reason = stringResource(R.string.fp_super_edu_reason_device),
                color = colorResource(R.color.info_blue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_super_edu_best_strategy),
                    fontSize = 11.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ProtectionMethodCard(
    method: String,
    effectiveness: String,
    reason: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colorResource(R.color.white)
                )
                Text(
                    text = reason,
                    fontSize = 10.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
            Text(
                text = effectiveness,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}