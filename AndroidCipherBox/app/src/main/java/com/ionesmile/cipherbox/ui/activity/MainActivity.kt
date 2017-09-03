package com.ionesmile.cipherbox.ui.activity

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.manager.util.ImportExportHelper
import com.ionesmile.cipherbox.manager.util.MenuOptIcon
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.adapter.CipherSwipeAdapter
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.onClick
import ru.bartwell.exfilepicker.ExFilePicker
import ru.bartwell.exfilepicker.ExFilePickerParcelObject
import java.io.File


class MainActivity : AppCompatActivity() {

    internal val EX_FILE_PICKER_RESULT_CHOOSE_DIR = 1

    lateinit var cipherAdapter: CipherSwipeAdapter
    lateinit var recyclerView: SwipeMenuRecyclerView
    lateinit var toolbar: Toolbar
    var searchView: SearchView? = null

    lateinit var cipherDao: CipherDao

    var selectedType: TypeTable? = null
    var currentSearchKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonManager.setTranslucentBar(window)
        setContentView(R.layout.activity_main)

        cipherDao = CipherDao.getInstance(this)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.onClick {
            showTypeSelectDialog()
        }

        recyclerView = findViewById(R.id.recyclerView) as SwipeMenuRecyclerView

        initListData()

        startSecureValid()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        MenuOptIcon.setMenuIconShow(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ciperbox_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        searchView?.queryHint = "请输入搜索内容"
        searchView?.backgroundColor = Color.TRANSPARENT
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SearchViewTAG", "searchView onQueryTextSubmit() query = " + query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.v("SearchViewTAG", "searchView onQueryTextChange() newText = " + newText)
                currentSearchKey = newText
                refreshList()
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this@MainActivity, DetailActivity::class.java))
            }
            R.id.action_export -> {
                showExportFolderSelect()
            }
            R.id.action_setting -> {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EX_FILE_PICKER_RESULT_CHOOSE_DIR) {
            if (data != null) {
                val parcelObject: ExFilePickerParcelObject = data.getParcelableExtra<Parcelable>(ExFilePickerParcelObject::class.java.canonicalName) as ExFilePickerParcelObject
                if (parcelObject.count > 0) {
                    // Here is object contains selected files names and path
                    Log.i("CipherTAG", "onActivityResult() path = " + parcelObject.path + "   count = " + parcelObject.count + "   names = " + parcelObject.names)
                    val file = File(parcelObject.path, parcelObject.names[0])
                    if (file.isDirectory) {
                        ImportExportHelper.showExportPasswordInputDialog(this, { encryptKey ->
                            ImportExportHelper.exportCipherFile(baseContext, file.absolutePath, encryptKey)
                        })
                    }
                }
            }
        }
    }

    private fun showExportFolderSelect() {
        var defaultDir = File(Environment.getExternalStorageDirectory().absolutePath, "CipherBox")
        if (!defaultDir.exists()) {
            defaultDir.mkdirs()
        }

        val intent = Intent(this, ru.bartwell.exfilepicker.ExFilePickerActivity::class.java)
        intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true)
        intent.putExtra(ExFilePicker.DISABLE_SORT_BUTTON, true)
        intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_DIRECTORIES)
        intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true)
        intent.putExtra(ExFilePicker.SET_START_DIRECTORY, defaultDir.absolutePath)
        startActivityForResult(intent, EX_FILE_PICKER_RESULT_CHOOSE_DIR)
    }

    fun initListData() {
        cipherAdapter = CipherSwipeAdapter(this, null)

        var itemClickListener = CipherSwipeAdapter.OnItemClickListener<CipherTable> { position, cipherTable ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_CIPHER_ID, cipherTable.databaseId)
            startActivity(intent)
        }

        cipherAdapter.setOnItemClickListener(itemClickListener)
        cipherAdapter.setOnItemLongClickListener { position, data ->
            showConfirmDeleteDialog(data)
        }
        val layoutManager = LinearLayoutManager(recyclerView!!.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.top = 2
            }
        })
        recyclerView.adapter = cipherAdapter

        // 设置菜单创建器。
        recyclerView.setSwipeMenuCreator(createSwipeMenuCreator())
        // 设置菜单Item点击监听。
        recyclerView.setSwipeMenuItemClickListener( { closeable, adapterPosition, menuPosition, direction ->
            closeable.smoothCloseMenu()
            var cipherTable = cipherAdapter.data[adapterPosition]
            when(menuPosition){
                0 -> CommonManager.setClipboard(baseContext, cipherTable.account)
                1 -> CommonManager.setClipboard(baseContext, cipherTable.password)
            }
        })
    }

    private fun showConfirmDeleteDialog(data: CipherTable) {
        AlertDialog.Builder(this).setTitle("提示")
                .setMessage("确定要删除【${data.name}】记录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", { dialog, which ->
                    cipherDao.deleteById(data.databaseId)
                    refreshList()
                })
                .create().show()
    }

    private fun showTypeSelectDialog() {
        val typeList = TypeDao.getInstance(baseContext).queryAll()
        var titleArrays = typeList.map { it.title }.toTypedArray()
        titleArrays = arrayListOf("所有记录", *titleArrays).toTypedArray()

        var selectIndex = 0
        if (selectedType != null) {
            selectIndex = typeList.indices.firstOrNull { selectedType?.databaseId == typeList[it].databaseId } ?: -1
            selectIndex ++
        }

        var dialog = AlertDialog.Builder(this)
                .setTitle("选择分类")
                .setSingleChoiceItems(titleArrays, selectIndex, { dialog, which ->
                    if (which == 0) {
                        selectedType = null
                    } else {
                        selectedType = typeList[which - 1]
                    }
                    refreshList()
                    dialog.dismiss()
                })
                .create()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun createSwipeMenuCreator(): SwipeMenuCreator {
        var swipeMenuCreator = SwipeMenuCreator { swipeLeftMenu, swipeRightMenu, viewType ->
            val accountItem = SwipeMenuItem(baseContext)
                    .setBackgroundDrawable(R.drawable.selector_cipher_item_option_bg)
                    .setText("用户名")
                    .setTextColor(Color.WHITE)
                    .setTextSize(16)
                    .setWidth(CommonManager.dp2px(baseContext, 80f))
                    .setHeight(RecyclerView.LayoutParams.MATCH_PARENT)
            val passwordItem = SwipeMenuItem(baseContext)
                    .setBackgroundDrawable(R.drawable.selector_cipher_item_option_password_bg)
                    .setText("密码")
                    .setTextColor(Color.WHITE)
                    .setTextSize(16)
                    .setWidth(CommonManager.dp2px(baseContext, 80f))
                    .setHeight(RecyclerView.LayoutParams.MATCH_PARENT)
            swipeRightMenu.addMenuItem(accountItem)
            swipeRightMenu.addMenuItem(passwordItem)
        }
        return swipeMenuCreator
    }

    private fun refreshList() {
        var cipherTables: List<CipherTable>
        if (selectedType == null) {
            if (TextUtils.isEmpty(currentSearchKey)){
                cipherTables = cipherDao.queryAll()
            } else {
                cipherTables = cipherDao.queryByKey(currentSearchKey)
            }
            toolbar.subtitle = "所有记录（${cipherTables.size}）"
        } else {
            if (TextUtils.isEmpty(currentSearchKey)){
                cipherTables = cipherDao.queryByType(selectedType?.databaseId!!)
            } else {
                cipherTables = cipherDao.queryByKeyAndType(currentSearchKey, selectedType!!)
            }
            toolbar.subtitle = "${selectedType?.title}（${cipherTables.size}）"
        }
        cipherAdapter.notifyDataChanged(cipherTables)
    }

    private fun startSecureValid() {

    }

}
