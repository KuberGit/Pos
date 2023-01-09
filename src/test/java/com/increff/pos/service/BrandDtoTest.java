package com.increff.pos.service;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {
    @Autowired
    private BrandDto brandDto;

    @Test
    public void testAddBrand() throws ApiException {
        // All data should be stored in a small case, trimmed.
        BrandForm f = TestUtil.getBrandFormDto("aLtErNaTe","AlTeR");
        BrandPojo p = brandDto.addBrand(f);
        assertEquals("alternate",p.getBrand());
        assertEquals("alter",p.getCategory());
        f = TestUtil.getBrandFormDto("   aLtErNaTe ","   alTeR");
        assertEquals("alternate",p.getBrand());
        assertEquals("alter",p.getCategory());
    }

    @Test
    public void testGetBrandData() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("Te","Al");
        BrandPojo p = brandDto.addBrand(f);
        p = brandDto.getByBrandCategory(f);
        BrandData d = brandDto.getBrandData(p.getId());
        assertEquals("te",d.getBrand());
        assertEquals("al",d.getCategory());
    }

    @Test
    public void testGetAllBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("aLtErNaTe","AlTeR");
        BrandPojo p = brandDto.addBrand(f);
        f = TestUtil.getBrandFormDto("bLtErNaTe","BlTeR");
        p = brandDto.addBrand(f);
        f = TestUtil.getBrandFormDto("cLtErNaTe","ClTeR");
        p = brandDto.addBrand(f);
        f = TestUtil.getBrandFormDto("dLtErNaTe","DlTeR");
        p = brandDto.addBrand(f);
        f = TestUtil.getBrandFormDto("eLtErNaTe","ElTeR");
        p = brandDto.addBrand(f);
        List<BrandData> reqList = brandDto.getAllBrand();
        assertEquals(5,reqList.size());
    }

    @Test
    public void testUpdateBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("aLtErNaTe","AlTeR");
        BrandPojo p = brandDto.addBrand(f);
        p = brandDto.getByBrandCategory(f);
        f.setCategory("   BlteR");
        p = brandDto.updateBrand(p.getId(),f);
        assertEquals("alternate",p.getBrand());
        assertEquals("blter",p.getCategory());
    }

    @Test
    public void testGetByBrandCategory() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("aLtErNaTe","AlTeR");
        BrandPojo p = brandDto.addBrand(f);
        p = brandDto.getByBrandCategory(f);
        assertEquals("alternate",p.getBrand());
        assertEquals("alter",p.getCategory());
    }


}
