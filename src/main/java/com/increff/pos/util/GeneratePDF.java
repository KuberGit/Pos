package com.increff.pos.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.fop.apps.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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