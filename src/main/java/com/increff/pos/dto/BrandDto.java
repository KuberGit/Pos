package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public List<BrandData> searchBrandData(BrandForm form)  {
        List<BrandPojo> list = brandService.searchBrandCategoryData(form);
        List<BrandData> reqList = new ArrayList<>();
        for(BrandPojo p:list){
            reqList.add(ConvertUtil.convertBrandPojotoBrandData(p));
        }
        return reqList;
    }

    public BrandData getBrandData(int id) {
        return ConvertUtil.convertBrandPojotoBrandData(brandService.get(id));
    }

    public List<BrandData> getAllBrand(){
        List<BrandPojo> p = brandService.getAll();
        List<BrandData> brandDataList = new ArrayList<>();
        for(BrandPojo b:p){
            brandDataList.add(ConvertUtil.convertBrandPojotoBrandData(b));
        }
        return brandDataList;
    }


    public BrandPojo updateBrand(int id,BrandForm f) throws ApiException {
        checkForm(f);
        BrandPojo p = ConvertUtil.convertBrandFormtoBrandPojo(f);
        return brandService.update(id,p);
    }

    public BrandPojo getByBrandCategory(BrandForm f) throws ApiException {
        return brandService.getByBrandCategory(f);
    }

    private void checkForm(BrandForm f) throws ApiException {
        if(f.getCategory() == null || f.getBrand() == null){
            throw new ApiException("No brand and category provided");
        }
    }

}
