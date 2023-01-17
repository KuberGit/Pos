package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao{

    private static String selectByBarcode = "select p from ProductPojo p where barcode=:barcode";
    // select all
    private static String selectAll = "select p from ProductPojo p";
    // search based on name and barcode
    private static String search = "select p from ProductPojo p where name like :name and barcode like :barcode";

    private static String select_Products_By_BrandId = "select p from ProductPojo p where brandCategoryId=:id";

    // <queryFunctions>
    // function to select by barcode
    public ProductPojo selectByBarcode(String barcode){
        TypedQuery<ProductPojo> query = getQuery(selectByBarcode,ProductPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    // function to select all
    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(selectAll, ProductPojo.class);
        return query.getResultList();
    }

    public List<ProductPojo> getProductByBrandCategory (int brandCategoryId) {
        TypedQuery<ProductPojo> query = getQuery(select_Products_By_BrandId, ProductPojo.class);
        query.setParameter("id",brandCategoryId);
        return query.getResultList();
    }

    // function to search based on name and barcode
    public List<ProductPojo> searchProductData(String barcode, String name) {
        TypedQuery<ProductPojo> query = getQuery(search, ProductPojo.class);
        query.setParameter("barcode", barcode + "%");
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
}
