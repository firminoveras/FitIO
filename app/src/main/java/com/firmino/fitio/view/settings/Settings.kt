package com.firmino.fitio.view.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firmino.fitio.R
import com.firmino.fitio.view.extensions.capitalizeAndFormat
import com.firmino.fitio.view.theme.ui.Theme

enum class SettingsStringKey {
    LAST_FILTER_NAME, LAST_FILTER_MUSCLE, LAST_FILTER_DIFFICULTY, LAST_FILTER_TYPE,
}

enum class SettingsIntKey {
    THEME, LAST_WEEK_DAY, DEFAULT_SETS, DEFAULT_REPS
}

enum class SettingsBooleanKey {
    IS_TRAINING, IS_INSTRUCTIONS_VISIBLE_DEFAULT
}

enum class SettingsLongKey {
    STARTED_TIME_TRAINING,
}

fun save(key: SettingsStringKey, value: String, context: Context) {
    val prefs = context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).edit()
    prefs.putString(key.name, value)
    prefs.apply()
}

fun save(key: SettingsIntKey, value: Int, context: Context) {
    val prefs = context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).edit()
    prefs.putInt(key.name, value)
    prefs.apply()
}

fun save(key: SettingsBooleanKey, value: Boolean, context: Context) {
    val prefs = context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).edit()
    prefs.putBoolean(key.name, value)
    prefs.apply()
}

fun save(key: SettingsLongKey, value: Long, context: Context) {
    val prefs = context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).edit()
    prefs.putLong(key.name, value)
    prefs.apply()
}

fun loadString(key: SettingsStringKey, defValue: String, context: Context) =
    context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).getString(key.name, defValue) ?: ""

fun loadInt(key: SettingsIntKey, defValue: Int, context: Context) =
    context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).getInt(key.name, defValue)

fun loadBoolean(key: SettingsBooleanKey, defValue: Boolean, context: Context) =
    context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).getBoolean(key.name, defValue)

fun loadLong(key: SettingsLongKey, defValue: Long, context: Context) =
    context.getSharedPreferences("com.firmino.fitio", Context.MODE_PRIVATE).getLong(key.name, defValue)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSettings(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var defaultSets by remember { mutableIntStateOf(loadInt(SettingsIntKey.DEFAULT_SETS, 3, context)) }
    var defaultReps by remember { mutableIntStateOf(loadInt(SettingsIntKey.DEFAULT_REPS, 10, context)) }

    AlertDialog(onDismissRequest = { onDismiss() }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Icon(
                    Icons.Rounded.Settings, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(text = "Default sets number", modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (defaultSets > 1) defaultSets-- },
                            modifier = Modifier.weight(1f)
                        ) { Text(text = "-") }
                        Text(text = "$defaultSets", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        IconButton(
                            onClick = { if (defaultSets < 100) defaultSets++ },
                            modifier = Modifier.weight(1f)
                        ) { Text(text = "+") }
                    }
                }
                Divider()
                Row(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(text = "Default reps number", modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (defaultReps > 1) defaultReps-- },
                            modifier = Modifier.weight(1f)
                        ) { Text(text = "-") }
                        Text(text = "$defaultReps", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        IconButton(
                            onClick = { if (defaultReps < 100) defaultReps++ },
                            modifier = Modifier.weight(1f)
                        ) { Text(text = "+") }
                    }
                }
                Button(
                    onClick = {
                        save(SettingsIntKey.DEFAULT_SETS, defaultSets, context)
                        save(SettingsIntKey.DEFAULT_REPS, defaultReps, context)
                        onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp)
                ) {
                    Text(text = "Apply")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogThemes(onThemeSelected: (theme: Theme) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_themes),
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Themes",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Theme.values().forEach {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it.name.capitalizeAndFormat(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                        TextButton(
                            onClick = { onThemeSelected(it) },
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(text = "Apply")
                        }
                    }
                }
            }
        }
    }
}
