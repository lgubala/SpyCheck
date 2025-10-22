package com.example.spycheck.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.db.tracking.TrackingEvent
import com.example.spycheck.ui.main.viewmodels.HistoryViewModel
import com.example.spycheck.ui.theme.Amber
import com.example.spycheck.ui.theme.BackgroundDark
import com.example.spycheck.ui.theme.Crimson
import com.example.spycheck.ui.theme.IcyBlue
import com.example.spycheck.ui.theme.LightGreen
import com.example.spycheck.ui.theme.SurfaceDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val trackingEvents by viewModel.trackingEvents.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        SurfaceDark
                    )
                )
            )
    ) {
        // Statistics Header
        val stats = calculateStats(trackingEvents)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundDark
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(id = R.string.history_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                StatRow(stringResource(id = R.string.history_total_requests), trackingEvents.size.toString(), Crimson)
                Spacer(modifier = Modifier.height(8.dp))
                StatRow(stringResource(id = R.string.history_unique_apps), stats.uniqueApps.toString(),LightGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatRow(stringResource(id = R.string.history_unique_domains), stats.uniqueDomains.toString(), Amber)
                Spacer(modifier = Modifier.height(8.dp))
                StatRow(stringResource(id = R.string.history_most_active), stats.mostActiveApp, IcyBlue)
            }
        }

        // Events List
        if (trackingEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.history_no_tracking_yet),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trackingEvents) { event ->
                    TrackingEventItem(event)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun TrackingEventItem(event: TrackingEvent) {
    val formattedTime = remember(event.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
            .format(Date(event.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Counter badge
                if (event.count > 1) {
                    Surface(
                        color = Crimson.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${event.count}×",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Crimson,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.domain,
                    fontSize = 14.sp,
                    color = Crimson,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            if (event.packageName != "system") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.packageName,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

data class HistoryStats(
    val uniqueApps: Int,
    val uniqueDomains: Int,
    val mostActiveApp: String
)

@Composable
fun calculateStats(events: List<TrackingEvent>): HistoryStats {
    val naString = stringResource(id = R.string.history_na)
    if (events.isEmpty()) {
        return HistoryStats(0, 0, naString)
    }

    val uniqueApps = events.map { it.packageName }.distinct().size
    val uniqueDomains = events.map { it.domain }.distinct().size
    val mostActiveApp = events.groupBy { it.appName }
        .maxByOrNull { it.value.sumOf { event -> event.count } }
        ?.key ?: naString

    return HistoryStats(uniqueApps, uniqueDomains, mostActiveApp)
}
