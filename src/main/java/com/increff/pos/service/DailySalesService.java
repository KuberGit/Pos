package com.increff.pos.service;

import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.pojo.DailySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DailySalesService {
    @Autowired
    private DailySalesDao dao;

    @Transactional
    public List<DailySalesPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void add(DailySalesPojo p) {
        dao.insert(p);
    }
}
