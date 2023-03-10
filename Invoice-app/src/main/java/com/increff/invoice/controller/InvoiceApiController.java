package com.increff.invoice.controller;

import com.increff.invoice.model.BillData;
import com.increff.invoice.util.GeneratePDF;
import com.increff.invoice.util.GenerateXML;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@Api
@RestController
public class InvoiceApiController {

    @RequestMapping(value = "/{id}", consumes = "application/json")
    public String encodeOrderDetails(@PathVariable int id, @RequestBody List<BillData> list) throws Exception {
        GenerateXML.createXml(list,id);
        byte[] encodedBytes = GeneratePDF.createPDF();
        String encodedStr = Base64.getEncoder().encodeToString(encodedBytes);
        return encodedStr;
    }
}