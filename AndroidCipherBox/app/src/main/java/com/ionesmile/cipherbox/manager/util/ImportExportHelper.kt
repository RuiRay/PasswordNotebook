package com.ionesmile.cipherbox.manager.util

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.CipherTable
import org.jetbrains.anko.find
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ionesmile on 19/08/2017.
 */
object ImportExportHelper {

    fun exportCipherFile(context: Context, folderPath: String?, password: String) : Boolean {
        val rootFile = File(folderPath)
        if (!rootFile.exists()) {
            rootFile.mkdirs()
        }
        val outFile = File(rootFile, getFileName())
        val success = FileUtil.writeFile(outFile.absolutePath, getCipherText(parsePasswordToEncryptKey(password), context))
        toast(context, if(success) "导出成功" else "导出失败")
        return success
    }

    fun importCipherFile(context: Context, filePath: String?, password: String) : Array<List<CipherTable>> {
        var fileContent: String = FileUtil.readFile(filePath)
        try {
            fileContent = EncryptUtil.decrypt(parsePasswordToEncryptKey(password), fileContent)
        } catch (e: Exception){
            toast(context, "解密失败")
            Log.w("ImportExportHelper", "importCipherFile()", e)
            return arrayOf()
        }
        var cipherList = GsonHelper.fromJsonArray(fileContent, CipherTable::class.java)
        cipherList?.let {
            val cipherDao = CipherDao.getInstance(context)
            val typeDao = TypeDao.getInstance(context)
            var ignoreList = ArrayList<CipherTable>()
            for (table in cipherList){
                if (cipherDao.exist(table) == null){
                    table.databaseId = cipherDao.nextDatabaseId
                    if(table.type == null)
                        table.type = typeDao.getDefaultType()
                    else {
                        var type = typeDao.queryByTitle(table.type.title)
                        if (type != null){
                            table.type = type
                        }
                    }
                    if(table.password != null)
                        table.password = PasswordHelper.decrypt(table.password)
                    cipherDao.insertOrUpdate(table)
                } else {
                    ignoreList.add(table)
                }
            }
            return arrayOf(cipherList, ignoreList)
        }
        toast(context, "数据解析异常")
        return arrayOf(ArrayList<CipherTable>(0))
    }

    fun showImportPasswordInputDialog(context: Context, callback: (encryptKey: String) -> (Boolean)) {
        showPasswordInputDialog("请输入解密密码", "", context, callback)
    }

    fun showExportPasswordInputDialog(context: Context, callback: (encryptKey: String) -> (Unit)) {
        showPasswordInputDialog("请输入加密密码", "警告：务必妥善保管好导出的文件，以防泄露", context, {
            callback(it)
            true
        })
    }

    fun showPasswordInputDialog(title: String, tip: String, context: Context, callback: (encryptKey: String) -> (Boolean)) {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_input_password, null)
        val etPwd = rootView.find<EditText>(R.id.et_password)
        val tvTip = rootView.find<TextView>(R.id.tv_tip)
        tvTip.text = tip
        var dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(rootView)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                var password = etPwd.text.toString().trim()
                if (password.length < 1) {
                    toast(context, "密码不能为空")
                    return@setOnClickListener
                }
                if(callback(password))
                    dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun getCipherText(encryptKey: String, context: Context): String? {
        val cipherDao = CipherDao.getInstance(context)
        val cipherTables = cipherDao.queryAllCopy()
        for (table in cipherTables) {
            if (table.password != null) {
                table.password = PasswordHelper.encrypt(table.password)
            }
        }
        var text = GsonHelper.toJson(cipherTables)
        text = EncryptUtil.encrypt(encryptKey, text)
        return text
    }

    private fun parsePasswordToEncryptKey(password: String): String {
        return EncryptUtil.getMD5Base64Value(password.toByteArray())
    }

    private fun getFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        var fileName = sdf.format(Date())
        fileName = "CipherBox_${fileName}.dat"
        return fileName
    }

    internal fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}