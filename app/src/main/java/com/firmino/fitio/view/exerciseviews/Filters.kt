package com.firmino.fitio.view.exerciseviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.firmino.fitio.R
import com.firmino.fitio.view.extensions.allDifficultyList
import com.firmino.fitio.view.extensions.allMusclesList
import com.firmino.fitio.view.extensions.allTypesList
import com.firmino.fitio.view.extensions.capitalizeAndFormat

data class Filter(
    var name: String = "",
    var muscle: String = "",
    var difficulty: String = "",
    var type: String = "",
) {

    fun clearAt(sentence: String): Filter {
        if (name == sentence) name = ""
        if (muscle == sentence) muscle = ""
        if (difficulty == sentence) difficulty = ""
        if (type == sentence) type = ""
        return this
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filters(
    modifier: Modifier = Modifier, filter: Filter, onFilterChange: (filter: Filter) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var filters by remember { mutableStateOf(filter) }

    var filterMuscleIndex by remember { mutableIntStateOf(allMusclesList.indexOf(filter.muscle)) }
    var filterDifficultyIndex by remember { mutableIntStateOf(allDifficultyList.indexOf(filter.difficulty)) }
    var filterTypeIndex by remember { mutableIntStateOf(allTypesList.indexOf(filter.type)) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (expanded) {
        ModalBottomSheet(onDismissRequest = { expanded = false }, sheetState = sheetState) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                SearchBar(query = filters.name,
                    onQueryChange = { filters = filters.copy(name = it) },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {}
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                FilterButtons(list = allMusclesList,
                    title = "Muscle",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_muscle),
                    index = filterMuscleIndex,
                    onIndexChange = { filterMuscleIndex = it }) { filters = filters.copy(muscle = it) }
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                FilterButtons(list = allDifficultyList,
                    title = "Difficulty",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_difficulty),
                    index = filterDifficultyIndex,
                    onIndexChange = { filterDifficultyIndex = it }) { filters = filters.copy(difficulty = it) }
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                FilterButtons(list = allTypesList,
                    title = "Type",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_type),
                    index = filterTypeIndex,
                    onIndexChange = { filterTypeIndex = it }) { filters = filters.copy(type = it) }
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = {
                        filters = Filter()
                        filterTypeIndex = 0
                        filterMuscleIndex = 0
                        filterDifficultyIndex = 0
                    }) {
                        Icon(Icons.Rounded.Clear, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Clear Filter")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(modifier = Modifier.weight(1f), onClick = {
                        expanded = false
                        onFilterChange(filters)
                    }) {
                        Icon(Icons.Rounded.Check, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Apply")
                    }
                }
            }
        }
    }

    FloatingActionButton(onClick = { expanded = true }, modifier = modifier.padding(16.dp)) {
        Icon(Icons.Rounded.Search, contentDescription = "Search")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FavoritesHeader(
    title: String, icon: ImageVector? = null, filter: Filter, total: Int, onFilterUpdate: (filter: Filter) -> Unit
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = title, style = MaterialTheme.typography.displaySmall)
            Divider(modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    Pair(painterResource(id = R.drawable.ic_name), filter.name),
                    Pair(painterResource(id = R.drawable.ic_muscle), filter.muscle),
                    Pair(painterResource(id = R.drawable.ic_difficulty), filter.difficulty),
                    Pair(painterResource(id = R.drawable.ic_type), filter.type),
                ).forEach {
                    if (it.second.isNotEmpty()) {
                        AssistChip(onClick = {
                            val newFilter = filter.copy().clearAt(it.second)
                            onFilterUpdate(newFilter)
                        }, label = { Text(text = it.second.capitalizeAndFormat()) }, leadingIcon = {
                            Icon(
                                painter = it.first,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }, trailingIcon = { Icon(Icons.Rounded.Clear, contentDescription = null) })
                    }
                }
            }
            Text(
                text = "$total exercise${if (total > 1) "s" else ""} found",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterButtons(
    list: List<String>,
    title: String = "",
    icon: ImageVector? = null,
    index: Int = 0,
    onIndexChange: (Int) -> Unit = {},
    onIndexSelected: (String) -> Unit
) {
    var selectedIndex by remember(index) { mutableIntStateOf(index) }
    var extended by remember { mutableStateOf(false) }
    val selectedText = remember(selectedIndex) {
        if (selectedIndex == 0) {
            "Any"
        } else {
            list[selectedIndex].capitalizeAndFormat()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                if (icon != null) Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
            }
            if (extended) {
                Icon(
                    Icons.Rounded.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.clickable { extended = false })
            } else {
                AssistChip(onClick = { extended = true },
                    trailingIcon = { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null) },
                    label = { Text(text = selectedText) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(visible = extended) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                list.forEachIndexed { index, text ->
                    val s = text.ifEmpty { "Any" }
                    FilterChip(
                        selected = index == selectedIndex,
                        label = { Text(text = s.capitalizeAndFormat().split(" ")[0]) },
                        leadingIcon = {
                            if (index == selectedIndex) Icon(
                                Icons.Rounded.Check, contentDescription = null
                            )
                        },
                        onClick = {
                            selectedIndex = index
                            onIndexSelected(if (s == "Any") "" else s)
                            onIndexChange(selectedIndex)
                        },
                    )
                }
            }
        }
    }

}