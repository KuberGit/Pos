package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDto {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemDto orderItemDto;
    @Autowired
    private InfoData info;

    @Transactional(rollbackFor = ApiException.class)
    public List<BillData> createOrder(OrderItemForm[] orderItemForms) throws ApiException {
        List<OrderItemForm> orderItems = new LinkedList<OrderItemForm>(Arrays.asList(orderItemForms));
        OrderPojo orderPojo = addOrder(orderItems);
        List<OrderItemPojo> list = orderItems.stream().map(productPojo -> {
            try {
                return ConvertUtil.convertOrderItemFormToOrderItemPojo(productPojo,
                        orderPojo, productService.getByBarcode(productPojo.barcode));
            } catch (ApiException e) {
                e.printStackTrace();
                OrderItemPojo p = new OrderItemPojo();
                return p;
            }
        }).collect(Collectors.toList());
        orderService.updateInventory(list);
        orderItemService.add(list);
        return orderService.getBillData(list);
    }

    @Transactional
    public void updateInvoice(int id) throws ApiException {
        orderService.updateInvoice(id);
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<BillData> generateInvoice(OrderItemForm[] orderItemForms) throws ApiException {
        List<BillData> reqBill = new ArrayList<BillData>();
        int newId = 1;
        for(OrderItemForm p:orderItemForms) {
            BillData item = new BillData();
            item.setName(p.getName());
            item.setQuantity(p.getQuantity());
            item.setMrp(p.getSellingPrice());
            item.setId(newId);
            reqBill.add(item);
            newId++;
        }
        return reqBill;
    }

    public OrderPojo addOrder(List<OrderItemForm> orderItems) throws ApiException {
        orderService.checkAvailabilityInventory(orderItems);
        OrderPojo orderPojo = new OrderPojo();
        Date date = new Date();
        orderPojo.setDatetime(date);
        orderService.add(orderPojo);
        return orderPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<BillData> changeOrder(int id, OrderItemForm[] orderItemForms) throws ApiException {
        List<OrderItemData> orderItemDataList = orderItemDto.get(id);
        addInInventory(orderItemDataList);
        List<OrderItemForm> orderItems = new LinkedList<OrderItemForm>(Arrays.asList(orderItemForms));
        OrderPojo orderPojo = updateOrder(id, orderItems);
        List<OrderItemPojo> list = orderItems.stream().map(o -> {
            try {
                return ConvertUtil.convertOrderItemFormToOrderItemPojo(o,
                        orderPojo, productService.getByBarcode(o.barcode));
            } catch (ApiException e) {
                e.printStackTrace();
                OrderItemPojo p = new OrderItemPojo();
                return p;
            }
        }).collect(Collectors.toList());
        orderService.updateInventory(list);
        orderItemService.deleteByOrderId(id);
        orderItemService.add(list);
        return orderService.getBillData(list);
    }

    public OrderPojo updateOrder(int id, List<OrderItemForm> orderItems) throws ApiException {
        orderService.checkAvailabilityInventory(orderItems);
        OrderPojo orderPojo = new OrderPojo();
        Date date = new Date();
        orderPojo.setDatetime(date);
        orderService.update(id, orderPojo);
        orderPojo.setId(id);
        return orderPojo;
    }

    public void addInInventory(List<OrderItemData> orderItemDataList) throws ApiException {
        for (OrderItemData orderItemData : orderItemDataList) {
            ProductPojo productMasterPojo = productService.getByBarcode(orderItemData.barcode);
            InventoryPojo inventoryPojo = inventoryService.getByProductId(productMasterPojo);
            InventoryPojo inventoryPojoFinal = new InventoryPojo();
            inventoryPojoFinal.setQuantity(orderItemData.quantity + inventoryPojo.getQuantity());
            inventoryService.update(inventoryPojo.getId(), inventoryPojoFinal);
        }
    }

    public OrderData get(int id) throws ApiException {
        OrderPojo orderPojo = orderService.get(id);
        return ConvertUtil.convertOrderPojotoOrderData(orderPojo, orderItemService.getByOrderId(orderPojo.getId()));
    }

    public List<OrderData> getAll() {
        List<OrderPojo> list = orderService.getAll();
        // map OrderPojo to OrderData
        return list.stream()
                .map(orderPojo -> {
                    try {
                        return ConvertUtil.convertOrderPojotoOrderData(orderPojo, orderItemService.getByOrderId(orderPojo.getId()));
                    } catch (ApiException e) {
                        e.printStackTrace();
                        OrderData p = new OrderData();
                        return p;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<OrderData> searchOrder(OrderSearchForm form) throws ApiException, ParseException {
        List<OrderPojo> orderPojo = orderService.searchOrder(form);
        // create list by date range
        if (!form.getStartDate().isEmpty() && !form.getEndDate().isEmpty()) {
            orderPojo = orderService.getListSearch(orderPojo, form.getStartDate(), form.getEndDate());
        }
        if (form.getId() == 0) {
            return orderPojo.stream()
                    .map(o -> {
                        try {
                            return ConvertUtil.convertOrderPojotoOrderData(o, orderItemService.getByOrderId(o.getId()));
                        } catch (ApiException e) {
                            e.printStackTrace();
                            OrderData p = new OrderData();
                            return p;
                        }
                    })
                    .collect(Collectors.toList());
        }
        // filter using orderId
        orderPojo = orderPojo.stream().filter(o -> (form.getId() == o.getId())).collect(Collectors.toList());
        // map OrderPojo to OrderData
        return orderPojo.stream()
                .map(o -> {
                    try {
                        return ConvertUtil.convertOrderPojotoOrderData(o, orderItemService.getByOrderId(o.getId()));
                    } catch (ApiException e) {
                        e.printStackTrace();
                        OrderData p = new OrderData();
                        return p;
                    }
                })
                .collect(Collectors.toList());
    }

}