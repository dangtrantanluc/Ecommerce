package com.store.E_Commerce.api.model;

import com.store.E_Commerce.model.Address;

public class DataChange<T> {
    private T data;
    private ChangeType changeType;

    public DataChange(ChangeType changeType, T data) {
        this.changeType = changeType;
        this.data = data;
    }

    public DataChange() {

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public enum ChangeType {
        INSERT, UPDATE, DELETE
    }
}
