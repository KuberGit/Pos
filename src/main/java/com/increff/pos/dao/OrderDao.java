package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao {

    // <queries>
    // select all
    private static String select_All = "select p from OrderPojo p";
    // search based on orderUser
    private static String search = "select p from OrderPojo p where orderUser like :orderUser";

    private static String get_by_time = "select p from OrderPojo p where datetime>=:start and datetime<=:end";

    // <queryFunctions>
    // function to select all
    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_All, OrderPojo.class);
        return query.getResultList();
    }

    // function to search based on orderUser
    public List<OrderPojo> searchOrderData(String orderUser) {
        TypedQuery<OrderPojo> query = getQuery(search, OrderPojo.class);
        query.setParameter("orderUser", orderUser + "%");
        return query.getResultList();
    }

    public List<OrderPojo> selectAllInTimeDuration(Date start, Date end) {
        TypedQuery<OrderPojo> query = getQuery(get_by_time, OrderPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

}
