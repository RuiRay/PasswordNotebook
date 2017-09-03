package com.ionesmile.cipherbox.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.Log
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.PreferenceManager
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.ui.activity.AboutActivity
import com.ionesmile.cipherbox.ui.activity.SettingActivity
import org.jetbrains.anko.toast

/**
 * Created by ionesmile on 21/08/2017.
 */
class SettingFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.setting_preference)

        initPasswordPref()
        initClick()
        initChecked()
    }

    private fun initClick() {
        findPreference("item_clear_all").setOnPreferenceClickListener {
            showClearAllDialog()
            true
        }
        findPreference("item_about").setOnPreferenceClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
            true
        }
        var listShowOptions = findPreference("list_show_options")
        listShowOptions.summary = resources.getStringArray(R.array.preference_list_show_options)[PreferenceManager.getListShowOptions(activity)]
        listShowOptions.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = resources.getStringArray(R.array.preference_list_show_options)[newValue.toString().toInt()]
            true
        }
    }

    private fun initChecked() {
        (findPreference("switch_lock_track") as SwitchPreference).isChecked = PreferenceManager.getLockPatternVisible(activity)
    }

    private fun initPasswordPref() {
        var passwordPref = findPreference("edit_password_pref")
        if (PreferenceManager.getLockPatternPassword(activity) == null) {
            var defaultPassword = SettingActivity.DEFAULT_LOCK_KEY.map { it.toString() }.reduce { acc, s -> "$acc$s" }
            passwordPref.summary = "默认密码：$defaultPassword，请及时修改"
        } else {
            passwordPref.summary = "******"
        }
        passwordPref.setOnPreferenceChangeListener { preference, newValue ->
            Log.v("SettingTAG", "setOnPreferenceChangeListener() newValue $newValue")
            var inputText = newValue.toString()
            if (inputText.length < 4) {
                toast("最少输入 4 位数！")
                return@setOnPreferenceChangeListener false
            }
            if (inputText.toCharArray().toSet().size < inputText.length) {
                toast("输入不能有重复！")
                return@setOnPreferenceChangeListener false
            }
            if (inputText.contains("0")) {
                toast("输入不能包含 0！")
                return@setOnPreferenceChangeListener false
            }
            preference.summary = inputText
            return@setOnPreferenceChangeListener true
        }
    }

    private fun showClearAllDialog() {
        var dialog = AlertDialog.Builder(activity)
                .setTitle("确定要删除所有记录")
                .setMessage("将清空列表和分类，建议有备份文件")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", { dialog, which ->
                    CipherDao.getInstance(activity).clearAll()
                    TypeDao.getInstance(activity).clearAll()
                    toast("已清空")
                })
                .create()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
}