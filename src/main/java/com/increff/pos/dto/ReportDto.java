package com.increff.pos.dto;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    @Autowired
    private DailySalesService dailySalesService;

    public List<BrandForm> getBrandReport(BrandForm form){
        NormalizeUtil.normalizeBrandForm(form);
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

    private String convertDate(String dateYYYYMMDD)
    {
        if(dateYYYYMMDD == "") return "";
        String dateArr[] = dateYYYYMMDD.split("-");
        String dateDDMMYYYY = dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0];
        return dateDDMMYYYY;
    }
    public List<SalesReportData> getSalesReport(SalesReportForm salesReportForm) throws ParseException, ApiException {
        salesReportForm.startdate = convertDate(salesReportForm.getStartdate());
        salesReportForm.enddate =convertDate(salesReportForm.getEnddate());
                List<Integer> orderIds = getOrderIds(salesReportForm);
        BrandForm brandForm = ConvertUtil.convertSalesReportFormtoBrandForm(salesReportForm);
        NormalizeUtil.normalizeBrandForm(brandForm);
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

    @Transactional(rollbackOn = ApiException.class)
    public List<DailySalesPojo> getDailySales() throws ApiException {
        return dailySalesService.getAll();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional(rollbackOn = ApiException.class)
    public void saveDailySales() throws ApiException {
        DailySalesPojo p = new DailySalesPojo();
        Date today = new Date();
        Date start = getStartOfDay(today, Calendar.getInstance());
        Date end = getEndOfDay(today, Calendar.getInstance());
        List<OrderPojo> orders = orderService.getAllInTimeDuration(start, end);
        p.setOrders(orders.size());
        p.setDate(today);
        System.out.println(today);
        double totalRevenue = 0;
        int totalItems = 0;
        for(OrderPojo order:orders) {
            List<OrderItemPojo> items = orderItemService.getByOrderId(order.getId());
            totalItems+=items.size();
            for(OrderItemPojo item:items) {
                totalRevenue += item.getSellingPrice()*item.getQuantity();
            }
        }
        p.setItems(totalItems);
        p.setRevenue(totalRevenue);

        dailySalesService.add(p);
    }

    public static Date getStartOfDay(Date day,Calendar cal) {
        if (day == null) day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE,      cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND,      cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public static Date getEndOfDay(Date day,Calendar cal) {
        if (day == null) day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE,      cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND,      cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }
}
