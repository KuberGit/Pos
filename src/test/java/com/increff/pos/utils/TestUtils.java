package com.increff.pos.utils;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.*;
import com.increff.pos.pojo.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtils {

    public static BrandForm getBrandForm(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static BrandPojo getBrandPojo(String brand, String category) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        return brandPojo;
    }

    public static ProductForm getProductForm(String name, String barcode, String brand, String category, Double price) {
        ProductForm productForm = new ProductForm();
        productForm.setName(name);
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setMrp(price);
        return productForm;
    }

    public static ProductSearchForm getProductSearchForm(String name, String barcode, String brand, String category) {
        ProductSearchForm productSearchForm = new ProductSearchForm();
        productSearchForm.setName(name);
        productSearchForm.setBarcode(barcode);
        productSearchForm.setBrand(brand);
        productSearchForm.setCategory(category);
        return productSearchForm;
    }

    public static ProductData getProductData(int id, String barcode) {
        ProductData productData = new ProductData();
        productData.setId(id);
        productData.setBarcode(barcode);
        return productData;
    }

    public static ProductPojo getProductPojo(String name, String barcode, Integer brandId, Double price) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setName(name);
        productPojo.setBarcode(barcode);
        productPojo.setBrandCategoryId(brandId);
        productPojo.setMrp(price);
        return productPojo;
    }

    public static InventoryForm getInventoryForm(String barcode, Integer quantity) {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public static InventorySearchForm getInventorySearchForm(String name, String barcode) {
        InventorySearchForm inventorySearchForm = new InventorySearchForm();
        inventorySearchForm.setBarcode(barcode);
        inventorySearchForm.setName(name);
        return inventorySearchForm;
    }

    public static InventoryPojo getInventoryPojo(Integer productId, Integer quantity) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

    public static OrderItemForm getOrderItemForm(String barcode, String name, Integer quantity, Double sellingPrice) {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode(barcode);
        orderItemForm.setName(name);
        orderItemForm.setQuantity(quantity);
        orderItemForm.setSellingPrice(sellingPrice);
        return orderItemForm;
    }

    public static OrderSearchForm getOrderSearchForm(int id, String orderUser, String startDate, String endDate) {
        OrderSearchForm orderSearchForm = new OrderSearchForm();
        orderSearchForm.setId(id);
        orderSearchForm.setOrderUser(orderUser);
        orderSearchForm.setStartDate(startDate);
        orderSearchForm.setEndDate(endDate);
        return orderSearchForm;
    }

    public static OrderPojo getOrderPojo(int id, Date datetime, int invoiceCreated) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setId(id);
        orderPojo.setDatetime(datetime);
        orderPojo.setInvoiceCreated(invoiceCreated);
        return orderPojo;
    }

    public static OrderItemPojo getOrderItemPojo(int id, int productId, int orderId, int quantity, double sellingPrice) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setId(id);
        orderItemPojo.setProductId(productId);
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setQuantity(quantity);
        orderItemPojo.setSellingPrice(sellingPrice);
        return orderItemPojo;
    }

    public static UserForm getUserForm(String email, String password) {
        UserForm userForm = new UserForm();
        userForm.setEmail(email);
        userForm.setPassword(password);
        return userForm;
    }

    public static OrderData getOrderData(Integer id, Timestamp date, Double bill) {
        OrderData orderData = new OrderData();
        orderData.setId(id);
        orderData.setDatetime(date);
        orderData.setBillAmount(bill);
        return orderData;
    }

    public static SalesReportForm getSalesReportForm(String brand,String category,String startDate,String endDate) {
        SalesReportForm salesReportForm = new SalesReportForm();
        salesReportForm.setStartdate(startDate);
        salesReportForm.setEnddate(endDate);
        salesReportForm.setBrand(brand);
        salesReportForm.setCategory(category);
        return salesReportForm;
    }

    public static List<OrderItemForm> getOrderItemForm(
            List<String> barcodes, List<Integer> quantities, List<Double> sellingPriceList) {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        for(int i = 0; i < barcodes.size(); i++) {
            OrderItemForm orderItemForm = new OrderItemForm();
            orderItemForm.setBarcode(barcodes.get(i));
            orderItemForm.setQuantity(quantities.get(i));
            orderItemForm.setSellingPrice(sellingPriceList.get(i));
            orderItemFormList.add(orderItemForm);
        }
        return orderItemFormList;
    }

    public static List<OrderItemData> getOrderItemDataList(
            List<Integer> orderIds, List<String> productNames, List<String> barcodes,
            List<Integer> quantities, List<Double> sellingPriceList) {
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(int i = 0; i < barcodes.size(); i++) {
            OrderItemData orderItemData = new OrderItemData();
            orderItemData.setOrderId(orderIds.get(i));
            orderItemData.setBarcode(barcodes.get(i));
            orderItemData.setQuantity(quantities.get(i));
            orderItemData.setName(productNames.get(i));
            orderItemData.setSellingPrice(sellingPriceList.get(i));
            orderItemDataList.add(orderItemData);
        }
        return orderItemDataList;
    }

    public static List<OrderItemPojo> getOrderItemPojoList(
            Integer orderId, List<ProductPojo> productPojoList, List<Integer> quantities, List<Double> sellingPriceList) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for(int i = 0; i < productPojoList.size(); i++) {
            OrderItemPojo orderItemPojo = new OrderItemPojo();
            orderItemPojo.setOrderId(orderId);
            orderItemPojo.setProductId(productPojoList.get(i).getId());
            orderItemPojo.setQuantity(quantities.get(i));
            orderItemPojo.setSellingPrice(sellingPriceList.get(i));
            orderItemPojoList.add(orderItemPojo);
        }
        return orderItemPojoList;
    }

    public static DailySalesPojo getDailySalesReportPojo(Date date, Double revenue, Integer items, Integer orders) {
        DailySalesPojo dailySalesReportPojo = new DailySalesPojo();
        dailySalesReportPojo.setDate(date);
        dailySalesReportPojo.setRevenue(revenue);
        dailySalesReportPojo.setItems(items);
        dailySalesReportPojo.setOrders(orders);
        return dailySalesReportPojo;
    }
}