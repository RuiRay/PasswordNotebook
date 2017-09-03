package com.ionesmile.cipherbox.model.dao

import android.content.Context
import com.ionesmile.cipherbox.model.RealmManager
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.activity.DetailActivity
import io.realm.Realm
import io.realm.Sort

/**
 * Created by ionesmile on 08/06/2017.
 */

class TypeDao private constructor(private val mRealm: Realm) {

    fun insertOrUpdate(typeTable: TypeTable) {
        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(typeTable)
        mRealm.commitTransaction()
    }

    fun executeTransaction(callback: (realm: Realm) -> (Unit)) {
        mRealm.executeTransaction { realm ->
            callback(realm)
        }
    }

    fun queryById(databaseId: Int): TypeTable? {
        val realmResults = mRealm.where(TypeTable::class.java)
                .equalTo("databaseId", databaseId).findAll()
        if (realmResults.size > 0) {
            return realmResults[0]
        }
        return null
    }

    fun queryByTitle(title: String): TypeTable? {
        val realmResults = mRealm.where(TypeTable::class.java)
                .equalTo("title", title).findAll()
        if (realmResults.size > 0) {
            return realmResults[0]
        }
        return null
    }

    fun getDefaultType(): TypeTable {
        var defaultType: TypeTable? = queryById(DetailActivity.DEFAULT_TYPE_ID)
        if (defaultType == null) {
            // create a default type
            val tempType = TypeTable()
            tempType.databaseId = DetailActivity.DEFAULT_TYPE_ID
            tempType.title = "默认类型"
            tempType.description = ""
            insertOrUpdate(tempType)
            defaultType = queryById(DetailActivity.DEFAULT_TYPE_ID)
        }
        return defaultType!!
    }

    fun queryAll(): List<TypeTable> {
        val realmResults = mRealm.where(TypeTable::class.java)
                .findAll()
        return realmResults
    }

    fun deleteById(databaseId: Int): Boolean {
        val realmResults = mRealm.where(TypeTable::class.java)
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
        val realmResults = mRealm.where(TypeTable::class.java).findAll()
        if (realmResults.size > 0) {
            mRealm.beginTransaction()
            realmResults.deleteAllFromRealm()
            mRealm.commitTransaction()
        }
    }

    val nextDatabaseId: Int
        get() {
            var result = mRealm.where(TypeTable::class.java).findAll()
            if (result.size > 0) {
                result = result.sort("databaseId", Sort.DESCENDING)
                return result[0].databaseId + 1
            }
            return 1
        }

    companion object {

        private var instance: TypeDao? = null

        fun getInstance(context: Context): TypeDao {
            if (instance == null) {
                val realm = RealmManager.getInstance(context).realm
                instance = TypeDao(realm!!)
            }
            return instance!!
        }
    }
}
