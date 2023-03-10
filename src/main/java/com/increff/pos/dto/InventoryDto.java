package com.increff.pos.dto;

import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.*;
import com.increff.pos.utils.ConvertUtil;
import com.increff.pos.utils.NormalizeUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;

@Component
public class InventoryDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    public InventoryPojo addInventory(InventoryForm form) throws ApiException {
        validateData(form);
        ProductPojo productPojo = productService.getByBarcode(form.getBarcode());
        InventoryPojo inventoryPojo = inventoryService.getByProduct(productPojo);
        inventoryPojo.setQuantity(form.quantity + inventoryPojo.getQuantity());
        return inventoryService.update(inventoryPojo.getId(), inventoryPojo);
    }

    public List<InventoryData> searchInventory(InventorySearchForm form) throws ApiException {
        ProductSearchForm productSearchForm = ConvertUtil.convertInventorySearchFormtoProductSearchForm(form);
        NormalizeUtil.normalizeProductSearchForm(productSearchForm);
        List<ProductPojo> productMasterPojoList = productService.searchProductData(productSearchForm);
        List<Integer> productIds = productMasterPojoList.stream().map(ProductPojo::getId).collect(Collectors.toList());
        // filter according to product id list
        List<InventoryPojo> list = inventoryService.getAll().stream()
                .filter(inventoryPojo -> (productIds.contains(inventoryPojo.getProductId()))).collect(Collectors.toList());
        // map InventoryPojo to InventoryData
        return list.stream()
                .map(inventoryPojo -> {
                    try {
                        return ConvertUtil.convertInventoryPojotoInventoryData(
                                inventoryPojo, productService.get(inventoryPojo.getProductId()));
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public InventoryData getInventoryData(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        ProductPojo productPojo = productService.get(inventoryPojo.getProductId());
        return ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo, productPojo);
    }

    public InventoryData getInventoryDataByBarcode(String barcode) throws ApiException {
        ProductPojo productMasterPojo = productService.getByBarcode(barcode);
        InventoryPojo inventoryPojo = inventoryService.get(productMasterPojo.getId());
        ProductPojo productPojo = productService.get(inventoryPojo.getProductId());
        return ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo, productPojo);
    }

    public InventoryPojo updateInventory(int id, InventoryForm form) throws ApiException {
        validateData(form);
        // get product
        ProductPojo productMasterPojo = productService.getByBarcode(form.getBarcode());
        InventoryPojo inventoryPojo = ConvertUtil.convertInventoryFormtoInventoryPojo(form, productMasterPojo);
        return inventoryService.update(id, inventoryPojo);
    }

    public List<InventoryData> getAllInventory() {
        List<InventoryPojo> list = inventoryService.getAll();
        // map InventoryPojo to InventoryData
        return list.stream()
                .map(inventoryPojo -> {
                    try {
                        return ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo, productService.get(inventoryPojo.getProductId()));
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void validateData(InventoryForm inventoryForm) throws ApiException {
        if (inventoryForm.quantity < 0) {
            throw new ApiException("Quantity can not be negative for product : " + inventoryForm.barcode + " !!");
        }
    }

    public UploadProgressData addInventoryFromFile(FileReader file) {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<InventoryForm> formList = new CsvToBeanBuilder(file)
                    .withSeparator('\t')
                    .withType(InventoryForm.class)
                    .build()
                    .parse();
            progress.setTotalCount(formList.size());
            for (InventoryForm form : formList) {
                try {
                    addInventory(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    String errorMsg = mapper.writeValueAsString(form) + " :: " + e.getMessage();
                    progress.getErrorMessages().add(errorMsg);
                }
            }
            return progress;
        } catch (Exception e) {
            progress.setErrorCount(progress.getErrorCount() + 1);
            progress.getErrorMessages().add(e.getMessage());
        }
        return progress;
    }

}