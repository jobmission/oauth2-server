package com.revengemission.sso.oauth2.server.service;


import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.NotImplementException;

public interface CommonServiceInterface<T> {


    default JsonObjects<T> list(int pageNum,
                                int pageSize,
                                String sortField,
                                String sortOrder) {
        throw new NotImplementException();
    }

    default T create(T t) {
        throw new NotImplementException();
    }

    default T retrieveById(long id) {
        throw new NotImplementException();
    }

    default T updateById(T t) {
        throw new NotImplementException();
    }

    default void deleteById(long id) {
        throw new NotImplementException();
    }
}
