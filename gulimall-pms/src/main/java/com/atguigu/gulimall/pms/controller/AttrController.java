package com.atguigu.gulimall.pms.controller;

import java.util.Arrays;



import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;
import com.atguigu.gulimall.pms.service.AttrService;
import com.atguigu.gulimall.pms.vo.AttrSaveVo;
import com.atguigu.gulimall.pms.vo.AttrWithGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.pms.entity.AttrEntity;


/**
 * 商品属性
 *
 * @author huangchenglong
 * @email 1533966146@qq.com
 * @date 2019-08-01 21:09:38
 */
@Slf4j
@Api(tags = "商品属性 管理")
@RestController
@RequestMapping("pms/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

@Autowired
private AttrGroupService attrGroupService;


    @ApiOperation("查询某个三级分类下的所有销售属性")
    @GetMapping("/sale/{catId}")
    public Resp<PageVo> getCatelogSaleAttrs(@PathVariable("catId") Long catId,
                                            QueryCondition queryCondition){

        PageVo pageVo = attrService.queryPageCatelogBaseAttrs(queryCondition, catId, 0);
        return  Resp.ok(pageVo);

    }

    @ApiOperation("查询某个三级分类下的所有基本属性")
    @GetMapping("/base/{catId}")
    public Resp<PageVo> getCatelogBaseAttrs(@PathVariable("catId") Long catId,
                                            QueryCondition queryCondition){
        PageVo pageVo = attrService.queryPageCatelogBaseAttrs(queryCondition,catId,1);
          return  Resp.ok(pageVo);

    }
    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attr:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrId}")
    @PreAuthorize("hasAuthority('pms:attr:info')")
    public Resp<AttrEntity> info(@PathVariable("attrId") Long attrId){
        AttrWithGroupVo attrWithGroupVo= new AttrWithGroupVo();
		AttrEntity attr = attrService.getById(attrId);
        BeanUtils.copyProperties(attr,attrWithGroupVo);
//查出这个属性的分组信息
        AttrGroupEntity attrGroupEntity=attrGroupService.getGroupInfoByAttrId(attrId);
attrWithGroupVo.setGroup(attrGroupEntity);

        return Resp.ok(attrWithGroupVo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attr:save')")
    public Resp<Object> save(@RequestBody AttrSaveVo attr){
		attrService.saveAttrAndRelation(attr);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attr:update')")
    public Resp<Object> update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attr:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return Resp.ok(null);
    }

}
