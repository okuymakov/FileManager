package com.example.vkinternshipapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.Constants
import com.example.vkinternshipapp.core.launchOnLifecycle
import com.example.vkinternshipapp.core.showPopup
import com.example.vkinternshipapp.core.toCharSequence
import com.example.vkinternshipapp.models.FileModel
import com.example.vkinternshipapp.ui.adapter.FileAdapter
import java.io.File

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val fileAdapter by lazy { FileAdapter(::onClick, ::onMoreClick) }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        doOnPermissions {
            setupList()
        }
        init()
        onBackPressedDispatcher.addCallback {
            viewModel.onAction(MainAction.MoveBack)
        }
    }

    private fun init() {
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.state.collect(::onState)
        }
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect(::onEvent)
        }
    }

    private fun onState(state: MainState) {
        val filesList = findViewById<RecyclerView>(R.id.files_List)
        val emptyView = findViewById<FrameLayout>(R.id.empty_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (state.files.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            filesList.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            filesList.visibility = View.VISIBLE
            fileAdapter.submitData(state.files)
        }
        toolbar.title = state.directories.last().toCharSequence(this)
        if (state.isRoot) {
            toolbar.navigationIcon = null
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        }
    }

    private fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.CloseApp -> finish()
        }
    }

    private fun setupList() {
        findViewById<RecyclerView>(R.id.files_List).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            viewModel.onAction(MainAction.MoveBack)
        }
    }

    private fun onClick(file: FileModel) {
        if (file.isDirectory) {
            viewModel.onAction(MainAction.MoveToDirectory(file.path))
        } else {
            openFile(file)
        }
    }

    private fun onMoreClick(file: FileModel, view: View) {
        view.showPopup(R.menu.file_popup) {
            when (it.itemId) {
                R.id.open_file -> {
                    openFile(file)
                    true
                }
                R.id.share_file -> {
                    shareFile(file)
                    true
                }
                else -> false
            }
        }
    }

    private fun openFile(file: FileModel) {
        val uri = FileProvider.getUriForFile(
            applicationContext,
            Constants.PROVIDER_AUTHORITY,
            File(file.path)
        )
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.type) ?: "text/plain"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, type)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    private fun shareFile(file: FileModel) {
        val uri = FileProvider.getUriForFile(
            applicationContext,
            Constants.PROVIDER_AUTHORITY,
            File(file.path)
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            setDataAndType(uri, type)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.file_name_with_ext, file.name, file.type)
            )
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_title_share_file)))
    }

    private fun doOnPermissions(block: () -> Unit) {
        if (checkPermissions()) {
            block()
        } else {
            requestPermissions {
                block()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions(onSuccess: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse(Constants.SETTINGS_URI)
            )
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (Environment.isExternalStorageManager()) {
                    onSuccess()
                }
            }.launch(intent)
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    onSuccess()
                }
            }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}