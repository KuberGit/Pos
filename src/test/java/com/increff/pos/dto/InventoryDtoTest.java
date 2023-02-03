package com.increff.pos.dto;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventorySearchForm;
import com.increff.pos.model.UploadProgressData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InventoryDtoTest extends AbstractUnitTest {

    private ProductPojo productPojo;

    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("nike", "shoes");
        brandService.add(brandPojo);
        productPojo = TestUtils.getProductPojo("runner", "nk123456", brandPojo.getId(), 13999d);
        productService.add(productPojo,brandPojo);
        ProductPojo newProductPojo = TestUtils.getProductPojo("flyer", "nk654321", brandPojo.getId(), 10999d);
        productService.add(newProductPojo,brandPojo);
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(productPojo.getId(), 0);
        inventoryService.add(inventoryPojo);
        InventoryPojo newInventoryPojo = TestUtils.getInventoryPojo(newProductPojo.getId(), 0);
        inventoryService.add(newInventoryPojo);
    }

    @Test
    @Rollback
    public void getInventoryDataTest() throws ApiException {
       InventoryData inventoryData = inventoryDto.getInventoryData(productPojo.getId());
        assertNotNull(inventoryData.getId());
        assertEquals(0, inventoryData.getQuantity());
        assertEquals("runner", inventoryData.getName());
        assertEquals("nk123456", inventoryData.getBarcode());
    }

    @Test
    @Rollback
    public void searchInventoryTest() throws ApiException {
        InventorySearchForm inventorySearchForm = TestUtils.getInventorySearchForm("runner","nk123456");
        List<InventoryData> inventoryData = inventoryDto.searchInventory(inventorySearchForm);
        assertEquals(0, inventoryData.get(0).getQuantity());
        assertEquals("runner", inventoryData.get(0).getName());
        assertEquals("nk123456", inventoryData.get(0).getBarcode());
//        assertEquals((Integer) 1, inventoryData.get(0).getId());
    }

    @Test
    @Rollback
    public void getInventoryQuantityTest() throws ApiException {
        InventoryData inventoryData = inventoryDto.getInventoryDataByBarcode("nk123456");
        assertEquals(0, inventoryData.getQuantity());
    }

    @Test
    @Rollback
    public void getAllInventoryTest() throws ApiException {
        List<InventoryData> inventoryDataList = inventoryDto.getAllInventory();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertEquals(inventoryPojoList.size(), inventoryDataList.size());
    }

    @Test
    @Rollback
    public void getInventoryByInvalidBarcodeTest() throws ApiException {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode doesn't exist");
        InventoryData inventoryData = inventoryDto.getInventoryDataByBarcode("nk012345");
    }

    @Test
    @Rollback
    public void updateInventoryTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("nk123456",50);
        InventoryPojo inventoryPojo = inventoryDto.updateInventory(productPojo.getId(),inventoryForm);
        ProductPojo productPojo = productService.getByBarcode("nk123456");
        InventoryPojo pojo = inventoryService.getByProductId(productPojo);
        assertEquals(productPojo.getId(), inventoryPojo.getProductId());
        assertEquals(pojo.getQuantity(), inventoryPojo.getQuantity());
    }

    @Test
    @Rollback
    public void updateInventoryNegativeQuantityTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("nk123456",-20);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Quantity can not be negative for product : nk123456 !!");
        InventoryPojo inventoryPojo = inventoryDto.updateInventory(1,inventoryForm);
    }

    @Test
    @Rollback
    public void addBrandFromFile() throws IOException {
        FileReader file = new FileReader("testFiles/inventory.tsv");
        UploadProgressData uploadProgressData = inventoryDto.addInventoryFromFile(file);
        assertEquals((Integer) 2, uploadProgressData.getTotalCount());
        assertEquals((Integer) 0, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 2, uploadProgressData.getErrorCount());
    }
}