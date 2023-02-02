package com.increff.pos.dto;

import com.increff.pos.model.OrderItemData;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import com.increff.pos.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemDto {
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;

    public List<OrderItemData> get(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getByOrderId(orderId);
        // map OrderItemPojo to OrderItemData
        return orderItemPojoList.stream()
                .map(o -> {
                    try {
                        return ConvertUtil.convertOrderItemPojotoOrderItemData(o, productService.get(o.getProductId()));
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

}