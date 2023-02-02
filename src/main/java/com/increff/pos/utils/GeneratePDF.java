package com.increff.pos.utils;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GeneratePDF {

    public static void createResponse(HttpServletResponse response, String base64Str) throws IOException {
        String pdfFileName = "output.pdf";
        response.reset();
        response.addHeader("Pragma", "public");
        response.addHeader("Cache-Control", "max-age=0");
        response.setHeader("Content-disposition", "attachment;filename=" + pdfFileName);
        response.setContentType("application/pdf");

        // avoid "byte shaving" by specifying precise length of transferred data
        byte[] decodedBytes = Base64.decodeBase64(base64Str);
        response.setContentLength(decodedBytes.length);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(decodedBytes);
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}