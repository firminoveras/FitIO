package com.firmino.fitio.view.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import com.firmino.fitio.R
import com.firmino.fitio.model.mapper.toExercise
import com.firmino.fitio.model.remote.ExerciseRemote
import com.firmino.fitio.view.exerciseviews.Exercise
import com.firmino.fitio.view.exerciseviews.FavoritesHeader
import com.firmino.fitio.view.exerciseviews.Filter
import com.firmino.fitio.view.exerciseviews.Filters
import com.firmino.fitio.view.settings.SettingsIntKey
import com.firmino.fitio.view.settings.SettingsStringKey
import com.firmino.fitio.view.settings.loadInt
import com.firmino.fitio.view.settings.loadString
import com.firmino.fitio.view.settings.save
import com.firmino.fitio.viewmodel.ExerciseLocalViewModel
import com.firmino.fitio.viewmodel.ExerciseRemoteViewModel
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState

private val favoriteNames = mutableListOf<String>()

@Composable
fun ContentExercises(
    localViewModel: ExerciseLocalViewModel, remoteViewModel: ExerciseRemoteViewModel, lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current

    var exercises by remember { mutableStateOf(listOf<ExerciseRemote>()) }
    var isEmpty by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var filters by remember {
        mutableStateOf(
            Filter(
                name = loadString(SettingsStringKey.LAST_FILTER_NAME, "", context),
                muscle = loadString(SettingsStringKey.LAST_FILTER_MUSCLE, "", context),
                difficulty = loadString(SettingsStringKey.LAST_FILTER_DIFFICULTY, "", context),
                type = loadString(SettingsStringKey.LAST_FILTER_TYPE, "", context),
            )
        )
    }

    remoteViewModel.state.observe(lifecycleOwner) { state ->
        isEmpty = false
        isLoading = false
        error = ""
        exercises = listOf()
        when (state) {
            is ExerciseRemoteState.Error -> error = state.message
            ExerciseRemoteState.Loading -> isLoading = true
            is ExerciseRemoteState.Success -> exercises = state.data
            ExerciseRemoteState.Empty -> isEmpty = true
        }
    }

    localViewModel.allExercises.observe(lifecycleOwner) {
        favoriteNames.clear()
        it.forEach { exercise -> favoriteNames.add(exercise.name) }
    }

    Box {
        Column(Modifier.fillMaxSize()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (isEmpty) EmptyContainer()
            if (error.isNotEmpty()) ErrorContainer(error)
            if (exercises.isNotEmpty()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        FavoritesHeader(title = "Exercises Repository",
                            icon = Icons.Rounded.Search,
                            filter = filters,
                            total = exercises.size,
                            onFilterUpdate = {
                                filters = it
                                remoteViewModel.getByFilters(it.name, it.muscle, it.difficulty, it.type)
                            })
                    }
                    items(exercises, key = { it.name }) { exercise ->
                        ExerciseLocal(exercise, localViewModel)
                    }
                    item {
                        ListFooter()
                    }
                }
            }
        }
        Filters(modifier = Modifier.align(Alignment.BottomEnd), filter = filters) {
            remoteViewModel.getByFilters(it.name, it.muscle, it.difficulty, it.type)
            filters = it.copy()
            save(SettingsStringKey.LAST_FILTER_NAME, it.name, context)
            save(SettingsStringKey.LAST_FILTER_MUSCLE, it.muscle, context)
            save(SettingsStringKey.LAST_FILTER_DIFFICULTY, it.difficulty, context)
            save(SettingsStringKey.LAST_FILTER_TYPE, it.type, context)
        }
    }
}

@Composable
private fun ExerciseLocal(exercise: ExerciseRemote, localViewModel: ExerciseLocalViewModel) {
    val context = LocalContext.current
    var favorite by remember { mutableStateOf(exercise.name in favoriteNames) }
    Exercise(exercise.toExercise(), isFavorite = favorite, onFavoriteClick = {
        favorite = !favorite
        if (favorite) localViewModel.insert(
            exercise.name,
            exercise.type,
            exercise.muscle,
            exercise.equipment,
            exercise.difficulty,
            exercise.instructions,
            sets = loadInt(SettingsIntKey.DEFAULT_SETS, 3, context),
            reps = loadInt(SettingsIntKey.DEFAULT_REPS, 10, context),
        )
        else localViewModel.delete(exercise.name)
    })
}

@Composable
private fun EmptyContainer() {
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
            text = "No exercise found",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Composable
private fun ErrorContainer(error: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            tint = MaterialTheme.colorScheme.secondaryContainer
        )
        Text(
            text = "Error:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Composable
private fun ListFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Divider(modifier = Modifier.padding(bottom = 16.dp, start = 60.dp, end = 60.dp))
        Text(
            text = "If you haven't found the exercise you're looking for, try again with more parameters in the filter. The maximum result number by request is 10.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 56.dp),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(72.dp))
    }
}