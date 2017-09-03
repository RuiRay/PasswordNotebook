package com.ionesmile.cipherbox.model.table;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by ionesmile on 12/08/2017.
 */

@RealmClass
public class CipherTable implements RealmModel {

    @PrimaryKey
    private int databaseId;
    private String name;
    private String account;
    private String password;
    private String url;
    private String remark;
    private int secureGrade;
    private byte[] icon;
    private long createTime;
    private long updateTime;
    private int priority;
    private TypeTable type;

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSecureGrade() {
        return secureGrade;
    }

    public void setSecureGrade(int secureGrade) {
        this.secureGrade = secureGrade;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TypeTable getType() {
        return type;
    }

    public void setType(TypeTable type) {
        this.type = type;
    }
}
