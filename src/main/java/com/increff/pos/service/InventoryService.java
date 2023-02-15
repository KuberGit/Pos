package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao dao;

    @Transactional
    public InventoryPojo add(InventoryPojo p) {
        dao.insert(p);
        return p;
    }

    @Transactional
    public InventoryPojo get(int id) {
        return dao.select(InventoryPojo.class, id);
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public InventoryPojo getByProduct(ProductPojo p) {
        return dao.selectByProductId(p.getId());
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo update(int id,InventoryPojo p) throws ApiException {
        InventoryPojo newP = check(id);
        newP.setQuantity(p.getQuantity());
        dao.update(newP);
        return newP;
    }

    @Transactional
    public InventoryPojo check(int id) throws ApiException {
        InventoryPojo p = dao.select(InventoryPojo.class, id);
        if (p == null) {
            throw new ApiException("Inventory doesn't exist - id " + id);
        }
        return p;
    }
}
