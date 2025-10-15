package com.mycompany.trabajo.practico.integrador.p2.daos;

import java.sql.Connection;
import java.util.List;
public interface GenericDao<T> {
    void create(T entity, Connection conn) throws Exception;
    T read(Long id, Connection conn) throws Exception;
    List<T> readAll(Connection conn) throws Exception;
    void update(T entity, Connection conn) throws Exception;
    void delete(Long id, Connection conn) throws Exception;
}
