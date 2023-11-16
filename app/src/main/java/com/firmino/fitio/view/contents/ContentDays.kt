package com.firmino.fitio.view.contents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.firmino.fitio.model.entity.ExerciseLocal
import com.firmino.fitio.model.mapper.getWeekdayAsList
import com.firmino.fitio.model.mapper.toExercise
import com.firmino.fitio.view.exerciseviews.Exercise
import com.firmino.fitio.view.extensions.allRestImages
import com.firmino.fitio.view.extensions.capitalizeAndFormat
import com.firmino.fitio.view.extensions.dateMillisToText
import com.firmino.fitio.view.extensions.getImageByName
import com.firmino.fitio.view.extensions.ticksMillisToText
import com.firmino.fitio.view.settings.SettingsBooleanKey
import com.firmino.fitio.view.settings.SettingsIntKey
import com.firmino.fitio.view.settings.SettingsLongKey
import com.firmino.fitio.view.settings.loadBoolean
import com.firmino.fitio.view.settings.loadInt
import com.firmino.fitio.view.settings.loadLong
import com.firmino.fitio.view.settings.save
import com.firmino.fitio.viewmodel.ExerciseLocalViewModel
import java.util.Timer
import java.util.TimerTask

@Composable
fun ContentDays(localViewModel: ExerciseLocalViewModel) {
    val context = LocalContext.current
    val weekDay = loadInt(SettingsIntKey.LAST_WEEK_DAY, 0, context)

    var selectedDay by remember { mutableIntStateOf(weekDay) }
    val exercises by localViewModel.allExercises.observeAsState(listOf())
    var isTraining by remember { mutableStateOf(loadBoolean(SettingsBooleanKey.IS_TRAINING, false, context)) }
    val exercisesByMuscle = remember(exercises, selectedDay) {
        val filteredExercises = mutableMapOf<String, MutableList<ExerciseLocal>>()
        exercises.filter { it.getWeekdayAsList()[selectedDay] }.forEach {
            if (filteredExercises.containsKey(it.muscle)) {
                filteredExercises[it.muscle]?.add(it)
            } else {
                filteredExercises[it.muscle] = mutableListOf(it)
            }
        }
        filteredExercises.values.toList()
    }
    val extended = remember(exercisesByMuscle.size) {
        val extendedList = mutableListOf<Boolean>()
        repeat(exercisesByMuscle.size) { extendedList.add(false) }
        mutableStateListOf(*extendedList.toTypedArray())
    }

    Column {
        AnimatedVisibility(visible = !isTraining) {
            DayTab(selectedDay = selectedDay, onTabSelect = {
                selectedDay = it
                save(SettingsIntKey.LAST_WEEK_DAY, it, context)
            })
        }
        if (exercisesByMuscle.isNotEmpty()) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Title(selectedDay = selectedDay,
                        exercises = exercises,
                        isTraining = isTraining,
                        startButtonClick = {
                            isTraining = true
                            save(SettingsBooleanKey.IS_TRAINING, isTraining, context)
                            save(SettingsLongKey.STARTED_TIME_TRAINING, System.currentTimeMillis(), context)
                        },
                        stopButtonClick = {
                            isTraining = false
                            save(SettingsBooleanKey.IS_TRAINING, isTraining, context)
                            localViewModel.resetAllSets()
                        })
                }
                itemsIndexed(
                    items = exercisesByMuscle,
                    key = { _, muscleExercises -> muscleExercises.hashCode() }) { index, muscleExercises ->
                    MuscularTrain(exercises = muscleExercises,
                        extended = extended[index],
                        enabled = isTraining,
                        onExtendedUpdate = {
                            extended[index] = it
                        }) {
                        localViewModel.update(it)
                    }
                }
            }
        } else {
            EmptyContentDay(selectedDay)
            isTraining = false
            save(SettingsBooleanKey.IS_TRAINING, false, context)
        }
    }
}

