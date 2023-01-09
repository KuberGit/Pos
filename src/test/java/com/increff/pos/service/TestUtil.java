package com.increff.pos.service;

import com.increff.pos.model.*;

public class TestUtil {

    public static BrandForm getBrandFormDto(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static InventoryForm getInventoryFormDto(String barcode, int quantity) {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public static InventorySearchForm getInventorySearchFormDto(String barcode, String name) {
        InventorySearchForm inventorySearchForm = new InventorySearchForm();
        inventorySearchForm.setBarcode(barcode);
        inventorySearchForm.setName(name);
        return inventorySearchForm;
    }

    public static ProductSearchForm getProductSearchFormDto(String barcode, String brand, String category,
                                                            String name) {
        ProductSearchForm productSearchForm = new ProductSearchForm();
        productSearchForm.setBarcode(barcode);
        productSearchForm.setBrand(brand);
        productSearchForm.setCategory(category);
        productSearchForm.setName(name);
        return productSearchForm;
    }

    public static ProductForm getProductFormDto(String brand, String category, String name, double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }
}
