package com.revengemission.sso.oauth2.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonObjects<T> implements Serializable {
    private List<T> data;
    private int draw;
    private long recordsFiltered;
    private long recordsTotal;

    public List<T> getData() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }
}
