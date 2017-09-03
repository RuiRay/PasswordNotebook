package com.ionesmile.cipherbox.model

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel

/**
 * Created by ionesmile on 08/06/2017.
 */

class RealmManager private constructor(context: Context) {

    val realm: Realm

    init {
        Realm.init(context)
        val defaultConfig = RealmConfiguration.Builder()
                //                .name("cipherBox.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(defaultConfig)
    }

    fun close() {
        realm.close()
        mInstance = null
    }

    fun <E : RealmModel> copyToRealm(`object`: E): E {
        return realm!!.copyToRealm(`object`)
    }

    companion object {

        private var mInstance: RealmManager? = null

        fun getInstance(context: Context): RealmManager {
            if (mInstance == null) {
                mInstance = RealmManager(context.applicationContext)
            }
            return mInstance!!
        }
    }

}