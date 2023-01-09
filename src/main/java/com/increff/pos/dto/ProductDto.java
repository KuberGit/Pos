package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private InventoryService inventoryService;


    @Transactional(rollbackFor = ApiException.class)
    public ProductPojo add(ProductForm f) throws ApiException {
        validateForm(f);
        BrandForm brandForm = ConvertUtil.convertProductFormtoBrandForm(f);
        BrandPojo b = brandDto.getByBrandCategory(brandForm);
        ProductPojo p = ConvertUtil.convertProductFormtoProductPojo(f,b);
        ProductPojo newP = productService.add(p,b);
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(0);
        inventoryPojo.setProductId(p.getId());
        inventoryService.add(inventoryPojo);
        return newP;
    }

    @Transactional
    public ProductData get(int id) throws ApiException {
        ProductPojo p = productService.get(id);
        BrandPojo b = brandService.get(p.getBrandCategoryId());
        return ConvertUtil.convertProductPojotoProductData(p,b);
    }

    @Transactional
    public List<ProductData> getAll(){
        List<ProductPojo> pList = productService.getAll();
        List<ProductData> reqList = new ArrayList<>();
        for(ProductPojo p:pList){
            ProductData d = ConvertUtil.convertProductPojotoProductData(p,brandService.get(p.getBrandCategoryId()));
            reqList.add(d);
        }
        return reqList;
    }

    public ProductDetails getByBarcode(String barcode) throws ApiException {
        ProductPojo productMasterPojo = productService.getByBarcode(barcode);
        BrandPojo brandMasterPojo = brandService.get(productMasterPojo.getBrandCategoryId());
        ProductData productData = ConvertUtil.convertProductPojotoProductData(productMasterPojo,
                brandMasterPojo);
        InventoryPojo inventoryPojo = inventoryService.getByProductId(productMasterPojo);
        return ConvertUtil.convertProductDatatoProductDetails(productData, inventoryPojo);
    }

    public List<ProductData> searchProduct(ProductSearchForm form) throws ApiException {
        BrandForm brandForm = ConvertUtil.convertProductSearchFormtoBrandForm(form);
        List<BrandPojo> brandMasterPojoList = brandService.searchBrandCategoryData(brandForm);
        List<Integer> brandIds = brandMasterPojoList.stream().map(o -> o.getId()).collect(Collectors.toList());
        List<ProductPojo> list = productService.searchProductData(form).stream()
                .filter(o -> (brandIds.contains(o.getBrandCategoryId()))).collect(Collectors.toList());
        return list.stream().map(
                        o -> ConvertUtil.convertProductPojotoProductData(o, brandService.get(o.getBrandCategoryId())))
                .collect(Collectors.toList());
    }

    public ProductPojo update(int id,ProductForm f) throws ApiException {
        validateForm(f);
        BrandForm brandForm = ConvertUtil.convertProductFormtoBrandForm(f);
        BrandPojo b = brandService.getByBrandCategory(brandForm);
        ProductPojo p = ConvertUtil.convertProductFormtoProductPojoU(f,b);
        return productService.update(id,p,b);
    }

    private void validateForm(ProductForm b) throws ApiException {
        if (b.getName() == null || b.getMrp() <= 0) {
            throw new ApiException("Please Enter Name and a positive mrp!");
        }
    }

    public UploadProgressData addProductFromFile(FileReader file) {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProductForm> formList = new CsvToBeanBuilder(file)
                    .withSeparator('\t')
                    .withType(ProductForm.class)
                    .build()
                    .parse();
            progress.setTotalCount(formList.size());
            for (ProductForm form : formList) {
                try {
                    add(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    String errorMsg = mapper.writeValueAsString(form) + " :: " + e.getMessage();
                    progress.getErrorMessages().add(errorMsg);
                    System.out.println(e);
                }
            }
            return progress;
        } catch (Exception e) {
            progress.setErrorCount(progress.getErrorCount() + 1);
            progress.getErrorMessages().add(e.getMessage());
            System.out.println(e);
        }
        return progress;
    }
}
