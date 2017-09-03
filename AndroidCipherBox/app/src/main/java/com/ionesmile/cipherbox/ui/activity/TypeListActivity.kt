package com.ionesmile.cipherbox.ui.activity

import android.app.AlertDialog
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.adapter.SimpleRecyclerAdapter
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemClick
import com.ionesmile.cipherbox.ui.adapter.view.TypeItemView
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class TypeListActivity : BaseActivity(), View.OnClickListener {

    lateinit var etTypeName: EditText
    lateinit var recyclerView: RecyclerView

    lateinit var typeDao: TypeDao

    lateinit var typeAdapter: SimpleRecyclerAdapter<TypeTable>

    var optTypeTable: TypeTable? = null

    override val layoutId: Int
        get() = R.layout.activity_type_list

    override fun initBase() {
        super.initBase()
        CommonManager.setTranslucentBar(window)
        typeDao = TypeDao.getInstance(this)
    }

    override fun initUI() {
        etTypeName = find(R.id.et_type_name)
        recyclerView = find(R.id.recyclerView)
    }

    override fun initData() {
        typeAdapter = SimpleRecyclerAdapter(object : SimpleRecyclerAdapter.ViewHolderCallback() {
            override fun getRecyclerItemView(): View {
                return TypeItemView(baseContext)
            }
        }, typeDao.queryAll())
        typeAdapter.setItemClickListener(object : ItemClick.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (view.id == R.id.view_delete){
                    var typeTable = typeAdapter.getItem(position)
                    if(typeTable.databaseId == DetailActivity.DEFAULT_TYPE_ID){
                        toast("默认类型无法删除！")
                        return
                    }
                    var cipherList = CipherDao.getInstance(baseContext).queryByType(typeTable.databaseId)
                    if (cipherList.size > 0){
                        showForceDeleteDialog(typeTable, position, cipherList)
                    } else {
                        deleteItem(typeTable, position)
                    }
                }
            }
        })
        typeAdapter.setOnItemClickListener { position, view, data ->
            optTypeTable = data as TypeTable
            etTypeName.setText(optTypeTable?.title)
        }
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.top = 2
            }
        })
        recyclerView.adapter = typeAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ciperbox_type, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                saveType()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteItem(typeTable: TypeTable, position: Int) {
        if (optTypeTable?.databaseId == typeTable.databaseId){
            optTypeTable = null
            etTypeName.setText("")
        }
        typeDao.deleteById(typeTable.databaseId)
        typeAdapter.notifyDataChanged(typeDao.queryAll())
    }

    private fun showForceDeleteDialog(type: TypeTable, position: Int, cipherList: List<CipherTable>) {
        AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("共有 ${cipherList.size} 处引用，确定要删除【${type.title}】类型吗？")
                .setNegativeButton("取消", null)
                .setNeutralButton("查看引用"){ dialog, which ->
                    showCipherOfTypeList(cipherList)
                }
                .setPositiveButton("确定") { dialog, which ->
                    updateCipherToDefaultType(cipherList)
                    deleteItem(type, position)
                    dialog.dismiss()
                }
                .create().show()
    }

    private fun showCipherOfTypeList(cipherList: List<CipherTable>) {
        AlertDialog.Builder(this)
                .setTitle("引用详情")
                .setItems(cipherList.map { it.name }.toTypedArray(), null)
                .setNegativeButton("完成", null)
                .create().show()
    }

    private fun updateCipherToDefaultType(cipherList: List<CipherTable>) {
        CipherDao.getInstance(baseContext).executeTransaction {
            for (cipher in cipherList){
                cipher.type = typeDao.getDefaultType()
            }
        }
    }

    override fun initListener() {

    }

    private fun saveType() {
        val typeText = etTypeName.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(typeText)) {
            etTypeName.requestFocus()
            return
        }
        if (optTypeTable == null){
            val tempType = TypeTable()
            tempType.databaseId = typeDao.nextDatabaseId
            tempType.title = typeText
            tempType.description = ""
            typeDao.insertOrUpdate(tempType)
        } else {
            typeDao.executeTransaction {
                optTypeTable?.title = typeText
            }
        }
        finish()
    }
}
