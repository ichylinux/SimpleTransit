package jp.co.hybitz.simpletransit.common.model;

import java.io.Serializable;

public class Entity implements Serializable {
    private long id;
    private long createdAt;
    private long updatedAt;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