@Composable
private fun EmptyContentDay(selectedDay: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .alpha(0.2f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = allRestImages[selectedDay]),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 80.dp),
            )
            Text(text = "Rest Day", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun MuscularTrain(
    exercises: List<ExerciseLocal>,
    enabled: Boolean,
    extended: Boolean,
    onExtendedUpdate: (extended: Boolean) -> Unit,
    onUpdate: (exercise: ExerciseLocal) -> Unit,
) {
    ElevatedCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            MuscularTrainTitle(
                exercises[0].muscle,
                extended = extended,
                enabled = enabled,
                onClick = { onExtendedUpdate(!extended) },
                exercises = exercises
            )
            AnimatedVisibility(visible = extended) {
                Column {
                    exercises.forEach { exercise ->
                        MuscularTrainContent(exercise = exercise, enabled = enabled, onUpdate = { onUpdate(it) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscularTrainContent(exercise: ExerciseLocal, enabled: Boolean, onUpdate: (exercise: ExerciseLocal) -> Unit) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var sets by remember { mutableIntStateOf(exercise.setsCount) }

    if (showDetailsDialog) {
        AlertDialog(onDismissRequest = { showDetailsDialog = false }) {
            Exercise(exercise = exercise.toExercise(),
                extended = true,
                extendable = false,
                iconOff = Icons.Rounded.Close,
                onFavoriteClick = { showDetailsDialog = false })
        }
    }

    Column(Modifier.clickable { showDetailsDialog = true }) {
        Divider()
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 132.dp)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "${exercise.sets}x${exercise.reps}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.alpha(0.3f),
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (enabled) {
                Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                    if (sets > 0) {
                        TextButton(onClick = {
                            sets = 0
                            onUpdate(exercise.copy(setsCount = 0))
                        }) {
                            Text(text = "Restart")
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(contentAlignment = Alignment.Center) {
                        FloatingActionButton(onClick = {
                            if (sets < exercise.sets) {
                                sets++
                                onUpdate(exercise.copy(setsCount = sets))
                            }
                        }, shape = MaterialTheme.shapes.extraLarge) {
                            if (sets < exercise.sets) Text(text = "$sets/${exercise.sets}")
                            else Icon(Icons.Rounded.Check, contentDescription = null)
                        }
                        CircularProgressIndicator(
                            modifier = Modifier.matchParentSize(),
                            strokeWidth = 6.dp,
                            progress = sets.toFloat() / exercise.sets
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MuscularTrainTitle(
    muscle: String, extended: Boolean, enabled: Boolean, onClick: () -> Unit, exercises: List<ExerciseLocal>
) {
    val progress by remember {
        derivedStateOf {
            if (enabled) {
                var totalSets = 0
                var completedSets = 0
                exercises.forEach {
                    totalSets += it.sets
                    completedSets += it.setsCount
                }
                completedSets.toFloat() / totalSets
            } else {
                0f
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.matchParentSize(),
            trackColor = if (extended) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = getImageByName(muscle)),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = muscle.capitalizeAndFormat(), style = MaterialTheme.typography.headlineSmall)
        }
        if (progress > 0) {
            Text(
                text = "${(progress * 100).toInt()} %",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun Title(
    selectedDay: Int,
    exercises: List<ExerciseLocal>,
    isTraining: Boolean,
    startButtonClick: () -> Unit,
    stopButtonClick: () -> Unit,
) {
    val context = LocalContext.current
    val startedAt by rememberUpdatedState(newValue = loadLong(SettingsLongKey.STARTED_TIME_TRAINING, 0, context))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dayName = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")[selectedDay]
    val exercisesFiltered = remember(exercises) { exercises.filter { it.getWeekdayAsList()[selectedDay] } }
    val progress = remember(exercisesFiltered) {
        var totalSets = 0
        var completedSets = 0
        exercisesFiltered.forEach {
            totalSets += it.sets
            completedSets += it.setsCount
        }
        completedSets.toFloat() / totalSets
    }

    var ticks by remember { mutableLongStateOf(0L) }
    LaunchedEffect(startedAt) {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (isTraining) ticks =
                    System.currentTimeMillis() - startedAt
            }
        }, 0, 1000)
    }

    if (showDeleteDialog) {
        StopTrainingDialog(onConfirm = {
            ticks = 0
            stopButtonClick()
            showDeleteDialog = false
        }, onDismissRequest = { showDeleteDialog = false })
    }

    Box {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp),
            ) {
                Text(text = dayName, style = MaterialTheme.typography.displayMedium)
                Divider(Modifier.padding(12.dp))
                Text(
                    text = "${exercisesFiltered.size} Exercise${if (exercisesFiltered.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyLarge
                )
                AnimatedVisibility(visible = !isTraining) {
                    Button(modifier = Modifier.padding(top = 16.dp), onClick = {
                        startButtonClick()
                    }) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Text(text = "Start")
                    }
                }
                AnimatedVisibility(visible = isTraining) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = ticks.ticksMillisToText(), style = MaterialTheme.typography.headlineLarge)
                        Text(
                            text = "started at ${startedAt.dateMillisToText()}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .alpha(0.5f)
                                .padding(bottom = 16.dp)
                        )
                        OutlinedButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Rounded.Close, contentDescription = null)
                            Text(text = "Stop")
                        }
                    }
                }
            }
            if (isTraining) {
                Box {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(14.dp)
                    )
                    Text(
                        text = "${(progress * 100).toInt()} %",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .align(Alignment.Center),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (progress < 0.45) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopTrainingDialog(onDismissRequest: () -> Unit = {}, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp))

                Text(
                    text = "Stop the current training",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "You are sure you want to end the current training? This will erase all of your current progress in the exercises of the day and reset the timer.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onDismissRequest() }) { Text(text = "Dismiss") }
                    TextButton(onClick = { onConfirm() }) { Text(text = "Confirm") }
                }
            }
        }
    }
}

@Composable
fun DayTab(selectedDay: Int, onTabSelect: (selectedDay: Int) -> Unit) {
    TabRow(selectedTabIndex = selectedDay) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEachIndexed { index, day ->
            Tab(selected = (index == selectedDay),
                onClick = { onTabSelect(index) },
                text = { Text(text = day, style = MaterialTheme.typography.labelSmall) })
        }
    }
}