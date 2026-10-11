package com.mattrobertson.greek.reader.plans.ui

import android.content.Context
import android.content.SharedPreferences
import android.app.TimePickerDialog
import android.os.Build
import android.provider.Settings
import android.net.Uri
import android.content.Intent
import android.app.AlarmManager
import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.plans.ReadingPlan
import com.mattrobertson.greek.reader.plans.ReadingPlans
import com.mattrobertson.greek.reader.ui.lib.MaxWidthColumn
import com.mattrobertson.greek.reader.verseref.VerseRef

@Composable
fun ReadingPlansScreen(
    onReadChapter: (VerseRef) -> Unit,
    onReadToday: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    }
    var selectedPlan by remember { mutableStateOf<Int?>(null) }
    var revision by remember { mutableStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        MaxWidthColumn {
            val selected = selectedPlan
            if (selected == null) {
                PlanList(
                    prefs = prefs,
                    revision = revision,
                    onSelected = { selectedPlan = it },
                    onDismiss = onDismiss
                )
            } else {
                PlanDetails(
                    planIndex = selected,
                    prefs = prefs,
                    revision = revision,
                    onProgressChanged = { revision++ },
                    onReadChapter = onReadChapter,
                    onReadToday = { day -> onReadToday(selected, day) },
                    onBack = { selectedPlan = null }
                )
            }
        }
    }
}

@Composable
private fun PlanList(
    prefs: SharedPreferences,
    revision: Int,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = { Text("Reading plans") },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.Close, contentDescription = "Close reading plans")
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(ReadingPlans.all) { index, plan ->
            val progress = progressFor(prefs, index, revision)
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelected(index) },
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(plan.title, style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        when {
                            progress < 0 -> "Not started · ${plan.days.size} days"
                            progress >= plan.days.size -> "Completed · ${plan.days.size} days"
                            else -> "Day ${progress + 1} of ${plan.days.size}"
                        },
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.body2
                    )
                    if (progress >= 0) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (progress.coerceAtMost(plan.days.size).toFloat() / plan.days.size),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PlanDetails(
    planIndex: Int,
    prefs: SharedPreferences,
    revision: Int,
    onProgressChanged: () -> Unit,
    onReadChapter: (VerseRef) -> Unit,
    onReadToday: (Int) -> Unit,
    onBack: () -> Unit
) {
    val plan = ReadingPlans.all[planIndex]
    val progress = progressFor(prefs, planIndex, revision)
    val previewDay = when {
        progress < 0 -> 0
        progress >= plan.days.size -> plan.days.lastIndex
        else -> progress
    }

    TopAppBar(
        title = { Text(plan.title, maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back to reading plans")
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(plan.description, style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(16.dp))
            PlanAction(
                plan = plan,
                planIndex = planIndex,
                progress = progress,
                previewDay = previewDay,
                prefs = prefs,
                onProgressChanged = onProgressChanged,
                onReadToday = onReadToday
            )
            Spacer(Modifier.height(16.dp))
            Text("Schedule", style = MaterialTheme.typography.h6)
        }

        itemsIndexed(plan.days) { dayIndex, chapters ->
            val completed = progress > dayIndex
            val current = progress == dayIndex
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = if (current) 4.dp else 1.dp,
                backgroundColor = if (current) MaterialTheme.colors.primary.copy(alpha = 0.08f)
                    else MaterialTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    if (completed) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    } else {
                        Text(
                            "${dayIndex + 1}",
                            modifier = Modifier.width(34.dp),
                            fontWeight = if (current) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        chapters.forEach { ref ->
                            TextButton(
                                onClick = { onReadChapter(ref) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) {
                                Text("${ref.book.abbrv} ${ref.chapter}")
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun PlanAction(
    plan: ReadingPlan,
    planIndex: Int,
    progress: Int,
    previewDay: Int,
    prefs: SharedPreferences,
    onProgressChanged: () -> Unit,
    onReadToday: (Int) -> Unit
) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth(), elevation = 3.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            when {
                progress < 0 -> {
                    Text("Ready to begin", style = MaterialTheme.typography.h6)
                    Text("Day 1: ${plan.days.first().joinToString { "${it.book.abbrv} ${it.chapter}" }}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        setProgress(prefs, planIndex, 0)
                        onProgressChanged()
                    }) { Text("Start plan") }
                }
                progress >= plan.days.size -> {
                    Text("Plan completed", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        prefs.edit().remove(progressKey(planIndex)).apply()
                        onProgressChanged()
                    }) { Text("Reset progress") }
                }
                else -> {
                    Text("Day ${progress + 1} of ${plan.days.size}", style = MaterialTheme.typography.h6)
                    Text(plan.days[previewDay].joinToString { "${it.book.abbrv} ${it.chapter}" })
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onReadToday(previewDay) }) {
                            Text("Read today's plan")
                        }
                        OutlinedButton(onClick = {
                            setProgress(prefs, planIndex, progress + 1)
                            if (progress == plan.days.lastIndex) ReadingPlanReminderScheduler.cancel(context, planIndex)
                            onProgressChanged()
                        }) { Text(if (progress == plan.days.lastIndex) "Finish plan" else "Complete day") }
                    }
                    TextButton(onClick = {
                        prefs.edit().remove(progressKey(planIndex)).apply()
                        onProgressChanged()
                    }) { Text("Reset progress") }
                }
            }
            if (progress in 0 until plan.days.size) {
                Spacer(Modifier.height(8.dp))
                ReminderSettings(planIndex, prefs, onProgressChanged)
            }
        }
    }
}

