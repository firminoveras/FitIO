package com.firmino.fitio.view.exerciseviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.firmino.fitio.model.mapper.Exercise
import com.firmino.fitio.view.extensions.capitalizeAndFormat
import com.firmino.fitio.view.extensions.getImageByName
import com.firmino.fitio.view.settings.SettingsBooleanKey
import com.firmino.fitio.view.settings.loadBoolean
import com.firmino.fitio.view.settings.save

@Composable
fun Exercise(
    exercise: Exercise,
    extended: Boolean = false,
    extendable: Boolean = true,
    iconOff: ImageVector? = null,
    iconOn: ImageVector? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(extended) }
    val instructionsVisible by remember {
        mutableStateOf(
            loadBoolean(
                SettingsBooleanKey.IS_INSTRUCTIONS_VISIBLE_DEFAULT, false, context
            )
        )
    }
    val difficulty = when (exercise.difficulty) {
        "beginner" -> "★☆☆"
        "intermediate" -> "★★☆"
        else -> "★★★"
    }
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(modifier = Modifier
                .clickable { if (extendable) expanded = !expanded }
                .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(visible = !expanded) {
                    Image(
                        painter = painterResource(id = getImageByName(exercise.muscle)),
                        modifier = Modifier.size(42.dp),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(5f)
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = exercise.type.capitalizeAndFormat(), style = MaterialTheme.typography.titleSmall)
                }
                Icon(if (isFavorite) iconOn ?: Icons.Rounded.Favorite else iconOff ?: Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                        .clickable { onFavoriteClick() }
                        .alpha(if (isFavorite) 1f else 0.2f))
            }

            Divider(modifier = Modifier.alpha(if (expanded) 1f else 0f))
            AnimatedVisibility(visible = expanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getImageByName(exercise.muscle)),
                            modifier = Modifier
                                .size(128.dp)
                                .align(Alignment.Center),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.align(Alignment.TopEnd),
                            text = "Difficulty\n$difficulty",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Equipment:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.width(6.dp))
                        SuggestionChip(onClick = {}, label = {
                            Text(text = exercise.equipment.capitalizeAndFormat())
                        })
                    }

                    if (exercise.instructions.isNotEmpty()) {
                        ExerciseInstructions(instructionsVisible, exercise)
                    }

                    Column(content = content)
                }
            }
        }
    }
}

@Composable
private fun ExerciseInstructions(expanded: Boolean, exercise: Exercise) {
    var extended by remember { mutableStateOf(expanded) }
    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                extended = !extended
                save(
                    SettingsBooleanKey.IS_INSTRUCTIONS_VISIBLE_DEFAULT, extended, context
                )
            }) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(
                    text = "Instructions",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    if (extended) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }

            AnimatedVisibility(visible = extended) {
                Column {
                    Divider()
                    Text(
                        text = exercise.instructions,
                        modifier = Modifier.padding(6.dp),
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}