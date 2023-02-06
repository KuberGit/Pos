package com.increff.invoice.controller;

import com.increff.invoice.model.BillData;
import com.increff.invoice.util.GeneratePDF;
import com.increff.invoice.util.GenerateXML;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@Api
@RestController
public class InvoiceApiController {

    @RequestMapping(value = "", consumes = "application/json")
    public String encodeOrderDetails(@RequestBody List<BillData> list) throws Exception {

        GenerateXML.createXml(list);
        byte[] encodedBytes = GeneratePDF.createPDF();
        String encodedStr = Base64.getEncoder().encodeToString(encodedBytes);
        return encodedStr;

    }
}