@Composable
internal fun ReminderSettings(planIndex: Int, prefs: SharedPreferences, onChanged: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var enabled by remember(planIndex) { mutableStateOf(prefs.getBoolean(reminderEnabledKey(planIndex), false)) }
    var hour by remember(planIndex) { mutableIntStateOf(prefs.getInt(reminderHourKey(planIndex), 9)) }
    var minute by remember(planIndex) { mutableIntStateOf(prefs.getInt(reminderMinuteKey(planIndex), 0)) }
    LaunchedEffect(enabled, hour, minute) {
        if (enabled) ReadingPlanReminderScheduler.schedule(context, planIndex, hour, minute)
    }
    fun enableExactAlarm() {
        if (Build.VERSION.SDK_INT >= 31 && !context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}")))
        } else ReadingPlanReminderScheduler.schedule(context, planIndex, hour, minute)
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) enableExactAlarm() else {
            enabled = false
            prefs.edit().putBoolean(reminderEnabledKey(planIndex), false).apply()
        }
    }
    DisposableEffect(lifecycleOwner, enabled, planIndex) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && enabled) ReadingPlanReminderScheduler.schedule(context, planIndex, hour, minute)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    Text("Daily reminder", style = MaterialTheme.typography.subtitle1)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = enabled, onCheckedChange = { turnOn ->
            enabled = turnOn
            prefs.edit().putBoolean(reminderEnabledKey(planIndex), turnOn).apply()
            if (turnOn) {
                if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else enableExactAlarm()
            } else ReadingPlanReminderScheduler.cancel(context, planIndex)
            onChanged()
        })
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = {
            TimePickerDialog(context, { _, h, m ->
                hour = h; minute = m
                prefs.edit().putInt(reminderHourKey(planIndex), h).putInt(reminderMinuteKey(planIndex), m).apply()
                if (enabled) ReadingPlanReminderScheduler.schedule(context, planIndex, h, m)
            }, hour, minute, true).show()
        }) { Text(String.format("%02d:%02d", hour, minute)) }
    }
}

private fun progressFor(prefs: SharedPreferences, planIndex: Int, revision: Int): Int {
    revision.hashCode() // makes Compose observe the revision at this call site
    return prefs.getInt(progressKey(planIndex), -1)
}

private fun setProgress(prefs: SharedPreferences, planIndex: Int, day: Int) {
    prefs.edit().putInt(progressKey(planIndex), day).apply()
}

// This is also the key used by v7, so upgrades retain existing plan progress.
internal fun progressKey(planIndex: Int) = "plan-$planIndex-day"
internal fun reminderEnabledKey(planIndex: Int) = "plan-$planIndex-reminder-enabled"
internal fun reminderHourKey(planIndex: Int) = "plan-$planIndex-reminder-hour"
internal fun reminderMinuteKey(planIndex: Int) = "plan-$planIndex-reminder-minute"
