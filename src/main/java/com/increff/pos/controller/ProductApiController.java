package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductDetails;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api
@RestController
@RequestMapping(value = "/api/product")
public class ProductApiController {
    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Adds a Product")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ProductPojo add(@RequestBody ProductForm form) throws ApiException {
        return productDto.add(form);
    }

    @ApiOperation(value = "Search a Product")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public List<ProductData> search(@RequestBody ProductSearchForm form) throws ApiException {
        return productDto.searchProduct(form);
    }

    @ApiOperation(value = "Gets a Product")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable int id) throws ApiException {
        return productDto.get(id);
    }

    @ApiOperation(value = "Gets a Product")
    @RequestMapping(value = "/b/{barcode}", method = RequestMethod.GET)
    public ProductDetails getByBarcode(@PathVariable String barcode) throws ApiException {
        return productDto.getByBarcode(barcode);
    }

    @ApiOperation(value = "Gets list of all Products")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return productDto.getAll();
    }

    @ApiOperation(value = "Updates a Product")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ProductPojo update(@PathVariable int id, @RequestBody ProductForm form) throws ApiException {
        return productDto.update(id, form);
    }
}
