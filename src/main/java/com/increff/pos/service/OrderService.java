package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.model.BillData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderSearchForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderDao dao;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    @Transactional
    public void add(OrderPojo p){
        dao.insert(p);
    }

    @Transactional
    public OrderPojo get(int id) throws ApiException {
        OrderPojo p = dao.select(OrderPojo.class,id);
        if(p == null) {
            throw new ApiException("order with Id " + id + " doesn't exist");
        }
        return p;
    }

    @Transactional
    public List<OrderPojo> getAll(){
        return dao.selectAll();
    }


    public void checkAvailabilityInventory(List<OrderItemForm> o) throws ApiException {
        for(OrderItemForm i:o){
            int orderQuantity = i.getQuantity();
            ProductPojo p = productService.getByBarcode(i.getBarcode());
            InventoryPojo iP = inventoryService.getByProductId(p);
            if(orderQuantity > iP.getQuantity()){
                throw new ApiException("Required number of " + orderQuantity + " of " + i.getBarcode() + " doesn't exists");
            }
        }
    }

    @Transactional
    public void updateInvoice(int id) throws ApiException {
        OrderPojo p = get(id);
        p.setInvoiceCreated(1);
    }

    public void updateInventory(List<OrderItemPojo> l) throws ApiException {
        for(OrderItemPojo p:l){
            InventoryPojo iP = inventoryService.getByProductId(productService.get(p.getProductId()));
            int updatedQuantity = iP.getQuantity() - p.getQuantity();
            iP.setQuantity(updatedQuantity);
            inventoryService.update(iP.getId(),iP);
        }
    }

//     search between two ddtt
    public List<OrderPojo> getListSearch(List<OrderPojo> p , String start , String end) throws ParseException {
        List<OrderPojo> reqList = new ArrayList<OrderPojo>();

        for(OrderPojo o:p){
            SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
            Date todayWithZeroTime = formatter.parse(formatter.format(o.getDatetime()));
            String recDate = todayWithZeroTime.toString();
            if((formatter.parse(start).before(formatter.parse(recDate)) || formatter.parse(start).equals(formatter.parse(recDate))) && (formatter.parse(recDate).before(formatter.parse(end)) || formatter.parse(recDate).equals(formatter.parse(end)))){
                reqList.add(o);
            }
        }
        return reqList;
    }

    public List<BillData> getBillData(List<OrderItemPojo> op) throws ApiException {
        List<BillData> reqBill = new ArrayList<BillData>();
        int newId = 1;
        for (OrderItemPojo o : op) {
            ProductPojo p = productService.get(o.getProductId());
            BillData item = new BillData();
            item.name = p.getName();
            item.quantity = o.getQuantity();
            item.mrp = o.getSellingPrice();
            item.id = newId++;
            reqBill.add(item);
        }
        return reqBill;
    }


    @Transactional(rollbackFor = ApiException.class)
    public void update(int id, OrderPojo p) throws ApiException {
        OrderPojo newP = check(id);
        newP.setDatetime(p.getDatetime());
        dao.update(newP);
    }

    @Transactional
    public List<OrderPojo> searchOrder(OrderSearchForm f) {
        return dao.searchOrderData(f.getOrderUser());
    }

    @Transactional
    public OrderPojo check(int id) throws ApiException {
        OrderPojo p = dao.select(OrderPojo.class, id);
        if (p == null) {
            throw new ApiException("Order doesn't exist - id : " + id);
        }
        return p;
    }

    @Transactional
    public List<OrderPojo> getAllInTimeDuration(Date start, Date end) {
        return dao.selectAllInTimeDuration(start, end);
    }
}
