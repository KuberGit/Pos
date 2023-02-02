package com.increff.pos.dto;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.utils.TestUtils;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderDto orderDto;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("Nike ", "Shoes");
        brandDto.addBrand(brandForm);
        ProductForm productForm = TestUtils.getProductForm(
                "Runner", "NK123456", " Nike", "Shoes ", 13999.00);
        productDto.add(productForm);
        ProductForm newProductForm = TestUtils.getProductForm(
                "Flyer", "NK654321", "Nike", "Shoes", 10999.99);
        productDto.add(newProductForm);
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123456", 50);
        inventoryDto.updateInventory(1,inventoryForm);
        InventoryForm newInventoryForm = TestUtils.getInventoryForm("NK654321", 30);
        inventoryDto.updateInventory(2,newInventoryForm);
    }

    @Test
    public void addOrderTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        OrderPojo orderPojo = orderDto.addOrder(orderItemFormList);
        assertEquals(1, orderPojo.getId());
        assertEquals(0, orderPojo.getInvoiceCreated());
        assertEquals(new Date().toString(), orderPojo.getDatetime().toString());
    }

    @Test
    public void addOrderWithExcessQuantityTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 52, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Required number of 52 of NK123456 doesn't exists");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void addNonExistingBarcodeTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("KN456789", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode doesn't exist");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void getAllOrdersTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        orderItemForm = TestUtils.getOrderItemForm("NK654321", 1, 10999.00);
        orderItemFormList.add(orderItemForm);
        orderDto.addOrder(orderItemFormList);
        List<OrderData> data = orderDto.getAll();
        assertEquals(1, data.size());
    }

    @Test
    public void getOrderByIdTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        OrderData data = orderDto.get(billDataList.get(0).getId());
        assertEquals(1, data.getId());
        assertEquals(new Date().toString(), data.getDatetime().toString());
        assertEquals(13999.00, data.getBillAmount(),0.0);
        assertEquals(0, data.getIsInvoiceCreated());
    }

    @Test
    public void getOrderDetailsTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        OrderData orderData = orderDto.get(1);
        assertEquals(0, orderData.getIsInvoiceCreated());
        assertEquals(1, orderData.getId());
        assertEquals(13999.00, orderData.getBillAmount(), 0.0);
        assertEquals(new Date().toString(), orderData.getDatetime().toString());
    }

    @Test
    public void updateOrderTest() throws ApiException {
        // Add initial order
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);

        // Update order
        List<OrderItemForm> updatedOrderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm updatedOrderItemForm = TestUtils.getOrderItemForm("NK123456", 5, 13999.00);
        updatedOrderItemFormList.add(updatedOrderItemForm);
        OrderItemForm newOrderItemForm = TestUtils.getOrderItemForm("NK654321", 5, 10999.00);
        updatedOrderItemFormList.add(newOrderItemForm);
        OrderPojo updatedOrderPojo = orderDto.updateOrder(billDataList.get(0).getId(), updatedOrderItemFormList);

        // Assert
        assertEquals(1, updatedOrderPojo.getId());
        assertEquals(new Date().toString(), updatedOrderPojo.getDatetime().toString());
        assertEquals(0, updatedOrderPojo.getInvoiceCreated());
    }

    @Test
    public void updateInvoiceStatusTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        orderDto.updateInvoice(billDataList.get(0).getId());
        OrderData orderData = orderDto.get(billDataList.get(0).getId());
        assertEquals(1, orderData.getIsInvoiceCreated());
    }
}