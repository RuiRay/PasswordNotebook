package com.ionesmile.cipherbox.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.manager.util.ImportExportHelper
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.ui.fragment.CipherEditFragment
import com.ionesmile.cipherbox.ui.fragment.CipherShowFragment
import ru.bartwell.exfilepicker.ExFilePicker
import ru.bartwell.exfilepicker.ExFilePickerActivity
import ru.bartwell.exfilepicker.ExFilePickerParcelObject
import java.io.File


class DetailActivity : AppCompatActivity() {

    var mCipherTableId: Int = DEFAULT_VALUE

    val STATE_ADD = 1
    val STATE_SHOW = 2
    val STATE_SHOW_EDIT = 3
    var currentState = STATE_ADD

    lateinit var toolbar: Toolbar

    lateinit var currentFragment: Fragment
    var showFragment: CipherShowFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonManager.setTranslucentBar(window)
        setContentView(R.layout.activity_detail)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mCipherTableId = intent.getIntExtra(EXTRA_CIPHER_ID, mCipherTableId)

        if (mCipherTableId == DEFAULT_VALUE) {
            currentState = STATE_ADD
            currentFragment = CipherEditFragment.newInstance(mCipherTableId)
        } else {
            currentState = STATE_SHOW
            showFragment = CipherShowFragment.newInstance(mCipherTableId)
            currentFragment = showFragment!!
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.layout_cipher_replace_root, currentFragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var showMenuId: Int
        when (currentState) {
            STATE_ADD -> showMenuId = R.menu.menu_ciperbox_fragment_edit
            STATE_SHOW -> showMenuId = R.menu.menu_ciperbox_fragment_show
            STATE_SHOW_EDIT -> showMenuId = R.menu.menu_ciperbox_fragment_edit
            else -> showMenuId = R.menu.menu_ciperbox_fragment_edit
        }
        menuInflater.inflate(showMenuId, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit -> {
                currentState = STATE_SHOW_EDIT
                startFragment(CipherEditFragment.newInstance(mCipherTableId))
                invalidateOptionsMenu()
            }
            R.id.action_save -> {
                if (currentFragment is CipherEditFragment) {
                    (currentFragment as CipherEditFragment).saveCipherTable()
                }
                when (currentState) {
                    STATE_ADD -> finish()
                    STATE_SHOW_EDIT -> {
                        supportFragmentManager.popBackStack()
                        currentState = STATE_SHOW
                        showFragment?.refreshInfo()
                        invalidateOptionsMenu()
                    }
                }
            }
            R.id.action_import -> {
                showImportFileWindow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EX_FILE_PICKER_RESULT_CHOOSE_FILE) {
            if (data != null) {
                val parcelObject: ExFilePickerParcelObject = data.getParcelableExtra<Parcelable>(ExFilePickerParcelObject::class.java.canonicalName) as ExFilePickerParcelObject
                if (parcelObject.count > 0) {
                    // Here is object contains selected files names and path
                    Log.i(TAG, "onActivityResult() path = " + parcelObject.path + "   count = " + parcelObject.count + "   names = " + parcelObject.names)
                    val file = File(parcelObject.path, parcelObject.names[0])
                    if (file.isFile) {
                        ImportExportHelper.showImportPasswordInputDialog(this, { encryptKey ->
                            var result = ImportExportHelper.importCipherFile(baseContext, file.absolutePath, encryptKey)
                            if (result.size == 2){
                                showImportResultDialog(result[0], result[1])
                            }
                            !result.isEmpty()
                        })
                    }
                }
            }
        }
    }

    private fun showImportResultDialog(allList: List<CipherTable>, ignoreList: List<CipherTable>) {
        var builder = AlertDialog.Builder(this)
                .setTitle("提示")
        if (ignoreList.isEmpty()) {
            builder.setMessage("本次成功导入 ${allList.size - ignoreList.size} 条记录")
        } else {
            builder.setMessage("本次成功导入 ${allList.size - ignoreList.size} 条，忽略 ${ignoreList.size} 条记录")
                    .setNegativeButton("查看忽略记录", { dialog, which ->
                        showIgnoreListDialog(ignoreList)
                    })
        }
        builder.setPositiveButton("完成", { dialog, which ->
            finish()
        })
                .create().show()
    }

    private fun showIgnoreListDialog(ignoreList: List<CipherTable>) {
        AlertDialog.Builder(this)
                .setTitle("忽略记录")
                .setItems(ignoreList.map { it.name }.toTypedArray(), null)
                .setNegativeButton("完成", null)
                .create().show()
    }

    fun showImportFileWindow() {
        var defaultDir = File(Environment.getExternalStorageDirectory().absolutePath, "CipherBox")
        if (!defaultDir.exists()) {
            defaultDir.mkdirs()
        }

        val intent = Intent(this, ExFilePickerActivity::class.java)
//        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, arrayOf())
        intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true)
        intent.putExtra(ExFilePicker.DISABLE_SORT_BUTTON, true)
        intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_FILES)
        intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true)
        intent.putExtra(ExFilePicker.SET_START_DIRECTORY, defaultDir.absolutePath)
        startActivityForResult(intent, EX_FILE_PICKER_RESULT_CHOOSE_FILE)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.popBackStackImmediate()) {
            currentState = STATE_SHOW
            invalidateOptionsMenu()
        } else {
            finish()
        }
    }

    fun updateTitle(title: String) {
        toolbar.title = title
    }

    fun startFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.layout_cipher_replace_root, fragment)
                .addToBackStack("CipherStack")
                .commit()
    }

    companion object {

        val EXTRA_CIPHER_ID = "extraCipHerId"
        val DEFAULT_VALUE = -1
        val DEFAULT_TYPE_ID = 1

        private val EX_FILE_PICKER_RESULT_CHOOSE_DIR = 1
        private val EX_FILE_PICKER_RESULT_CHOOSE_FILE = 2
        private val TAG = "DetailActivity"
    }
}
