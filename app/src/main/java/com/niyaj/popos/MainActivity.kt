package com.niyaj.popos

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.niyaj.common.utils.Constants.NETWORK_PERMISSION_REQUEST_CODE
import com.niyaj.common.utils.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.niyaj.common.utils.Constants.UPDATE_MANAGER_REQUEST_CODE
import com.niyaj.designsystem.theme.PoposTheme
import com.niyaj.popos.ui.PoposApp
import com.niyaj.ui.util.hasNetworkPermission
import com.niyaj.ui.util.hasNotificationPermission
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity
 * @author Sk Niyaj Ali
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateOptions = AppUpdateOptions
        .newBuilder(AppUpdateType.IMMEDIATE)
        .setAllowAssetPackDeletion(false)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val mainViewModel by viewModels<MainViewModel>()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        val hasNotificationPermission = this.hasNotificationPermission()
        val hasNetworkPermission = this.hasNetworkPermission()

        if (!hasNotificationPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        if (!hasNetworkPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                ),
                NETWORK_PERMISSION_REQUEST_CODE
            )
        }


        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            run {
                if (result.resultCode != RESULT_OK) {
                    Toast.makeText(
                        this,
                        "Something Went Wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setContent {
            PoposTheme {
                val status = mainViewModel.networkStatus.collectAsStateWithLifecycle().value

                if (status) {
                    checkForAppUpdates()
                }

                PoposApp(mainViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        updateOptions,
                        UPDATE_MANAGER_REQUEST_CODE
                    )
                }
            }
    }

    private fun checkForAppUpdates() {
        if (!BuildConfig.DEBUG) {
            appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { info ->
                    val isUpdateAvailable =
                        info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                    val isUpdateAllowed = when (updateOptions.appUpdateType()) {
                        AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                        else -> false
                    }

                    if (isUpdateAvailable && isUpdateAllowed) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            this,
                            updateOptions,
                            UPDATE_MANAGER_REQUEST_CODE
                        )
                    }

                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Unable to update app!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}