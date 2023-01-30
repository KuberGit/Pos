package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProductService {
    @Autowired
    private ProductDao dao;


    @Transactional(rollbackFor = ApiException.class)
    public ProductPojo add(ProductPojo p, BrandPojo b) throws ApiException {
        ProductPojo ex = dao.selectByBarcode(p.getBarcode());
        if(ex == null){
            dao.insert(p);
            return p;
        }else{
            String barcode = StringUtil.generateBarcode();
            ProductPojo newP = new ProductPojo();
            newP.setBarcode(barcode);
            newP.setBrandCategoryId(b.getId());
            newP.setMrp(p.getMrp());
            newP.setName(p.getName());
            return add(newP,b);
        }
    }

    @Transactional
    public ProductPojo get(int id) {
        return dao.select(ProductPojo.class,id);
    }


    public List<ProductPojo> getProductByBrandCategoryId(int brandCategoryId) {
        return dao.getProductByBrandCategory(brandCategoryId);
    }

    @Transactional
    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public ProductPojo getByBarcode(String barcode) throws ApiException {
        barcode = StringUtil.toLowerCase(barcode);
        ProductPojo p = dao.selectByBarcode(barcode);
        if(p == null){
            throw new ApiException("Barcode doesn't exist");
        }else{
            return p;
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public ProductPojo update(int id, ProductPojo p,BrandPojo b) throws ApiException {
        ProductPojo newP = check(id);
        newP.setBrandCategoryId(b.getId());
        newP.setMrp(p.getMrp());
        newP.setName(p.getName());
        dao.update(newP);
        return newP;
    }

    @Transactional
    public List<ProductPojo> searchProductData(ProductSearchForm f) {
        return dao.searchProductData(f.getBarcode(), f.getName());
    }

    @Transactional
    private ProductPojo check(int id) throws ApiException {
        ProductPojo p = dao.select(ProductPojo.class,id);
        if(p == null){
            throw new ApiException("Product doesn't exist - id : " + id);
        }else{
            return p;
        }
    }

}
