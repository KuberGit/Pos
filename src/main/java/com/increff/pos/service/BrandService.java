package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;

    @Transactional
    public BrandPojo add(BrandPojo p) throws ApiException {
        NormalizeUtil.normalizeBrandMasterPojo(p);
        getCheckBrandCategoryExist(p.getBrand(),p.getCategory());
        dao.insert(p);
        return p;
    }

    @Transactional(readOnly = true)
    public BrandPojo get(int id) {
        return dao.select(BrandPojo.class,id);
    }

    @Transactional
    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public BrandPojo getByBrandCategory(BrandForm form) throws ApiException {
        NormalizeUtil.normalizeBrandForm(form);
        return getCheckForBrandCategory(form);
    }

    @Transactional
    public BrandPojo update(int id, BrandPojo p) throws ApiException {
        NormalizeUtil.normalizeBrandMasterPojo(p);
        getCheckBrandCategoryExist(p.getBrand(),p.getCategory());
        BrandPojo ex = getCheck(id);
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.update(ex);
        return ex;
    }

    @Transactional
    public List<BrandPojo> searchBrandCategoryData(BrandForm form) {
        NormalizeUtil.normalizeBrandForm(form);
        return dao.searchBrandData(form.getBrand(),form.getCategory());
    }

    private BrandPojo getCheck(int id) throws ApiException {
        BrandPojo p = dao.select(BrandPojo.class,id);
        if (p == null) {
            throw new ApiException("Brand and Category with given ID does not exist - id: " + id);
        }
        return p;
    }

    private void getCheckBrandCategoryExist(String brand, String category) throws ApiException {
        BrandPojo p = dao.selectByBrandCategory(brand,category);
        if (p != null) {
            throw new ApiException("Brand and Category already exists");
        }
    }

    private BrandPojo getCheckForBrandCategory(BrandForm form) throws ApiException {
        BrandPojo p = dao.selectByBrandCategory(form.getBrand(),form.getCategory());
        if (p == null) {
            throw new ApiException("Brand-Category doesn't exist");
        }
        return p;
    }

}
