package com.increff.pos.service;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDao brandDao;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void addBrandCategoryTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        BrandPojo actualPojo = brandService.add(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        assertEquals(expectedPojo.getId(), actualPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());
    }

    @Test
    public void addDuplicateBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand and Category already exists");
        brandService.add(brandPojo); // This should throw the exception
    }

    @Test
    public void getAllBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        BrandPojo newBrandPojo = TestUtils.getBrandPojo("nike", "shoes");
        brandDao.insert(brandPojo);
        brandDao.insert(newBrandPojo);
        List<BrandPojo> expectedBrandPojoList = brandDao.selectAll();
        List<BrandPojo> actualBrandPojoList = brandService.getAll();
        assertEquals(expectedBrandPojoList.size(), actualBrandPojoList.size());
    }

    @Test
    public void getBrandByIdTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        BrandPojo actualPojo = brandService.get(expectedPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand and Category with given ID does not exist - id: 5");
        brandService.get(5);
    }

    @Test
    public void getIfNameAndCategoryExistTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        BrandForm brandForm = TestUtils.getBrandForm("apple","electronics");
        BrandPojo actualPojo = brandService.getByBrandCategory(brandForm);
        assertEquals(expectedPojo.getId(), actualPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand-Category doesn't exist");
        BrandForm brandForm1 = TestUtils.getBrandForm("levis","shirt");
        brandService.getByBrandCategory(brandForm1);
    }

    @Test
    public void getByBrandAndCategoryTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        BrandForm brandForm = TestUtils.getBrandForm("apple","electronics");
        BrandPojo actualPojoByBrand = brandService.getByBrandCategory(brandForm);
        assertEquals(expectedPojo.getId(), actualPojoByBrand.getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByBrand.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByBrand.getCategory());

        BrandForm brandForm1 = TestUtils.getBrandForm("apple","electronics");
        BrandPojo actualPojoByCategory = brandService.getByBrandCategory(brandForm1);
        assertEquals(expectedPojo.getId(), actualPojoByCategory.getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByCategory.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByCategory.getCategory());

        BrandForm brandForm2 = TestUtils.getBrandForm("app","");
        List<BrandPojo> actualPojoBySearch = brandService.searchBrandCategoryData(brandForm2);
        assertEquals(expectedPojo.getId(), actualPojoByCategory.getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByCategory.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByCategory.getCategory());
    }

    @Test
    public void updateBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo updatedBrandPojo = TestUtils.getBrandPojo("dell", "electronics");
        brandService.update(brandPojo.getId(), updatedBrandPojo);
        BrandPojo expectedBrandPojo = brandDao.selectByBrandCategory("dell", "electronics");
        BrandPojo actualBrandPojo = brandService.get(expectedBrandPojo.getId());
        assertEquals(expectedBrandPojo.getId(), actualBrandPojo.getId());
        assertEquals(expectedBrandPojo.getBrand(), actualBrandPojo.getBrand());
        assertEquals(expectedBrandPojo.getCategory(), actualBrandPojo.getCategory());
    }
}