package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DailySalesDao extends AbstractDao{
    private static String select_all = "select p from DailySalesPojo p";

    @PersistenceContext
    private EntityManager em;

    public List<DailySalesPojo> selectAll() {
        TypedQuery<DailySalesPojo> query = getQuery(select_all, DailySalesPojo.class);
        return query.getResultList();
    }

    public void insert(DailySalesPojo p) {
        em.persist(p);
    }
}
