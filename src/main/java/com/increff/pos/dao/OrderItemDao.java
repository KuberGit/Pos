package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {

    // <queries>
    // select all
    private static String selectAll = "select p from OrderItemPojo p";
    // select by order id
    private static String selectByOrderId = "select p from OrderItemPojo p where orderId=:orderId";
    // delete by order id
    private static String deleteByOrderId = "delete from OrderItemPojo p where orderId=:orderId";
    // select list by order id
    private static String selectByOrderIdList = "select p from OrderItemPojo p where orderId IN:orderIds";

    // <queryFunctions>
    // function to select by order id
    public List<OrderItemPojo> selectByOrderId(int orderId){
        TypedQuery<OrderItemPojo> query = getQuery(selectByOrderId,OrderItemPojo.class);
        query.setParameter("orderId",orderId);
        return query.getResultList();
    }

    // function to select all
    public List<OrderItemPojo> selectAll() {
        TypedQuery<OrderItemPojo> query = getQuery(selectAll, OrderItemPojo.class);
        return query.getResultList();
    }

    // function to delete by order id
    public int deleteByOrderId(int orderId) {
        Query query = em().createQuery(deleteByOrderId);
        query.setParameter("orderId", orderId);
        return query.executeUpdate();
    }

    // function to select list by order id
    public List<OrderItemPojo> selectByOrderIdList(List<Integer> orderIds){
        TypedQuery<OrderItemPojo> query = getQuery(selectByOrderIdList,OrderItemPojo.class);
        query.setParameter("orderIds" , orderIds);
        return query.getResultList();
    }

}
