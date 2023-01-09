package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao{
    // <queries>
    // select by product id
    private static String selectByProductId = "select p from InventoryPojo p where productId=:productId";
    // select all
    private static String selectAll = "select p from InventoryPojo p";

    // <queryFunctions>
    // function to select by product id
    public InventoryPojo selectByProductId(int productId) {
        TypedQuery<InventoryPojo> query = getQuery(selectByProductId, InventoryPojo.class);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    // function to select all from Inventory
    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(selectAll, InventoryPojo.class);
        return query.getResultList();
    }
}
