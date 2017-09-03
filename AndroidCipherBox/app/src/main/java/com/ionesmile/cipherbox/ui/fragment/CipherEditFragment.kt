package com.ionesmile.cipherbox.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager.setLogoImage
import com.ionesmile.cipherbox.model.dao.CipherDao
import com.ionesmile.cipherbox.model.dao.TypeDao
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.activity.DetailActivity
import com.ionesmile.cipherbox.ui.weight.TypeSelectDialog
import org.jetbrains.anko.onClick

/**
 * Created by ionesmile on 17/08/2017.
 */
class CipherEditFragment : BaseFragment(){

    lateinit var ivIcon: ImageView
    lateinit var etWebsite: EditText
    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var etUrl: EditText
    lateinit var tvType: TextView
    lateinit var rbSecure: SimpleRatingBar
    lateinit var etRemark: EditText

    lateinit var cipherDao: CipherDao
    lateinit var typeDao: TypeDao

    lateinit var mCipherTable: CipherTable
    var mCipherTableId: Int = DetailActivity.DEFAULT_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cipherDao = CipherDao.getInstance(context)
        typeDao = TypeDao.getInstance(context)
        mCipherTableId = arguments.getInt(DetailActivity.EXTRA_CIPHER_ID, mCipherTableId)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_chiper_edit
    }

    override fun initView() {
        ivIcon = findViewById(R.id.iv_website_icon) as ImageView
        etWebsite = findViewById(R.id.et_website) as EditText
        etUsername = findViewById(R.id.et_username) as EditText
        etPassword = findViewById(R.id.et_password) as EditText
        etUrl = findViewById(R.id.et_url) as EditText
        tvType = findViewById(R.id.tv_type) as TextView
        rbSecure = findViewById(R.id.ratingBar_secure) as SimpleRatingBar
        etRemark = findViewById(R.id.et_remark) as EditText
        initListener()
    }

    override fun initData() {
        if (mCipherTableId == DetailActivity.DEFAULT_VALUE) {
            (activity as DetailActivity).updateTitle("添加信息")
        } else {
            (activity as DetailActivity).updateTitle("修改信息")
        }
        mCipherTable = loadCipherTable()
        updateViewByCipher(mCipherTable)
    }

    private fun initListener() {
        tvType.onClick {
            TypeSelectDialog(activity).showSelectTypeDialog(tvType.tag as TypeTable, { type ->
                tvType.tag = type
                tvType.text = type.title
            })
        }

        (findViewById(R.id.cb_password_visible) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun updateViewByCipher(cipher: CipherTable) {
        etWebsite.setText(cipher.name)
        etUsername.setText(cipher.account)
        etPassword.setText(cipher.password)
        etUrl.setText(cipher.url)
        tvType.text = cipher.type.title
        tvType.tag = cipher.type
        etRemark.setText(cipher.remark)
        rbSecure.rating = cipher.secureGrade / 2.0f
        setLogoImage(ivIcon, cipher.icon)
    }

    fun loadCipherTable() : CipherTable {
        var cipher : CipherTable
        if (mCipherTableId != DetailActivity.DEFAULT_VALUE) {
            cipher = cipherDao.queryById(mCipherTableId)
        } else {
            cipher = CipherTable()
            cipher.type = typeDao.getDefaultType()
        }
        return cipher
    }

    fun saveCipherTable() {
        if (!checkInput()) {
            return
        }
        if (mCipherTableId == DetailActivity.DEFAULT_VALUE) {
            mCipherTable.databaseId = cipherDao.nextDatabaseId
            mCipherTable.createTime = System.currentTimeMillis()
            cipherDao.insertOrUpdate(mCipherTable)
            mCipherTable = cipherDao.queryById(mCipherTable.databaseId)
        }
        saveInputInfo(mCipherTable)
    }

    private fun checkInput(): Boolean {
        return true
    }

    private fun saveInputInfo(cipherTable: CipherTable) {
        cipherDao.executeTransaction { realm ->
            cipherTable.name = etWebsite.text.toString().trim()
            cipherTable.account = etUsername.text.toString().trim()
            cipherTable.password = etPassword.text.toString().trim()
            cipherTable.url = etUrl.text.toString().trim()
            cipherTable.type = tvType.tag as TypeTable
            cipherTable.remark = etRemark.text.toString().trim()
            cipherTable.secureGrade = (rbSecure.rating * 2).toInt()
            cipherTable.updateTime = System.currentTimeMillis()
        }
    }

    companion object {

        fun newInstance(cipherTableId: Int): CipherEditFragment {
            val args = Bundle()
            args.putInt(DetailActivity.EXTRA_CIPHER_ID, cipherTableId)
            val fragment = CipherEditFragment()
            fragment.arguments = args
            return fragment
        }
    }

}