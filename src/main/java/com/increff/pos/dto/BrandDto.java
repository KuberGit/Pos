package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.UploadProgressData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.utils.ConvertUtil;
import com.increff.pos.utils.NormalizeUtil;
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
        NormalizeUtil.normalizeBrandMasterPojo(p);
        return brandService.add(p);
    }

    public List<BrandData> searchBrandData(BrandForm form) {
        NormalizeUtil.normalizeBrandForm(form);
        List<BrandPojo> list = brandService.searchBrandCategoryData(form);
        List<BrandData> reqList = new ArrayList<>();
        for (BrandPojo p : list) {
            reqList.add(ConvertUtil.convertBrandPojotoBrandData(p));
        }
        return reqList;
    }

    public BrandData getBrandData(int id) throws ApiException {
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
        NormalizeUtil.normalizeBrandMasterPojo(p);
        return brandService.update(id, p);
    }

    public BrandPojo getByBrandCategory(BrandForm f) throws ApiException {
        NormalizeUtil.normalizeBrandForm(f);
        return brandService.getByBrandCategory(f);
    }

    private void checkForm(BrandForm f) throws ApiException {
        if (f.getCategory() == null || f.getBrand() == null || f.getCategory().trim().isEmpty() || f.getBrand().trim().isEmpty()) {
            throw new ApiException("No brand and/or category provided");
        }
    }

    public UploadProgressData addBrandCategoryFromFile(FileReader file) throws ApiException {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<BrandForm> formList = new CsvToBeanBuilder(file).withSeparator('\t').withType(BrandForm.class).build().parse();
            progress.setTotalCount(formList.size());
            for (BrandForm form : formList) {
                try {
                    addBrand(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    progress.getErrorMessages().add(mapper.writeValueAsString(form) + " :: " + e.getMessage());
                }
            }
            return progress;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

}
