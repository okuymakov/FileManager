package com.example.vkinternshipapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.*
import com.example.vkinternshipapp.domain.filemanager.SortType
import com.example.vkinternshipapp.domain.models.FileModel
import com.example.vkinternshipapp.ui.adapter.DirSepDecorator
import com.example.vkinternshipapp.ui.adapter.DirectoryAdapter
import com.example.vkinternshipapp.ui.adapter.FileAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val fileAdapter by lazy { FileAdapter(::onFileClick, ::onMoreClick) }
    private val directoryAdapter by lazy { DirectoryAdapter(::onDirectoryClick) }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        val permissionManager = PermissionManager {
            viewModel.onAction(MainAction.GrantPermissions)
            viewModel.onAction(MainAction.FetchFiles)
        }
        if (permissionManager.checkPermissions()) {
            viewModel.onAction(MainAction.GrantPermissions)
            viewModel.onAction(MainAction.FetchFiles)
        } else {
            findViewById<Button>(R.id.request_permissions).setOnClickListener {
                permissionManager.requestPermissions()
            }
        }
        onBackPressedDispatcher.addCallback {
            viewModel.onAction(MainAction.MoveBack())
        }
    }

    private fun init() {
        setupToolbar()
        setupList()
        findViewById<Button>(R.id.try_again).setOnClickListener {
            viewModel.onAction(MainAction.FetchFiles)
        }
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.state.collect(::onState)
        }
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect(::onEvent)
        }
    }

    private fun onState(state: MainState) {
        val filesList = findViewById<RecyclerView>(R.id.files_list)
        val emptyView = findViewById<View>(R.id.empty_view)
        val loadingView = findViewById<View>(R.id.loading_view)
        val errorView = findViewById<View>(R.id.error_view)
        val permissionsNotGrantedView = findViewById<View>(R.id.permissions_not_granted_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        directoryAdapter.submitData(state.paths)
        when {
            state.isLoading -> {
                loadingView.show()
                emptyView.hide()
                filesList.hide()
                errorView.hide()
                permissionsNotGrantedView.hide()
            }
            state.permissionsNotGranted -> {
                loadingView.hide()
                emptyView.hide()
                filesList.hide()
                errorView.hide()
                permissionsNotGrantedView.show()
            }
            state.isError -> {
                loadingView.hide()
                emptyView.hide()
                filesList.hide()
                errorView.show()
                permissionsNotGrantedView.hide()
            }
            state.files.isEmpty() -> {
                loadingView.hide()
                emptyView.show()
                filesList.hide()
                errorView.hide()
                permissionsNotGrantedView.hide()
            }
            else -> {
                loadingView.hide()
                emptyView.hide()
                filesList.show()
                errorView.hide()
                permissionsNotGrantedView.hide()
                fileAdapter.submitData(state.files)
            }
        }
        state.paths.firstOrNull()?.directoryName(this)?.let {
            toolbar.title = it
        }
        if (state.isRoot) {
            toolbar.navigationIcon = null
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        }
        val iconRes =
            if (state.isDescending) R.drawable.ic_arrow_down_24 else R.drawable.ic_arrow_up_24
        toolbar.menu.findItem(R.id.change_sort_direction).setIcon(iconRes)
    }

    private fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.CloseApp -> finish()
        }
    }

    private fun setupList() {
        findViewById<RecyclerView>(R.id.files_list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
            setHasFixedSize(true)
        }
        findViewById<RecyclerView>(R.id.directories_list).apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, true)
            addItemDecoration(DirSepDecorator(context, R.drawable.ic_arrow_right_24))
            adapter = directoryAdapter
        }
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                viewModel.onAction(MainAction.MoveBack())
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.change_sort_type -> {
                        showSortPopup(menuItem.itemId)
                        true
                    }
                    R.id.change_sort_direction -> {
                        viewModel.onAction(MainAction.ChangeSortDirection)
                        true
                    }
                    R.id.updated_files -> {
                        viewModel.onAction(MainAction.FetchUpdated)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showSortPopup(@IdRes id: Int) {
        findViewById<View>(id).showPopup(R.menu.sort_popup) {
            val type = when (it.itemId) {
                R.id.by_name -> {
                    SortType.BY_NAME
                }
                R.id.by_date -> {
                    SortType.BY_DATE
                }
                R.id.by_type -> {
                    SortType.BY_TYPE
                }
                R.id.by_size -> {
                    SortType.BY_SIZE
                }
                else -> null
            }
            if (type != null) {
                it.isChecked = true
                viewModel.onAction(MainAction.SortBy(type))
                false
            } else false
        }.menu.findCheckedItem().apply { isChecked = true }
    }

    private fun Menu.findCheckedItem(): MenuItem {
        val id = when (viewModel.state.value.sortType) {
            SortType.BY_NAME -> R.id.by_name
            SortType.BY_SIZE -> R.id.by_size
            SortType.BY_DATE -> R.id.by_date
            SortType.BY_TYPE -> R.id.by_type
        }
        return findItem(id)
    }

    private fun onDirectoryClick(dirPath: String) {
        viewModel.onAction(MainAction.MoveBack(dirPath))
    }

    private fun onFileClick(file: FileModel) {
        if (file.isDirectory) {
            viewModel.onAction(MainAction.MoveToDirectory(file.path))
        } else {
            openFile(file)
        }
    }

    private fun onMoreClick(file: FileModel, view: View) {
        if (!file.isDirectory) {
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
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.type) ?: "text/plain"
        val intent = Intent(Intent.ACTION_SEND).apply {
            this.type = type
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.file_name_with_ext, file.name, file.type)
            )
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_title_share_file)))
    }

    private inner class PermissionManager(onSuccess: () -> Unit) {
        private var launcherForApi30: ActivityResultLauncher<Intent>? = null
        private var launcherForApi19: ActivityResultLauncher<String>? = null

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                launcherForApi30 =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        if (Environment.isExternalStorageManager()) {
                            onSuccess()
                        }
                    }
            } else {
                launcherForApi19 =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        if (isGranted) {
                            onSuccess()
                        }
                    }
            }
        }

        fun requestPermissions() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse(Constants.SETTINGS_URI)
                )
                launcherForApi30?.launch(intent)
            } else {
                launcherForApi19?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        fun checkPermissions(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}
