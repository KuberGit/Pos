package com.increff.pos.dto;

import com.increff.pos.model.InventoryData;
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

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventoryDtoTest extends AbstractUnitTest {

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
        ProductPojo productPojo = TestUtils.getProductPojo("runner", "nk123", brandPojo.getId(), 13999d);
        productService.add(productPojo,brandPojo);
        ProductPojo newProductPojo = TestUtils.getProductPojo("flyer", "nk321", brandPojo.getId(), 10999d);
        productService.add(newProductPojo,brandPojo);
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(productPojo.getId(), 0);
        inventoryService.add(inventoryPojo);
        InventoryPojo newInventoryPojo = TestUtils.getInventoryPojo(newProductPojo.getId(), 0);
        inventoryService.add(newInventoryPojo);
    }

    @Test
    public void getInventoryQuantityTest() throws ApiException {
        InventoryData inventoryData = inventoryDto.getInventoryDataByBarcode("NK123");
        assertEquals(0, inventoryData.getQuantity());
    }

    @Test
    public void getAllInventoryTest() throws ApiException {
        List<InventoryData> inventoryDataList = inventoryDto.getAllInventory();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertEquals(inventoryPojoList.size(), inventoryDataList.size());
    }

    @Test
    public void getInventoryByInvalidBarcodeTest() throws ApiException {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode doesn't exist");
        InventoryData inventoryData = inventoryDto.getInventoryDataByBarcode("NK012");
    }

    @Test
    public void updateInventoryTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123",50);
        InventoryPojo inventoryPojo = inventoryDto.updateInventory(1,inventoryForm);
        ProductPojo productPojo = productService.getByBarcode("nk123");
        InventoryPojo pojo = inventoryService.getByProductId(productPojo);
        assertEquals(productPojo.getId(), inventoryPojo.getProductId());
        assertEquals(pojo.getQuantity(), inventoryPojo.getQuantity());
    }

    @Test
    public void updateInventoryNegativeQuantityTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123",-20);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Quantity can not be negative for product : NK123 !!");
        InventoryPojo inventoryPojo = inventoryDto.updateInventory(1,inventoryForm);
    }

    @Test
    public void addBrandFromFile() throws IOException {
        FileReader file = new FileReader("testFiles/inventory.tsv");
        UploadProgressData uploadProgressData = inventoryDto.addInventoryFromFile(file);
        assertEquals((Integer) 2, uploadProgressData.getTotalCount());
        assertEquals((Integer) 0, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 2, uploadProgressData.getErrorCount());
    }
}