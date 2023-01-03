package com.increff.pos.dao;

import java.util.List;
import javax.persistence.TypedQuery;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

@Repository
public class BrandDao extends AbstractDao {


    // select by brand and category
    private static String selectByBrandCategory = "select p from BrandPojo p where brand=:brand and category=:category";
    // select all brands
    private static String selectAll = "select p from BrandPojo p";
    // search based on brand and category
    private static String search = "select p from BrandPojo p where brand like :brand and category like :category";


    // <queryFunctions>
    // function to select by brand and category
    public BrandPojo selectByBrandCategory(String brand,String category) {
        TypedQuery<BrandPojo> query = getQuery(selectByBrandCategory, BrandPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return getSingle(query);
    }

    // function to select all brands
    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(selectAll, BrandPojo.class);
        return query.getResultList();
    }

    // function to search based on brand and category
    public List<BrandPojo> searchBrandData(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(search, BrandPojo.class);
        query.setParameter("brand", brand+"%");
        query.setParameter("category", category+"%");
        return query.getResultList();
    }
}
