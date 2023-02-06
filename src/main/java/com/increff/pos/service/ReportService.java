package com.increff.pos.service;

import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportService {

    public List<InventoryReportData> groupDataForInventoryReport(List<InventoryReportData> list) {
        LinkedHashMap<List<String>, InventoryReportData> map = new LinkedHashMap<List<String>, InventoryReportData>();
        for (InventoryReportData inventoryReportData : list) {
            List<String> key = new ArrayList<String>(
                    Arrays.asList(inventoryReportData.brand, inventoryReportData.category));
            // check key already exists
            if (map.containsKey(key)) {
                // update existing one
                InventoryReportData in = map.get(key);
                in.quantity = in.quantity + inventoryReportData.quantity;
                map.put(key, in);
            } else {
                // create new one
                map.put(key, inventoryReportData);
            }
        }
        Collection<InventoryReportData> values = map.values();
        // convert hashmap to list
        List<InventoryReportData> inventoryReportDataList = new ArrayList<InventoryReportData>(values);
        return inventoryReportDataList;
    }

    public List<SalesReportData> groupSalesReportDataCategoryWise(List<SalesReportData> salesReportDatas) throws ApiException {
        LinkedHashMap<String, SalesReportData> m = new LinkedHashMap<String, SalesReportData>();
        for (SalesReportData salesReportData : salesReportDatas) {
            // check key already exists
            if (m.containsKey(salesReportData.category)) {
                // update existing one
                SalesReportData salesReportDataExisting = m.get(salesReportData.category);
                salesReportDataExisting.setQuantity(salesReportDataExisting.quantity + salesReportData.quantity);
                salesReportDataExisting.setRevenue(salesReportDataExisting.revenue + salesReportData.revenue);
                m.put(salesReportData.getCategory(), salesReportDataExisting);
            } else {
                // create new one
                m.put(salesReportData.category, salesReportData);
            }
        }
        Collection<SalesReportData> values = m.values();
        // convert hashmap to list
        List<SalesReportData> salesDataList = new ArrayList<SalesReportData>(values);
        if (salesDataList.isEmpty()) {
            throw new ApiException("There are no sales for given data");
        }
        return salesDataList;
    }

    public List<Integer> getOrderIdList(List<OrderPojo> o, String startdate, String enddate)
            throws ParseException, ApiException {
        List<Integer> orderIds = new ArrayList<Integer>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (OrderPojo p : o) {
            // Split datetime with space and get first element of array as date
            String receivedDate = sdf.format(p.getDatetime());
            // Compares date with startdate and enddate
            if ((sdf.parse(startdate).before(sdf.parse(receivedDate))
                    || sdf.parse(startdate).equals(sdf.parse(receivedDate)))
                    && (sdf.parse(receivedDate).before(sdf.parse(enddate))
                    || sdf.parse(receivedDate).equals(sdf.parse(enddate)))) {
                // Add id to array
                orderIds.add(p.getId());
            }
        }
        if (orderIds.size() == 0) {
            throw new ApiException("There are no orders for given dates");
        }
        return orderIds;
    }

}
