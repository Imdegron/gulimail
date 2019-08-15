package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.pms.feign.SmsSkuSaleInfoFeignService;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SmsSkuSaleInfoFeignService smsSkuSaleInfoFeignService;
    @Autowired
    SpuInfoDao spuInfoDao;

    @Autowired
    SpuInfoDescDao spuInfoDescDao;

    @Autowired
    ProductAttrValueDao spuAttrValueDao;

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SkuImagesDao imagesDao;

    @Autowired
    AttrDao attrDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Long catId) {
        //1.封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //查全站的，catalog_id=catId
        //catalog_id=227 and (spu_name like oss or id =1)
        if (catId != 0) {
            wrapper.eq("catalog_id", catId);
            if (!StringUtils.isEmpty(queryCondition.getKey())) {
                wrapper.and(obj -> {
                    obj.like("spu_name", queryCondition.getKey());
                    obj.or().like("id", queryCondition.getKey());
                    return obj;

                });

            }


        }

        //封装翻页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);
        //3.去数据库查询
        IPage<SpuInfoEntity> data = this.page(page, wrapper);
        /**
         * 分页
         * 	 * @param list        列表数据
         * 	 * @param totalCount  总记录数
         * 	 * @param pageSize    每页记录数
         * 	 * @param currPage    当前页数
         */
        //PageVo pageVo=new PageVo(data.getRecords(),data.getTotal(),data.getSize(),data.getCurrent());
        PageVo vo = new PageVo(data);
        return vo;
    }

    @Override
    public void spuBigSave(SpuAllSaveVo spuInfo) {


        //（1）保存spu的基本信息
        Long spuId = this.saveSpuBaseInfo(spuInfo);
        //(2)保存spu的图片信息
        Long spuImage = this.saveSpuInfoImages(spuId, spuInfo.getSpuImages());

        //2.保存sku的基本属性信息
        List<BaseAttrVo> baseAttrs = spuInfo.getBaseAttrs();
        this.saveSpuBaseAttrs(spuId, baseAttrs);

        //3.保存sku的销售信息
        this.saveSkuInfos(spuId, spuInfo.getSkus());
    }

    //负责解析数据做出相应的业务
    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuInfo) {
        //1.保存sku的基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //把spuInfo里基本信息复制给spuInfoEntity中
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        //设置时间，给spuInfoEntity
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());
        //添加保存
        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }


    @Override
    public Long saveSpuInfoImages(Long spuId, String[] spuImages) {
        //保存图片信息
        StringBuffer urls = new StringBuffer();
        for (String image : spuImages) {
            urls.append(image);
            urls.append(",");

        }
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuId);
        descEntity.setDecript(urls.toString());
        spuInfoDescDao.insert(descEntity);
        return descEntity.getSpuId();
    }

    @Override
    public void saveSpuBaseAttrs(Long spuId, List<BaseAttrVo> baseAttrs) {
        List<ProductAttrValueEntity> allSave = new ArrayList<>();
        for (BaseAttrVo baseAttr : baseAttrs) {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(baseAttr.getAttrId());
            entity.setAttrName(baseAttr.getAttrName());
            String[] selected = baseAttr.getValueSelected();
            entity.setAttrValue(AppUtils.arrayToStringWithSeperator(selected, ","));
            entity.setAttrSort(0);
            entity.setQuickShow(1);
            entity.setSpuId(spuId);
            allSave.add(entity);
        }
        spuAttrValueDao.insertBatch(allSave);

    }

    //3.保存sku的所有信息
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        //查出sku的信息
        SpuInfoEntity spuInfo = this.getById(spuId);
        //保存sku的info信息
        for (SkuVo skuVo : skus) {
            String[] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            if (images != null && images.length > 0) {
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setWeight(skuVo.getWeight());

            //保存sku的基本信息
            skuInfoDao.insert(skuInfoEntity);

            //保存sku的所有图片
            Long skuId = skuInfoEntity.getSkuId();
            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity imagesEntity = new SkuImagesEntity();
                imagesEntity.setSkuId(skuId);
                imagesEntity.setDefaultImg(i == 0 ? 1 : 0);
                imagesEntity.setImgUrl(images[i]);
                imagesEntity.setImgSort(0);
                //添加并保存sku的所有图片
                imagesDao.insert(imagesEntity);
            }

            //保存当前sku的所有销售属性组合
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            for (SaleAttrVo attrVo : saleAttrs) {
                //查出当前属性的信息
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                entity.setAttrId(attrVo.getAttrId());
                //查出这个属性的真正信息
                AttrEntity attrEntity = attrDao.selectById(attrVo.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrSort(0);
                entity.setAttrValue(attrVo.getAttrValue());
                entity.setSkuId(skuId);
//sku与销售属性的关联关系
                skuSaleAttrValueDao.insert(entity);
            }
            //以上都是pms系统做的工作

            //以下需要由sms完成，保存每一个sku的相关优惠信息
            SkuSaleInfoTo info = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo,info);
            info.setSkuId(skuId);
            //发给sms，去处理
            log.info("pms准备给sms发送信息"+info);
            smsSkuSaleInfoFeignService.saveSkuSaleInfos(info);
            log.info("pms准备给sms发送信息jiesu....");
        }

    }


}