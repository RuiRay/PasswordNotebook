package com.ionesmile.cipherbox.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.manager.PreferenceManager
import com.star.lockpattern.util.LockPatternUtil
import com.star.lockpattern.widget.LockPatternView
import org.jetbrains.anko.find

/**
 * Created by ionesmile on 19/08/2017.
 */
class LockActivity : AppCompatActivity() {

    lateinit var lockPatternView: LockPatternView
    lateinit var messageTv: TextView

    private val DELAY_TIME = 600L
    private var gesturePassword: ByteArray? = LockPatternUtil.getSHA1(SettingActivity.DEFAULT_LOCK_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(validEnvSecurity()) return
        CommonManager.setTranslucentBar(window)
        setContentView(R.layout.activity_lock)

        initView()

        // load password
        var settingPassword = PreferenceManager.getLockPatternPassword(this)
        settingPassword?.let {
            var settingByte = settingPassword.toCharArray().map { it.toString().toByte() }.toByteArray()
            gesturePassword = LockPatternUtil.getSHA1(settingByte)
        }
    }

    private fun initView() {
        lockPatternView = find(R.id.lockPatternView)
        lockPatternView.setOnPatternListener(patternListener)
        lockPatternView.setPatternVisible(PreferenceManager.getLockPatternVisible(this))
        messageTv = find(R.id.messageTv)
    }

    private val patternListener = object : LockPatternView.OnPatternListener {

        override fun onPatternStart() {
            lockPatternView.removePostClearPatternRunnable()
        }

        override fun onPatternComplete(pattern: List<LockPatternView.Cell>?) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT)
                } else {
                    updateStatus(Status.ERROR)
                }
            }
        }
    }

    /**
     * 更新状态
     * @param status
     */
    private fun updateStatus(status: Status) {
        messageTv.setText(status.strId)
        messageTv.setTextColor(status.color)
        when (status) {
            Status.DEFAULT -> lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT)
            Status.ERROR -> {
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR)
                lockPatternView.postClearPatternRunnable(DELAY_TIME)
            }
            Status.CORRECT -> {
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT)
                loginGestureSuccess()
            }
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private fun loginGestureSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.view_move_left_show, R.anim.view_move_left_hide)
        finish()
    }

    private fun validEnvSecurity() : Boolean {
        Log.v("checkTAG", "validEnvSecurity()")
        return false
    }

    private enum class Status constructor(val strId: Int, val color: Int) {
        //默认的状态
        DEFAULT(R.string.gesture_default, 0xFFA5A5A5.toInt()),
        //密码输入错误
        ERROR(R.string.gesture_error, 0xFFF4333C.toInt()),
        //密码输入正确
        CORRECT(R.string.gesture_correct, 0xFFA5A5A5.toInt())
    }
}