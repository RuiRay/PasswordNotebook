package com.ionesmile.cipherbox.ui.weight

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.activity.TypeListActivity

/**
 * Created by ionesmile on 18/08/2017.
 */
class TypeSelectDialog(var activity: Activity) {

    val typeDao: TypeDao = TypeDao.getInstance(activity)

    fun showSelectTypeDialog(type: TypeTable, callback: (type: TypeTable) -> (Unit)) {
        val typeTables = typeDao.queryAll()
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("请选择类型")
                .setSingleChoiceItems(parseToTextArr(typeTables), getIndexFromType(type, typeTables)) { dialog, which ->
                    callback?.let {
                        callback(typeTables[which])
                    }
                    dialog.dismiss()
                }
                .setNeutralButton("添加类型") { dialog, which ->
                    dialog.dismiss()
                    activity.startActivity(Intent(activity, TypeListActivity::class.java))
                }
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()
    }

    private fun getIndexFromType(type: TypeTable, typeTables: List<TypeTable>): Int {
        val size = typeTables.size
        for (i in 0..size - 1) {
            if (type.databaseId == typeTables[i].databaseId) {
                return i
            }
        }
        // loop can be replace with stdlib operations
        return -1
    }

    private fun parseToTextArr(typeTables: List<TypeTable>): Array<String?> {
        val resultArr = arrayOfNulls<String>(typeTables.size)
        var index = 0
        for (typeTable in typeTables) {
            resultArr[index++] = typeTable.title
        }
        return resultArr
    }
}