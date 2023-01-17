package com.increff.pos.dto;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportDto {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;

    public List<BrandForm> getBrandReport(BrandForm form){
        List<BrandPojo> list = brandService.searchBrandCategoryData(form);
        List<BrandForm> reqList = new ArrayList<BrandForm>();
        for(BrandPojo p:list){
            reqList.add(ConvertUtil.convertBrandPojotoBrandForm(p));
        }
        return reqList;
    }

    public List<InventoryReportData> getInventoryReport() throws ApiException {
        List<InventoryReportData> inventoryReportData = new ArrayList();
        List<BrandPojo> brandCategoryList = brandService.getAll();
        for (BrandPojo brandCategory : brandCategoryList) {
            InventoryReportData inventoryReportItemDataItem = new InventoryReportData();
            inventoryReportItemDataItem.setBrand(brandCategory.getBrand());
            inventoryReportItemDataItem.setCategory(brandCategory.getCategory());
            inventoryReportItemDataItem.setId(brandCategory.getId());
            int quantity = 0;
            List<ProductPojo> productList = productService.getProductByBrandCategoryId(brandCategory.getId());
            for (ProductPojo pojo : productList) {
                InventoryPojo inventoryPojo = inventoryService.get(pojo.getId());
                quantity+=inventoryPojo.getQuantity();
            }
            inventoryReportItemDataItem.setQuantity(quantity);
            inventoryReportData.add(inventoryReportItemDataItem);
        }
        return inventoryReportData;
    }

    public List<SalesReportData> getSalesReport(SalesReportForm salesReportForm) throws ParseException, ApiException {
        List<Integer> orderIds = getOrderIds(salesReportForm);
        BrandForm brandForm = ConvertUtil.convertSalesReportFormtoBrandForm(salesReportForm);
        List<BrandPojo> brandMasterPojoList = brandService.searchBrandCategoryData(brandForm);
        List<Integer> brandIds = brandMasterPojoList.stream().map(o -> o.getId()).collect(Collectors.toList());
        // filter using brandId list and map to product id list
        List<Integer> productIds = productService.getAll().stream()
                .filter(o -> (brandIds.contains(o.getBrandCategoryId()))).map(o -> o.getId())
                .collect(Collectors.toList());
        // filter using product and order id list
        List<OrderItemPojo> listOfOrderItemPojos = orderItemService.getAll().stream()
                .filter(o -> (productIds.contains(o.getProductId()) && orderIds.contains(o.getOrderId())))
                .collect(Collectors.toList());
        // map to sales report data
        List<SalesReportData> salesReportData = listOfOrderItemPojos.stream()
                .map(o -> ConvertUtil.convertToSalesReportData(o,
                        brandService.get(productService.get(o.getProductId()).getBrandCategoryId())))
                .collect(Collectors.toList());

        return reportService.groupSalesReportDataCategoryWise(salesReportData);
    }

    public List<Integer> getOrderIds(SalesReportForm salesReportForm) throws ParseException, ApiException {
        List<OrderPojo> orderPojo = orderService.getAll();
        // Get list of order ids
        if (salesReportForm.startdate.isEmpty() && salesReportForm.enddate.isEmpty()) {
            return orderPojo.stream().map(o -> o.getId()).collect(Collectors.toList());
        }
        return reportService.getOrderIdList(orderPojo, salesReportForm.startdate, salesReportForm.enddate);
    }
}
