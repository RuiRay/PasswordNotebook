package com.ionesmile.cipherbox.manager

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.ionesmile.cipherbox.R

import java.io.ByteArrayOutputStream

/**
 * Created by ionesmile on 14/06/2017.
 */

object CommonManager {

    fun bitmap2Bytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun bytes2Bitmap(b: ByteArray?): Bitmap? {
        if (b != null && b.size != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            return null
        }
    }

    @JvmStatic fun setTranslucentBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            var color: Int = 0xEF393A3F.toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = color
            }
        }
    }

    @JvmStatic fun setClipboard(context: Context, copyText: CharSequence) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = copyText
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
    }

    @JvmStatic fun setLogoImage(ivIcon: ImageView, iconBytes: ByteArray?) {
        var bitmap = CommonManager.bytes2Bitmap(iconBytes)
        if (bitmap != null) {
            ivIcon.setImageBitmap(bitmap)
        } else {
            ivIcon.setImageResource(R.mipmap.ic_launcher)
        }
    }

    @JvmStatic fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
