package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.UploadProgressData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void addBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm(" Apple ", "  ElecTronIcs");
        BrandPojo brandPojo = brandDto.addBrand(brandForm);
        BrandPojo brandData = brandService.get(brandPojo.getId());
        assertEquals(brandPojo.getId(), brandData.getId());
        assertEquals(brandPojo.getBrand(), brandData.getBrand());
        assertEquals(brandPojo.getCategory(), brandData.getCategory());
    }

    @Test
    public void searchBrandDataTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm(" Apple ", "  ElecTronIcs");
        BrandPojo brandPojo1 = brandDto.addBrand(brandForm);
        List<BrandData> brandDataList = brandDto.searchBrandData(brandForm);
        BrandPojo brandPojo = brandService.getByBrandCategory(brandForm);
        assertEquals(brandPojo.getId(), brandDataList.get(0).getId());
        assertEquals(brandPojo.getBrand(), brandDataList.get(0).getBrand());
        assertEquals(brandPojo.getCategory(), brandDataList.get(0).getCategory());
    }

    @Test
    public void addDuplicateBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("Apple", "ElecTronIcs");
        BrandForm newBrandForm = TestUtils.getBrandForm(" Apple", "electronics");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand and Category already exists");
        brandDto.addBrand(brandForm);
        brandDto.addBrand(newBrandForm); // This should throw the exception
    }

    @Test
    public void addNullBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm(null, null);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("No brand and/or category provided");
        brandDto.addBrand(brandForm);
    }

    @Test
    public void getAllBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("apple", "electronics");
        BrandForm newBrandForm = TestUtils.getBrandForm("nike", "shoes");
        BrandPojo brandPojo = brandDto.addBrand(brandForm);
        BrandPojo newBrandPojo = brandDto.addBrand(newBrandForm);
        BrandPojo brandPojoService = brandService.getByBrandCategory(brandForm);
        BrandPojo newBrandPojoService = brandService.getByBrandCategory(brandForm);
        List<BrandData> brandDataList = brandDto.getAllBrand();
        assertEquals(2, brandDataList.size());
    }


    @Test
    public void getBrandByInvalidIdTest() throws ApiException {
        BrandForm BrandForm = TestUtils.getBrandForm("nike", "shoes");
        brandDto.addBrand(BrandForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand and Category with given ID does not exist - id: 10");
        brandDto.getBrandData(10);
    }

    @Test
    public void updateBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("ApPlE", "Electronics");
        BrandPojo brandPojo = brandDto.addBrand(brandForm);
        BrandForm updatedBrandForm = TestUtils.getBrandForm("Dell", "electronicS ");
        brandDto.updateBrand(brandPojo.getId(), updatedBrandForm);
        BrandData updatedBrandData = brandDto.getBrandData(brandPojo.getId());
        assertEquals("dell", updatedBrandData.getBrand());
        assertEquals("electronics", updatedBrandData.getCategory());
    }

    @Test
    public void addBrandFromFileTest() throws IOException, ApiException {
        FileReader file = new FileReader("testfiles/brand.tsv");
        UploadProgressData uploadProgressData = brandDto.addBrandCategoryFromFile(file);
        assertEquals((Integer) 13, uploadProgressData.getTotalCount());
        assertEquals((Integer) 13, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 0, uploadProgressData.getErrorCount());
    }
}