package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.components.CineVoiceBottomBar
import com.example.ui.components.CineVoiceTopAppBar
import com.example.ui.components.SecretVaultDialog
import com.example.ui.screens.AudioFxScreen
import com.example.ui.screens.DiagnosticScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.CineVoiceTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CineVoiceTheme {
                CineVoiceApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CineVoiceApp(viewModel: StudioViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showSecretVault by viewModel.showSecretVaultDialog.collectAsState()
    val tapHint by viewModel.vaultTapHint.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val diagnosticState by viewModel.diagnosticState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }

    // Back handler: if not on Studio tab, pressing back returns to Studio tab
    if (currentTab != StudioTab.STUDIO) {
        BackHandler {
            viewModel.selectTab(StudioTab.STUDIO)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        containerColor = ObsidianBackground,
        topBar = {
            CineVoiceTopAppBar(
                isKeyConfigured = viewModel.vaultManager.isKeyConfigured(),
                tapHint = tapHint,
                onHeaderTapped = { viewModel.onHeaderTapped() }
            )
        },
        bottomBar = {
            CineVoiceBottomBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                StudioTab.STUDIO -> StudioScreen(viewModel = viewModel)
                StudioTab.AUDIO_FX -> AudioFxScreen(viewModel = viewModel)
                StudioTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                StudioTab.DIAGNOSTIC -> DiagnosticScreen(viewModel = viewModel)
            }
        }
    }

    // Secret API Vault Dialog (triggered by 5 rapid header taps)
    if (showSecretVault) {
        SecretVaultDialog(
            currentApiKey = viewModel.vaultManager.getApiKey(),
            diagnosticState = diagnosticState,
            onSaveKey = { viewModel.saveVaultApiKey(it) },
            onClearKey = { viewModel.clearVaultApiKey() },
            onRunDiagnostic = { viewModel.runDiagnosticScan() },
            onDismiss = { viewModel.dismissSecretVault() }
        )
    }
}
