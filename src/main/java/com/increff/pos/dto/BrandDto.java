package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.UploadProgressData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Setter
public class BrandDto {
    @Autowired
    private BrandService brandService;


    public BrandPojo addBrand(BrandForm f) throws ApiException {
        checkForm(f);
        BrandPojo p = ConvertUtil.convertBrandFormtoBrandPojo(f);
        return brandService.add(p);
    }

    public List<BrandData> searchBrandData(BrandForm form) {
        List<BrandPojo> list = brandService.searchBrandCategoryData(form);
        List<BrandData> reqList = new ArrayList<>();
        for (BrandPojo p : list) {
            reqList.add(ConvertUtil.convertBrandPojotoBrandData(p));
        }
        return reqList;
    }

    public BrandData getBrandData(int id) {
        return ConvertUtil.convertBrandPojotoBrandData(brandService.get(id));
    }

    public List<BrandData> getAllBrand() {
        List<BrandPojo> p = brandService.getAll();
        List<BrandData> brandDataList = new ArrayList<>();
        for (BrandPojo b : p) {
            brandDataList.add(ConvertUtil.convertBrandPojotoBrandData(b));
        }
        return brandDataList;
    }


    public BrandPojo updateBrand(int id, BrandForm f) throws ApiException {
        checkForm(f);
        BrandPojo p = ConvertUtil.convertBrandFormtoBrandPojo(f);
        return brandService.update(id, p);
    }

    public BrandPojo getByBrandCategory(BrandForm f) throws ApiException {
        return brandService.getByBrandCategory(f);
    }

    private void checkForm(BrandForm f) throws ApiException {
        if (f.getCategory() == null || f.getBrand() == null) {
            throw new ApiException("No brand and category provided");
        }
    }

    @Getter
    @Setter
    private static class BrandError {
        private String brandName;

        private String category;

        private String errorMessage;
    }

    public UploadProgressData addBrandCategoryFromFile(FileReader file) {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<BrandForm> formList = new CsvToBeanBuilder(file)
                    .withSeparator('\t')
                    .withType(BrandForm.class)
                    .build()
                    .parse();
            System.out.println(formList.size());
            progress.setTotalCount(formList.size());
            for (BrandForm form : formList) {
                try {
                    addBrand(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);

                    BrandError error = new BrandError();
                    error.setCategory(form.getCategory());
                    error.setBrandName(form.getBrand());
                    error.setErrorMessage(e.getMessage());

                    String errorMsg = mapper.writeValueAsString(error);
                    progress.getErrorMessages().add(errorMsg);
                    System.out.println(e);
                }
            }
            return progress;
        } catch (Exception e) {
            progress.setErrorCount(progress.getErrorCount() + 1);
            progress.getErrorMessages().add(e.getMessage());
            System.out.println(e);
        }
        return progress;
    }

}
