package com.revengemission.sso.oauth2.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonObjects<T> implements Serializable {
    private List<T> objectElements;
    private long currentPage;
    private long totalPage;
    private long total;

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getObjectElements() {
        if (objectElements == null) {
            objectElements = new ArrayList<>();
        }
        return objectElements;
    }
}
