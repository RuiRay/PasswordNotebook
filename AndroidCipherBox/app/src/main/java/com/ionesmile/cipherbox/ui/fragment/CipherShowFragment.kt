package com.ionesmile.cipherbox.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.activity.DetailActivity
import com.ionesmile.cipherbox.ui.activity.DetailActivity.Companion.DEFAULT_VALUE
import com.ionesmile.cipherbox.ui.activity.DetailActivity.Companion.EXTRA_CIPHER_ID
import com.ionesmile.cipherbox.ui.weight.TypeSelectDialog
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

/**
 * Created by ionesmile on 17/08/2017.
 */
class CipherShowFragment : BaseFragment(){

    lateinit var ivIcon: ImageView
    lateinit var tvWebsite: TextView
    lateinit var tvUsername: TextView
    lateinit var tvPassword: TextView
    lateinit var tvUrl: TextView
    lateinit var tvType: TextView
    lateinit var rbSecure: SimpleRatingBar
    lateinit var tvRemark: TextView

    lateinit var currentCipher: CipherTable

    lateinit var cipherDao: CipherDao
    lateinit var typeDao: TypeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cipherDao = CipherDao.getInstance(context)
        typeDao = TypeDao.getInstance(context)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_chiper_show
    }

    override fun initView() {
        ivIcon = findViewById(R.id.iv_website_icon) as ImageView
        tvWebsite = findViewById(R.id.tv_website) as TextView
        tvUsername = findViewById(R.id.tv_username) as TextView
        tvPassword = findViewById(R.id.tv_password) as TextView
        tvUrl = findViewById(R.id.tv_url) as TextView
        tvType = findViewById(R.id.tv_type) as TextView
        rbSecure = findViewById(R.id.ratingBar_secure) as SimpleRatingBar
        tvRemark = findViewById(R.id.tv_remark) as TextView
        tvRemark.movementMethod = ScrollingMovementMethod.getInstance()
        initListener()
    }

    override fun initData() {
        refreshInfo()
    }

    private fun initListener() {
        var longClickListener = { v: View? ->
            if (v is TextView) {
                var copyText = v.text.trim()
                CommonManager.setClipboard(context, copyText)
            }
            true
        }
        tvWebsite.onLongClick(longClickListener)
        tvUsername.onLongClick(longClickListener)
        tvUrl.onLongClick(longClickListener)
        tvRemark.onLongClick(longClickListener)
        tvPassword.onLongClick {
            CommonManager.setClipboard(context, currentCipher.password)
            return@onLongClick true
        }

        tvType.onClick {
            TypeSelectDialog(activity).showSelectTypeDialog(tvType.tag as TypeTable, { type ->
                tvType.tag = type
                tvType.text = type.title
                cipherDao.executeTransaction {
                    currentCipher.type = type
                }
            })
        }

        rbSecure.setOnRatingBarChangeListener { simpleRatingBar, rating, fromUser ->
            if (fromUser){
                cipherDao.executeTransaction {
                    currentCipher.secureGrade = (rating * 2).toInt()
                }
            }
        }

        (findViewById(R.id.cb_password_visible) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                tvPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    fun refreshInfo(){
        loadCipherTable()
        updateViewByCipher(currentCipher)
        (activity as DetailActivity).updateTitle(currentCipher.name)
    }

    private fun updateViewByCipher(cipher: CipherTable) {
        tvWebsite.text = cipher.name
        tvUsername.text = cipher.account
        tvPassword.text = cipher.password
        tvUrl.text = cipher.url
        tvType.text = cipher.type.title
        tvType.tag = cipher.type
        tvRemark.text = cipher.remark
        rbSecure.rating = cipher.secureGrade / 2.0f
        CommonManager.setLogoImage(ivIcon, cipher.icon)
    }

    fun loadCipherTable() {
        val cipherId = arguments.getInt(EXTRA_CIPHER_ID, DEFAULT_VALUE)
        if (cipherId != DEFAULT_VALUE) {
            currentCipher = cipherDao.queryById(cipherId)
        } else {
            Log.w("ShowFragment", "loadCipherTable() cipherId INVALID.")
            activity.finish()
        }
    }

    companion object {

        fun newInstance(cipherTableId: Int): CipherShowFragment {
            var args = Bundle()
            args.putInt(DetailActivity.EXTRA_CIPHER_ID, cipherTableId)
            var fragment = CipherShowFragment()
            fragment.arguments = args
            return fragment
        }
    }
}