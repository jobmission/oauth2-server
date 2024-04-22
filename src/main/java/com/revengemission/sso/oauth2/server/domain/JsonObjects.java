package com.revengemission.sso.oauth2.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonObjects<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5382742283722856873L;
	private List<T> rows;
    private long total;
    private long pages;

    public List<T> getRows() {
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
