package com.increff.pos.dto;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.utils.ConvertUtil;
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
    @Autowired
    private OrderService inventortService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("Nike ", "Shoes");
        brandDto.addBrand(brandForm);
        ProductForm productForm = TestUtils.getProductForm(
                "Runner", "NK123456", " Nike", "Shoes ", 13999.00);
        productDto.add(productForm);
        ProductDetails productDetails = productDto.getByBarcode("NK123456");
        ProductForm newProductForm = TestUtils.getProductForm(
                "Flyer", "NK654321", "Nike", "Shoes", 10999.99);
        productDto.add(newProductForm);
        ProductDetails productDetails1 = productDto.getByBarcode("NK654321");
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123456", 50);
        inventoryDto.updateInventory(productDetails.getId(),inventoryForm);
        InventoryForm newInventoryForm = TestUtils.getInventoryForm("NK654321", 30);
        inventoryDto.updateInventory(productDetails1.getId(),newInventoryForm);
    }

    @Test
    public void addOrderTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        OrderPojo orderPojo = orderDto.addOrder(orderItemFormList);
        assertEquals(0, orderPojo.getInvoiceCreated());
        assertEquals(new Date().toString(), orderPojo.getDatetime().toString());
    }

    @Test
    public void addOrderWithExcessQuantityTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456", "Runner",52, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Required number of 52 of NK123456 doesn't exists");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void addNonExistingBarcodeTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("KN456789","Runner", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Barcode doesn't exist");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void getAllOrdersTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[2];
        //List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 1, 13999.00);
        orderItemFormList[0] = (orderItemForm);
        orderItemForm = TestUtils.getOrderItemForm("NK654321","Flyer", 1, 10999.00);
        orderItemFormList[1] = (orderItemForm);
        orderDto.createOrder(orderItemFormList);
        List<OrderData> data = orderDto.getAll();
        assertEquals(1, data.size());
    }

    @Test
    public void getOrderByIdTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        OrderData data = orderDto.get(orderDto.getAll().get(0).getId());
        assertEquals(new Date().toString(), data.getDatetime().toString());
        assertEquals(13999.00, data.getBillAmount(),0.0);
        assertEquals(0, data.getIsInvoiceCreated());
    }

    @Test
    public void getOrderDetailsTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        OrderData orderData = orderDto.get(orderDto.getAll().get(0).getId());
        assertEquals(0, orderData.getIsInvoiceCreated());
        assertEquals(13999.00, orderData.getBillAmount(), 0.0);
        assertEquals(new Date().toString(), orderData.getDatetime().toString());
    }

    @Test
    public void updateOrderTest() throws ApiException {
        // Add initial order
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner",1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);

        // Update order
        List<OrderItemForm> updatedOrderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm updatedOrderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 5, 13999.00);
        updatedOrderItemFormList.add(updatedOrderItemForm);
        OrderItemForm newOrderItemForm = TestUtils.getOrderItemForm("NK654321","Flyer", 5, 10999.00);
        updatedOrderItemFormList.add(newOrderItemForm);
        OrderPojo updatedOrderPojo = orderDto.updateOrder(orderDto.getAll().get(0).getId(), updatedOrderItemFormList);

        // Assert
        assertEquals(new Date().toString(), updatedOrderPojo.getDatetime().toString());
        assertEquals(0, updatedOrderPojo.getInvoiceCreated());
    }

    @Test
    public void updateInvoiceStatusTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123456","Runner", 1, 13999.00);
        orderItemFormList[0] = orderItemForm;
        List<BillData> billDataList = orderDto.createOrder(orderItemFormList);
        orderDto.updateInvoice(billDataList.get(0).getId());
        OrderData orderData = orderDto.get(billDataList.get(0).getId());
        assertEquals(1, orderData.getIsInvoiceCreated());
    }

    @Test
    public void generateInvoiceTest() throws ApiException {
        OrderItemForm[] orderItemFormList = new OrderItemForm[1];
        orderItemFormList[0] = TestUtils.getOrderItemForm("nk123456","Runner",1,13999.00);
        List<BillData> billDataList = orderDto.generateInvoice(orderItemFormList);
        assertEquals(13999.00,billDataList.get(0).getMrp(),0.0);
        assertEquals(1,billDataList.get(0).getQuantity());
        assertEquals("Runner",billDataList.get(0).getName());
        assertEquals(1,billDataList.get(0).getId());
    }

    @Test
    public void changeOrderTest() throws ApiException {
        OrderItemForm[] orderItemForms = new OrderItemForm[1];
        orderItemForms[0] = TestUtils.getOrderItemForm("nk654321","Flyer",2,10999.00);
        List<BillData> billDataList = orderDto.createOrder(orderItemForms);
        OrderItemForm[] orderItemFormsNew = new OrderItemForm[1];
        orderItemFormsNew[0] = TestUtils.getOrderItemForm("nk123456","Runner",1,13999.00);
        List<BillData> billDataListNew = orderDto.changeOrder(orderDto.getAll().get(0).getId(),orderItemFormsNew);
        assertEquals(13999.00,billDataListNew.get(0).getMrp(),0.0);
        assertEquals("runner",billDataListNew.get(0).getName());
        assertEquals( 1,billDataListNew.get(0).getQuantity());
    }

}