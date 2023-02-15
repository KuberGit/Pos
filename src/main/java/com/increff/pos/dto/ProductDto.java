package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.utils.ConvertUtil;
import com.increff.pos.utils.NormalizeUtil;
import com.increff.pos.utils.StringUtil;
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

    @Autowired
    private ProductDao dao;


    @Transactional(rollbackFor = ApiException.class)
    public ProductPojo add(ProductForm f) throws ApiException {
        validateForm(f);
        BrandForm brandForm = ConvertUtil.convertProductFormtoBrandForm(f);
        NormalizeUtil.normalizeBrandForm(brandForm);
        BrandPojo b = brandDto.getByBrandCategory(brandForm);
        ProductPojo p = ConvertUtil.convertProductFormtoProductPojo(f,b);
        NormalizeUtil.normalizeProductPojo(p);
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
    public List<ProductData> getAll() throws ApiException {
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
        InventoryPojo inventoryPojo = inventoryService.getByProduct(productMasterPojo);
        return ConvertUtil.convertProductDatatoProductDetails(productData, inventoryPojo);
    }

    public List<ProductData> searchProduct(ProductSearchForm form) throws ApiException {
        BrandForm brandForm = ConvertUtil.convertProductSearchFormtoBrandForm(form);
        NormalizeUtil.normalizeBrandForm(brandForm);
        List<BrandPojo> brandMasterPojoList = brandService.searchBrandCategoryData(brandForm);
        List<Integer> brandIds = brandMasterPojoList.stream().map(brandPojo -> brandPojo.getId()).collect(Collectors.toList());
        NormalizeUtil.normalizeProductSearchForm(form);
        List<ProductPojo> list = productService.searchProductData(form).stream()
                .filter(productPojo -> (brandIds.contains(productPojo.getBrandCategoryId()))).collect(Collectors.toList());
        return list.stream().map(
                        productPojo -> {
                            try {
                                return ConvertUtil.convertProductPojotoProductData(productPojo, brandService.get(productPojo.getBrandCategoryId()));
                            } catch (ApiException e) {
                                throw new RuntimeException(e);
                            }
                        })
                .collect(Collectors.toList());
    }

    public ProductPojo update(int id,ProductForm f) throws ApiException {
        validateFormUpdate(id,f);
        BrandForm brandForm = ConvertUtil.convertProductFormtoBrandForm(f);
        NormalizeUtil.normalizeBrandForm(brandForm);
        BrandPojo b = brandService.getByBrandCategory(brandForm);
        ProductPojo p = ConvertUtil.convertProductFormtoProductPojoU(f,b);
        NormalizeUtil.normalizeProductPojo(p);
        return productService.update(id,p,b);
    }

    private void validateForm(ProductForm b) throws ApiException {
        if (b.getName() == null) {
            throw new ApiException("Please Enter Name");
        }
        if (b.getMrp() <= 0) {
            throw new ApiException("Please Enter a positive mrp!");
        }
        if (b.getBarcode().length() < 8) {
            throw new ApiException("Please Enter a valid barcode");
        }
        if(!isValidBarcode(b.getBarcode())) {
            throw new ApiException("Please Enter a valid barcode!");
        }
        String barcode = StringUtil.toLowerCase(b.getBarcode());
        ProductPojo p = dao.selectByBarcode(barcode);
        if(p != null){
            throw new ApiException("Barcode already exists");
        }
    }

    private void validateFormUpdate(int id, ProductForm b) throws ApiException {
        ProductDetails p = getByBarcode(b.getBarcode());
        if(p != null) {
            if(p.getId()!=id) {
                throw new ApiException("Barcode already exists");
            }
        }
        if (b.getName() == null) {
            throw new ApiException("Please Enter Name");
        }
        if (b.getMrp() <= 0) {
            throw new ApiException("Please Enter a positive mrp!");
        }
        if (b.getBarcode().length() < 8 || !isValidBarcode(b.getBarcode())) {
            throw new ApiException("Please Enter a valid barcode");
        }
    }

    public UploadProgressData addProductFromFile(FileReader file) throws ApiException {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProductForm> formList = new CsvToBeanBuilder(file).withSeparator('\t').withType(ProductForm.class).build().parse();
            progress.setTotalCount(formList.size());
            for (ProductForm form : formList) {
                try {
                    add(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    progress.getErrorMessages().add(mapper.writeValueAsString(form) + " :: " + e.getMessage());
                }
            }
            return progress;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public static boolean isValidBarcode(String barcode) {
        return barcode.trim().matches("\\w+");
    }
}
