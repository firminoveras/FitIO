package com.firmino.fitio.view.contents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firmino.fitio.R
import com.firmino.fitio.model.entity.ExerciseLocal
import com.firmino.fitio.model.mapper.toExercise
import com.firmino.fitio.model.mapper.updateByWeekdayList
import com.firmino.fitio.view.exerciseviews.Exercise
import com.firmino.fitio.view.exerciseviews.FavoritesHeader
import com.firmino.fitio.view.exerciseviews.Filter
import com.firmino.fitio.view.exerciseviews.Filters
import com.firmino.fitio.viewmodel.ExerciseLocalViewModel

@Composable
fun ContentFavorites(localViewModel: ExerciseLocalViewModel) {
    var deleteDialog by remember { mutableStateOf("") }
    val exercises by localViewModel.allExercises.observeAsState(listOf())

    var filters by remember { mutableStateOf(Filter()) }

    val filteredExercises = remember(exercises, filters) {
        exercises.filter {
            filters.muscle in it.muscle && filters.name in it.name && filters.difficulty in it.difficulty && filters.type in it.type
        }
    }

    if (deleteDialog.isNotEmpty()) {
        DeleteDialog(onDismissRequest = { deleteDialog = "" }, onDeleteClick = {
            localViewModel.delete(deleteDialog)
            deleteDialog = ""
        })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (filteredExercises.isEmpty()) {
            EmptyText()
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    FavoritesHeader(title = "Favorites",
                        icon = Icons.Rounded.Favorite,
                        filter = filters,
                        total = filteredExercises.size,
                        onFilterUpdate = { filters = it.copy() })
                }
                items(items = filteredExercises, key = { it.name }) { exercise ->
                    ExerciseLocal(exercise = exercise, onDeleteClick = { deleteDialog = exercise.name }) {
                        localViewModel.update(it)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        Filters(modifier = Modifier.align(Alignment.BottomEnd), filter = filters) { filters = it.copy() }
    }
}

@Composable
fun EmptyText() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_empty_search),
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            tint = MaterialTheme.colorScheme.secondaryContainer
        )
        Text(
            text = "No favorites found",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(onDismissRequest: () -> Unit = {}, onDeleteClick: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp))

                Text(
                    text = "Delete favorite",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "You are sure you want to delete this entry from your favorites?",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onDismissRequest() }) { Text(text = "Dismiss") }
                    TextButton(onClick = { onDeleteClick() }) { Text(text = "Confirm") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLocal(
    exercise: ExerciseLocal, onDeleteClick: () -> Unit = {}, onUpdate: (exercise: ExerciseLocal) -> Unit = {}
) {
    val exerciseDays = remember {
        mutableStateListOf(
            exercise.mon, exercise.tue, exercise.wed, exercise.thu, exercise.fri, exercise.sat, exercise.sun
        )
    }

    Exercise(
        exercise = exercise.toExercise(),
        isFavorite = true,
        onFavoriteClick = { onDeleteClick() },
        iconOn = Icons.Rounded.Delete
    ) {
        Column {
            Divider(modifier = Modifier.padding(bottom = 12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEachIndexed { index, weekDay ->
                    FilterChip(selected = exerciseDays[index],
                        onClick = {
                            exerciseDays[index] = !exerciseDays[index]
                            exercise.updateByWeekdayList(exerciseDays)
                            onUpdate(exercise)
                        },
                        label = { Text(text = weekDay, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (true in exerciseDays) {
                SetsPicker(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                    defaultReps = exercise.reps,
                    defaultSets = exercise.sets,
                    onValuesUpdate = { sets, reps ->
                        exercise.sets = sets
                        exercise.reps = reps
                        onUpdate(exercise)
                    })
            }
        }
    }
}

@Composable
fun SetsPicker(
    modifier: Modifier = Modifier,
    defaultSets: Int = 3,
    defaultReps: Int = 10,
    onValuesUpdate: (sets: Int, reps: Int) -> Unit
) {
    var sets by remember { mutableIntStateOf(defaultSets) }
    var reps by remember { mutableIntStateOf(defaultReps) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            Modifier
                .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.extraLarge)
                .height(40.dp)
                .align(Alignment.Center), verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = expanded) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { sets-- }) { Text(text = "-") }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$sets", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall)
                        Text(text = "Sets", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { sets++ }) { Text(text = "+") }
                    IconButton(onClick = { reps-- }) { Text(text = "-") }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$reps", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall)
                        Text(text = "Reps", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { reps++ }) { Text(text = "+") }
                }
            }
            Button(onClick = {
                if (expanded) onValuesUpdate(sets, reps)
                expanded = !expanded
            }, modifier = Modifier.widthIn(min = 120.dp)) {
                Text(text = if (expanded) "Apply" else "Change")
            }
            AnimatedVisibility(visible = !expanded) {
                Text(
                    text = "${sets}x$reps",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}
