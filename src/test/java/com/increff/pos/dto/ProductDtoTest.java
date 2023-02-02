package com.increff.pos.dto;

import com.increff.pos.model.*;
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

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private InventoryService inventoryService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUpBrands() throws ApiException {
        BrandPojo brandPojo1 = TestUtils.getBrandPojo("haldirams", "food");
        brandService.add(brandPojo1);
        BrandPojo brandPojo2 = TestUtils.getBrandPojo("apple", "electronics");
        brandService.add(brandPojo2);
    }

    @Test
    public void addProductTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Haldirams ", "  Food", 9.99);
        ProductPojo productPojo1 = productDto.add(productForm);
        ProductPojo productPojo = productService.getByBarcode("ba123456");
        assertEquals(productPojo.getName(), productPojo1.getName());
        assertEquals(productPojo.getBarcode(), productPojo1.getBarcode());
        assertEquals(productPojo.getMrp(), productPojo1.getMrp());
        assertEquals(productPojo.getBrandCategoryId(), productPojo1.getBrandCategoryId());
        assertEquals(productPojo.getId(), productPojo1.getId());
    }

    @Test
    public void addProductWithoutBrandTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Apple ", "  Food", 9.99);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand-Category doesn't exist");
        productDto.add(productForm);
    }

    @Test
    public void addProductWithDuplicateBarcodeTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        ProductForm newProductForm = TestUtils.getProductForm(
                "Iphone 7", "ba123456", " Apple ", "  Electronics", 79999.00);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode already exists");
        productDto.add(newProductForm);
    }

    @Test
    public void addProductNullFieldsTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                null, null, " Haldirams ", "  Electronics", 9.99);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Please Enter Name");
        productDto.add(productForm);
    }

    @Test
    public void addProductInvalidFieldsTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "bhujiya", "ba123", " Haldirams ", "  Food", -12.3);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Please Enter a positive mrp!");
        productDto.add(productForm);
    }

    @Test
    public void getAllProductTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("haldirams","food");
        BrandPojo brandPojo1 = brandService.getByBrandCategory(brandForm);
        ProductPojo productPojo1 = TestUtils.getProductPojo("bhujiya", "ba123456", brandPojo1.getId(), 9.99);
        productService.add(productPojo1,brandPojo1);
        BrandForm brandForm1 = TestUtils.getBrandForm("apple","electronics");
        BrandPojo brandPojo2 = brandService.getByBrandCategory(brandForm1);
        ProductPojo productPojo2 = TestUtils.getProductPojo("iphone 5", "ip123456", brandPojo2.getId(), 34999d);
        productService.add(productPojo2,brandPojo2);
        List<ProductPojo> productPojoList = productService.getAll();
        List<ProductData> productDataList = productDto.getAll();
        assertEquals(productPojoList.size(), productDataList.size());
    }

    @Test
    public void getProductByIdTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Haldirams ", "  Food", 9.99);
        ProductPojo productPojo1 = productDto.add(productForm);
        ProductPojo productPojo = productService.getByBarcode("ba123456");
        BrandPojo brandPojo = brandService.get(productPojo.getId());
        assertEquals(productPojo.getBrandCategoryId(), productPojo1.getBrandCategoryId());
        assertEquals(productPojo.getId(), productPojo1.getId());
        assertEquals(productPojo.getName(), productPojo1.getName());
        assertEquals(productPojo.getBarcode(), productPojo1.getBarcode());
        assertEquals(productPojo.getMrp(), productPojo1.getMrp());
    }

    @Test
    public void getProductByBarcodeTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("haldirams","food");
        BrandPojo brandPojo = brandService.getByBrandCategory(brandForm);
        ProductPojo pojo = TestUtils.getProductPojo("bhujiya", "ba123456", brandPojo.getId(), 9.99);
        productService.add(pojo,brandPojo);
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(0);
        inventoryPojo.setProductId(pojo.getId());
        inventoryService.add(inventoryPojo);
        ProductPojo productPojo = productService.getByBarcode("ba123456");
        ProductDetails productData = productDto.getByBarcode("ba123456");
        assertEquals(productPojo.getId(), productData.getId());
        assertEquals(productPojo.getName(), productData.getName());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(brandPojo.getBrand(), productData.getBrand());
        assertEquals(brandPojo.getCategory(), productData.getCategory());
        assertEquals(productPojo.getMrp(), productData.getMrp());
    }

    @Test
    public void getProductByInvalidIdTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given ID does not exist, id: 5");
        ProductData productData = productDto.get(5);
    }

    @Test
    public void getProductByInvalidBarcodeTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123456", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode doesn't exist");
        ProductData productData = productDto.getByBarcode("ab654321");
    }

    @Test
    public void updateProductTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("apple","electronics");
        BrandPojo brandPojo = brandService.getByBrandCategory(brandForm);
        ProductPojo productPojo = TestUtils.getProductPojo("iphone", "ip123456", brandPojo.getId(), 34999d);
        productService.add(productPojo,brandPojo);
        ProductForm productForm = TestUtils.getProductForm(" IPhone 5 ","IP123456", "Apple", "Electronics", 39999d);
        ProductPojo updatedData = productDto.update(productPojo.getId(), productForm);
        ProductPojo pojo = productService.get(productPojo.getId());
        assertEquals(pojo.getId(), updatedData.getId());
        assertEquals(pojo.getBarcode(), updatedData.getBarcode());
        assertEquals(pojo.getMrp(), updatedData.getMrp());
        assertEquals(brandPojo.getId(), updatedData.getBrandCategoryId());
    }

    @Test
    public void addProductFromFile() throws IOException {
        FileReader file = new FileReader("testFiles/product.tsv");
        UploadProgressData uploadProgressData = productDto.addProductFromFile(file);
        assertEquals((Integer) 2, uploadProgressData.getTotalCount());
        assertEquals((Integer) 0, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 1, uploadProgressData.getErrorCount());
    }
}