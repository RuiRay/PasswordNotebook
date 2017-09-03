package com.ionesmile.cipherbox.model.dao

import android.content.Context
import com.ionesmile.cipherbox.model.RealmManager
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.model.table.TypeTable
import io.realm.Case
import io.realm.Realm
import io.realm.RealmModel
import io.realm.Sort

/**
 * Created by ionesmile on 08/06/2017.
 */

class CipherDao private constructor(private val mRealm: Realm) {

    fun insertOrUpdate(`object`: RealmModel) {
        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(`object`)
        mRealm.commitTransaction()
    }

    fun executeTransaction(callback: (realm: Realm) -> (Unit)) {
        mRealm.executeTransaction { realm ->
            callback(realm)
        }
    }

    fun queryById(databaseId: Int): CipherTable {
        val realmResults = mRealm.where(CipherTable::class.java)
                .equalTo("databaseId", databaseId).findAll()
        if (realmResults.size > 0) {
            return realmResults[0]
        }
        return null!!
    }

    fun exist(table: CipherTable): CipherTable? {
        val realmResults = mRealm.where(CipherTable::class.java)
                .equalTo("name", table.name)
                .equalTo("account", table.account)
                .equalTo("password", table.password)
                .equalTo("url", table.url)
//                .equalTo("remark", table.remark)  ?
                .findAll()
        if (realmResults.size > 0) {
            return realmResults[0]
        }
        return null
    }

    fun queryAll(): List<CipherTable> {
        val realmResults = mRealm.where(CipherTable::class.java)
                .findAll()
        return realmResults
    }

    fun queryByType(typeId: Int): List<CipherTable> {
        val realmResults = mRealm.where(CipherTable::class.java)
                .equalTo("type.databaseId", typeId)
                .findAll()
        return realmResults
    }

    fun queryAllCopy(): List<CipherTable> {
        val realmResults = mRealm.where(CipherTable::class.java)
                .findAll()
        return mRealm.copyFromRealm(realmResults)
    }

    fun queryByKey(key: String?): List<CipherTable> {
        if (key != null){
            val realmResults = mRealm.where(CipherTable::class.java)
                    .contains("name", key, Case.INSENSITIVE)
                    .or()
                    .contains("url", key, Case.INSENSITIVE)
                    .or()
                    .contains("remark", key, Case.INSENSITIVE)
                    .findAll()
            return realmResults
        } else {
            return queryAll()
        }
    }

    fun queryByKeyAndType(key: String?, type: TypeTable): List<CipherTable> {
        if (key != null){
            val realmResults = mRealm.where(CipherTable::class.java)
                    .equalTo("type.databaseId", type.databaseId)
                    .beginGroup()
                    .contains("name", key, Case.INSENSITIVE)
                    .or()
                    .contains("url", key, Case.INSENSITIVE)
                    .or()
                    .contains("remark", key, Case.INSENSITIVE)
                    .endGroup()
                    .findAll()
            return realmResults
        } else {
            return queryByType(type.databaseId)
        }
    }

    fun deleteById(databaseId: Int): Boolean {
        val realmResults = mRealm.where(CipherTable::class.java)
                .equalTo("databaseId", databaseId).findAll()
        if (realmResults.size > 0) {
            mRealm.beginTransaction()
            realmResults.deleteAllFromRealm()
            mRealm.commitTransaction()
            return true
        }
        return false
    }

    fun clearAll() {
        val realmResults = mRealm.where(CipherTable::class.java).findAll()
        if (realmResults.size > 0) {
            mRealm.beginTransaction()
            realmResults.deleteAllFromRealm()
            mRealm.commitTransaction()
        }
    }

    val nextDatabaseId: Int
        get() {
            var result = mRealm.where(CipherTable::class.java).findAll()
            if (result.size > 0) {
                result = result.sort("databaseId", Sort.DESCENDING)
                return result[0].databaseId + 1
            }
            return 1
        }

    companion object {

        private var instance: CipherDao? = null

        fun getInstance(context: Context): CipherDao {
            if (instance == null) {
                val realm = RealmManager.getInstance(context).realm
                instance = CipherDao(realm)
            }
            return instance!!
        }
    }
}
