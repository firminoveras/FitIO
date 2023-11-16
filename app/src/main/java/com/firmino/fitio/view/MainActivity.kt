package com.firmino.fitio.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firmino.fitio.FitIoApplication
import com.firmino.fitio.R
import com.firmino.fitio.view.contents.ContentDays
import com.firmino.fitio.view.contents.ContentExercises
import com.firmino.fitio.view.contents.ContentFavorites
import com.firmino.fitio.view.extensions.getAppVersion
import com.firmino.fitio.view.settings.DialogSettings
import com.firmino.fitio.view.settings.DialogThemes
import com.firmino.fitio.view.settings.SettingsIntKey
import com.firmino.fitio.view.settings.SettingsStringKey
import com.firmino.fitio.view.settings.loadInt
import com.firmino.fitio.view.settings.loadString
import com.firmino.fitio.view.settings.save
import com.firmino.fitio.view.theme.ui.FitIOTheme
import com.firmino.fitio.view.theme.ui.Theme
import com.firmino.fitio.viewmodel.ExerciseLocalViewModel
import com.firmino.fitio.viewmodel.ExerciseRemoteViewModel
import com.firmino.fitio.viewmodel.factory.ExerciseViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var remoteViewModel: ExerciseRemoteViewModel
    private val localViewModel: ExerciseLocalViewModel by viewModels { ExerciseViewModelFactory((application as FitIoApplication).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remoteViewModel = ViewModelProvider(this)[ExerciseRemoteViewModel::class.java]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView, View.ALPHA, 1f, 0f
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 500L
                slideUp.doOnEnd { splashScreenView.remove() }
                slideUp.start()
            }
        }

        remoteViewModel.getByFilters(
            name = loadString(SettingsStringKey.LAST_FILTER_NAME, "", this),
            muscle = loadString(SettingsStringKey.LAST_FILTER_MUSCLE, "", this),
            difficulty = loadString(SettingsStringKey.LAST_FILTER_DIFFICULTY, "", this),
            type = loadString(SettingsStringKey.LAST_FILTER_TYPE, "", this),
        )

        setContent {
            var theme by remember { mutableStateOf(Theme.values()[loadInt(SettingsIntKey.THEME, 0, this)]) }
            FitIOTheme(theme = theme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onThemeChange = {
                        theme = it
                        save(SettingsIntKey.THEME, Theme.values().indexOf(it), this)
                    })
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(onThemeChange: (theme: Theme) -> Unit = {}) {
        val navController = rememberNavController()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        var dialogSettingsVisible by remember { mutableStateOf(false) }
        var dialogThemesVisible by remember { mutableStateOf(false) }
        var dialogInfoVisible by remember { mutableStateOf(false) }

        if (dialogSettingsVisible) DialogSettings { dialogSettingsVisible = false }
        if (dialogInfoVisible) DialogInfo { dialogInfoVisible = false }
        if (dialogThemesVisible) DialogThemes(onThemeSelected = onThemeChange) { dialogThemesVisible = false }

        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            TopBar(scrollBehavior, onSettingsClick = { dialogSettingsVisible = true }, onThemeChangeClick = {
                dialogThemesVisible = true
            }, onInfoClick = {
                dialogInfoVisible = true
            })
        }, bottomBar = { NavigationBottomBar(onNavigationSelect = { navController.navigate(it) }) }) {
            NavHost(
                navController = navController, startDestination = "exercises", modifier = Modifier.padding(it)
            ) {
                composable("Exercises") {
                    ContentExercises(
                        localViewModel = localViewModel,
                        remoteViewModel = remoteViewModel,
                        lifecycleOwner = this@MainActivity
                    )
                }
                composable("Favorites") { ContentFavorites(localViewModel = localViewModel) }
                composable("Today") { ContentDays(localViewModel = localViewModel) }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialogInfo(onDismiss: () -> Unit) {
        val context = LocalContext.current
        AlertDialog(onDismissRequest = { onDismiss() }) {
            Card {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Rounded.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "App Info",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(text = "Attributions", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Icons by svgrepo.com", style = MaterialTheme.typography.labelMedium)
                    Text(text = "Backgrounds by storyset.com", style = MaterialTheme.typography.labelMedium)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Version", style = MaterialTheme.typography.titleMedium)
                    Text(text = getAppVersion(context)?.versionName ?: "-", style = MaterialTheme.typography.labelMedium)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/firminoveras"))
                        )
                    }) {
                        Icon(ImageVector.vectorResource(R.drawable.ic_github), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Developer GitHub")
                    }

                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(
        scrollBehavior: TopAppBarScrollBehavior,
        onSettingsClick: () -> Unit,
        onThemeChangeClick: () -> Unit,
        onInfoClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_header),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.matchParentSize()
            )
            LargeTopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Column {
                        Text(text = "FitIO", overflow = TextOverflow.Ellipsis)
                        Text(text = "You in control", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                containerColor = Color.Transparent,
            ), actions = {
                IconButton(onClick = { onInfoClick() }) {
                    Icon(Icons.Rounded.Info, contentDescription = null)
                }
                IconButton(onClick = { onThemeChangeClick() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_themes), contentDescription = null)
                }
                IconButton(onClick = { onSettingsClick() }) {
                    Icon(Icons.Rounded.Settings, contentDescription = null)
                }
            })
        }
    }

    @Composable
    private fun NavigationBottomBar(onNavigationSelect: (title: String) -> Unit) {
        var selectedItem by remember { mutableIntStateOf(0) }
        NavigationBar {
            listOf(
                Pair("Exercises", Icons.Rounded.Menu),
                Pair("Favorites", Icons.Rounded.Favorite),
                Pair("Today", Icons.Rounded.DateRange)
            ).forEachIndexed { index, item ->
                NavigationBarItem(selected = index == selectedItem, onClick = {
                    selectedItem = index
                    onNavigationSelect(item.first)
                }, label = { Text(text = item.first) }, icon = { Icon(item.second, null) })
            }
        }
    }
}