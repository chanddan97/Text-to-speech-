package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGoldGlow
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.StudioTab

@Composable
fun CineVoiceBottomBar(
    selectedTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_navigation_bar"),
        containerColor = ObsidianSurface,
        windowInsets = WindowInsets.navigationBars
    ) {
        val tabs = listOf(
            Triple(StudioTab.STUDIO, "Studio", Icons.Default.Mic),
            Triple(StudioTab.AUDIO_FX, "Audio FX", Icons.Default.Tune),
            Triple(StudioTab.HISTORY, "History", Icons.Default.History),
            Triple(StudioTab.DIAGNOSTIC, "Diagnostic", Icons.Default.Analytics)
        )

        tabs.forEach { (tab, label, icon) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CinematicGold,
                    selectedTextColor = CinematicGoldGlow,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = ObsidianBorder
                ),
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}
