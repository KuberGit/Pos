package com.increff.pos.utils;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.model.*;
import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
import com.increff.pos.utils.ConvertUtil;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConversionUtilTest {

    @Test
    public void convertBrandPojoToBrandDataTest() {
        BrandPojo brandPojo = TestUtils.getBrandPojo(" apple ", " Electronics ");
        brandPojo.setId(1);
        BrandData brandData = ConvertUtil.convertBrandPojotoBrandData(brandPojo);
        assertEquals(brandPojo.getBrand(), brandData.getBrand());
        assertEquals(brandPojo.getCategory(), brandData.getCategory());
        assertEquals(brandPojo.getId(), brandData.getId());
    }

    @Test
    public void convertBrandFormToBrandPojoTest() {
        BrandForm brandForm = TestUtils.getBrandForm(" apple ", " Electronics ");
        BrandPojo brandPojo = ConvertUtil.convertBrandFormtoBrandPojo(brandForm);
        assertEquals(brandForm.getBrand(), brandPojo.getBrand());
        assertEquals(brandForm.getCategory(), brandPojo.getCategory());
    }

    @Test
    public void convertProductFormToProductPojoTest() {
        ProductForm productForm = TestUtils.getProductForm("milky bar","da123","amul","dairy", 10.0);
        BrandPojo brandPojo = TestUtils.getBrandPojo("amul","dairy");
        ProductPojo productPojo = ConvertUtil.convertProductFormtoProductPojo(productForm,brandPojo);
        assertEquals(productForm.getName(), productPojo.getName());
        assertEquals(productForm.getMrp(), productPojo.getMrp());
        assertEquals(productForm.getBarcode(), productPojo.getBarcode());
        assertEquals(brandPojo.getId(), productPojo.getBrandCategoryId());
    }

    @Test
    public void convertProductPojoToProductDataTest() {
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple","electronics");
        ProductData productData = ConvertUtil.convertProductPojotoProductData(productPojo, brandPojo);
        assertEquals(productPojo.getName(), productData.getName());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(productPojo.getMrp(), productData.getMrp());
        assertEquals("apple", productData.getBrand());
        assertEquals("electronics", productData.getCategory());
    }

    @Test
    public void convertInventoryPojoToInventoryDataTest() {
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(1,50);
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        InventoryData inventoryData = ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo, productPojo);
        assertEquals(productPojo.getName(), inventoryData.getName());
        assertEquals(productPojo.getBarcode(), inventoryData.getBarcode());
        assertEquals(inventoryPojo.getQuantity(), inventoryData.getQuantity());
    }

    @Test
    public void convertInventoryFormToInventoryPojoTest() {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("ipx123",50);
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        InventoryPojo inventoryPojo = ConvertUtil.convertInventoryFormtoInventoryPojo(inventoryForm,productPojo);
        assertEquals(0, inventoryPojo.getId());
        assertEquals(inventoryForm.getQuantity(), inventoryPojo.getQuantity());
    }

    @Test
    public void convertOrderItemPojoToOrderItemDataTest() {
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        List<ProductPojo> products = Arrays.asList(productPojo);
        List<Integer>quantities = Arrays.asList(6);
        List<Double>sellingPrices = Arrays.asList(52.0);
        List<OrderItemPojo> orderItemPojos = TestUtils.getOrderItemPojoList(1,products,quantities,sellingPrices);
        OrderItemData orderItemData = ConvertUtil.convertOrderItemPojotoOrderItemData(orderItemPojos.get(0), productPojo);
        assertEquals(orderItemPojos.get(0).getQuantity(), orderItemData.getQuantity());
        assertEquals(orderItemPojos.get(0).getSellingPrice(), orderItemData.getSellingPrice(),0.0);
        assertEquals("ipx123", orderItemData.getBarcode());
        assertEquals("iphone", orderItemData.getName());
    }

    @Test
    public void convertOrderItemFormToOrderItemPojo() {
        List<String>barcodes = Arrays.asList("am111");
        List<Integer>quantities = Arrays.asList(6);
        List<Double>sellingPrices = Arrays.asList(52.0);
        List<OrderItemForm> orderItemFormList = TestUtils.getOrderItemForm(barcodes,quantities,sellingPrices);
        OrderPojo orderPojo = TestUtils.getOrderPojo(1,new Date(),1);
        ProductPojo productPojo = TestUtils.getProductPojo("amul","am111",1,52.0);
        OrderItemPojo orderItemPojo = ConvertUtil.convertOrderItemFormToOrderItemPojo(orderItemFormList.get(0),orderPojo,productPojo);
        assertEquals(orderItemFormList.get(0).getQuantity(), orderItemPojo.getQuantity());
        assertEquals(orderItemFormList.get(0).getSellingPrice(), orderItemPojo.getSellingPrice(),0.0);
        assertEquals( 1, orderItemPojo.getOrderId());
        assertEquals( 0,orderItemPojo.getProductId());
    }

    @Test
    public void convertFormToPojoTest() {
        UserForm userForm = TestUtils.getUserForm("xyz@xyz.com","xyz");
        UserPojo pojo = ConvertUtil.convertFormToPojo(userForm);
        assertEquals(userForm.getEmail(), pojo.getEmail());
        assertEquals(userForm.getPassword(), pojo.getPassword());
    }

    @Test
    public void convertPojoToUserData() {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail("xyz@xyz.com");
        userPojo.setId(1);
        userPojo.setRole("supervisor");
        UserData userData = ConvertUtil.convertPojoToData(userPojo);
        assertEquals(userPojo.getEmail(), userData.getEmail());
        assertEquals(userPojo.getRole(), userData.getRole());
        assertEquals(1, userData.getId());
    }

    @Test
    public void convertBrandPojoToBrandFormTest() {
        BrandPojo brandPojo = TestUtils.getBrandPojo(" apple ", " Electronics ");
        BrandForm brandForm = ConvertUtil.convertBrandPojotoBrandForm(brandPojo);
        assertEquals(brandPojo.getBrand(), brandForm.getBrand());
        assertEquals(brandPojo.getCategory(), brandForm.getCategory());
    }

    @Test
    public void convertProductFormToBrandFormTest() {
        ProductForm productForm = TestUtils.getProductForm("milky bar","da123","amul","dairy", 10.0);
        BrandForm brandForm = ConvertUtil.convertProductFormtoBrandForm(productForm);
        assertEquals(productForm.getBrand(), brandForm.getBrand());
        assertEquals(productForm.getCategory(), brandForm.getCategory());
    }

    @Test
    public void convertProductFormToProductPojoUTest() {
        ProductForm productForm = TestUtils.getProductForm("milky bar","da123","amul","dairy", 10.0);
        BrandPojo brandPojo = TestUtils.getBrandPojo("amul","dairy");
        brandPojo.setId(1);
        ProductPojo productPojo = ConvertUtil.convertProductFormtoProductPojoU(productForm,brandPojo);
        assertEquals(1, productPojo.getBrandCategoryId());
        assertEquals(productForm.getName(), productPojo.getName());
        assertEquals(productForm.getMrp(), productPojo.getMrp());
    }

    @Test
    public void convertInventorySearchFormToProductSearchFormTest() {
        InventorySearchForm inventorySearchForm = TestUtils.getInventorySearchForm("milky bar","da123");
        ProductSearchForm productSearchForm = ConvertUtil.convertInventorySearchFormtoProductSearchForm(inventorySearchForm);
        assertEquals(productSearchForm.getName(),inventorySearchForm.getName());
        assertEquals(productSearchForm.getBarcode(),inventorySearchForm.getBarcode());
    }

    @Test
    public void convertProductSearchFormToBrandFormTest() {
        ProductSearchForm productSearchForm = TestUtils.getProductSearchForm("milky bar", "da123","amul","dairy");
        BrandForm brandForm = ConvertUtil.convertProductSearchFormtoBrandForm(productSearchForm);
        assertEquals(productSearchForm.getBrand(),brandForm.getBrand());
        assertEquals(productSearchForm.getCategory(),brandForm.getCategory());
    }

    @Test
    public void convertSalesReportFormToBrandFormTest() {
        SalesReportForm salesReportForm = TestUtils.getSalesReportForm("amul","dairy","","");
        BrandForm brandForm = ConvertUtil.convertSalesReportFormtoBrandForm(salesReportForm);
        assertEquals(brandForm.getBrand(),brandForm.getBrand());
        assertEquals(brandForm.getCategory(),brandForm.getCategory());
    }

    @Test
    public void convertToSalesReportDataTest() {
        OrderItemPojo orderItemPojo = TestUtils.getOrderItemPojo(1,1,1,1,52.00);
        BrandPojo brandPojo = TestUtils.getBrandPojo("amul","dairy");
        SalesReportData salesReportData = ConvertUtil.convertToSalesReportData(orderItemPojo,brandPojo);
        assertEquals(salesReportData.getBrand(),brandPojo.getBrand());
        assertEquals(salesReportData.getCategory(),brandPojo.getCategory());
        assertEquals(salesReportData.getQuantity(),orderItemPojo.getQuantity());
        assertEquals(salesReportData.getRevenue(),orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice(),0.0);
    }

    @Test
    public void convertProductDataToProductDetailsTest() {
        ProductData productData = TestUtils.getProductData(1,"da123456");
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(1,5);
        ProductDetails productDetails = ConvertUtil.convertProductDatatoProductDetails(productData,inventoryPojo);
        assertEquals(productDetails.getBarcode(),productData.getBarcode());
        assertEquals(productDetails.getBrand(),productData.getBrand());
        assertEquals(productDetails.getCategory(),productData.getCategory());
        assertEquals(productDetails.getMrp(),productData.getMrp());
        assertEquals(productDetails.getName(),productData.getName());
        assertEquals(productDetails.getId(),productData.getId());
    }

}