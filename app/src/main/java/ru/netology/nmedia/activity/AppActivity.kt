package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import android.Manifest
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding


class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()

        intent?.let {
            if (it.action == Intent.ACTION_SEND) {
                val text = it.getStringExtra(Intent.EXTRA_TEXT)
                if (text.isNullOrBlank()) {
                    Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok) {
                            finish()
                        }.show()
                    return@let
                }
                findNavController(R.id.nav_graph).navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
            }
        }
        //checkGoogleApiAvailability()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermissions(arrayOf(permission), 1)
    }
}

//private fun checkGoogleApiAvailability() {
//    with(GoogleApiAvailability.getInstance()) {
//        val code = isGooglePlayServicesAvailable(this@AppActivity)
//        if (code == ConnectionResult.SUCCESS) {
//            return@with
//        }
//        if (isUserResolvableError(code)) {
//            getErrorDialog(this@AppActivity, code, 9000)?.show()
//            return
//        }
//        Toast.makeText(this@AppActivity, "Google API Unavailable", Toast.LENGTH_SHORT).show()
//    }
//}
