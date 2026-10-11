package com.mattrobertson.greek.reader.plans.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.plans.ReadingPlans

/** Reading-plan controls surfaced in the app's main Settings screen. */
@Composable
fun ReadingPlanSettingsSection() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    }
    var revision by remember { mutableIntStateOf(0) }
    val activePlans = remember(revision) {
        ReadingPlans.all.indices.filter { index ->
            prefs.getInt(progressKey(index), -1) in ReadingPlans.all[index].days.indices
        }
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text("Reading plan", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(4.dp))
        if (activePlans.isEmpty()) {
            Text("Start a plan from the Plans tab to set a daily reminder or reset its progress.", style = MaterialTheme.typography.body2)
        } else activePlans.forEach { index ->
            val plan = ReadingPlans.all[index]
            val day = prefs.getInt(progressKey(index), 0)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth(), elevation = 1.dp) {
                Column(Modifier.padding(12.dp)) {
                    Text(plan.title, style = MaterialTheme.typography.subtitle1)
                    Text("Day ${day + 1} of ${plan.days.size}", style = MaterialTheme.typography.body2)
                    Spacer(Modifier.height(8.dp))
                    ReminderSettings(index, prefs) { revision++ }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            ReadingPlanReminderScheduler.cancel(context, index)
                            prefs.edit()
                                .remove(progressKey(index))
                                .putBoolean(reminderEnabledKey(index), false)
                                .apply()
                            revision++
                        }) { Text("Reset plan") }
                    }
                }
            }
        }
    }
}
