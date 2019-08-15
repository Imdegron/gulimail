package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrGroupDao;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
@Autowired
    AttrAttrgroupRelationDao relationDao;

@Autowired
    AttrDao attrDao;

@Autowired
  AttrGroupDao attrGroupDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }
//获取某个三级分类下的所有属性分组
    @Override
    public PageVo queryPageAttrGroupsByCatId(QueryCondition queryCondition, Long catId) {
        //获取分页条件
        IPage<AttrGroupEntity> page=new Query<AttrGroupEntity>().getPage(queryCondition);
          //构造查询条件
        QueryWrapper<AttrGroupEntity> queryWrapper=new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId);
       //翻页效果
        IPage<AttrGroupEntity> data= this.page(page,queryWrapper);

        //查出的所有分组
        List<AttrGroupEntity> records= data.getRecords();

        //查出每一个分组的所有属性
        //将要返回出去的带分组信息及它的属性信息的对象
        List<AttrGroupWithAttrsVo> groupWithAttrs =new ArrayList<>(records.size());
        for (AttrGroupEntity record : records) {
            //1.创建一个vo对象准备收集所有需要的数据
            AttrGroupWithAttrsVo vo= new AttrGroupWithAttrsVo();
         //2.复制信息给vo
            BeanUtils.copyProperties(record,vo);
           Long groupId= record.getAttrGroupId();
           //获取当前分组的所有属性
            //先找关联关系
            List<AttrAttrgroupRelationEntity> relationEntities=relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",groupId));
            if(relationEntities!=null && relationEntities.size()>0){
                List<Long> attrIds =new ArrayList<>();
                for (AttrAttrgroupRelationEntity entity : relationEntities) {
                    attrIds.add(entity.getAttrId());
                }
                //查出当前分组的所有信息
                List<AttrEntity> attrEntities=attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id",attrIds));
                //把当前信息存给vo
                vo.setAttrEntities(attrEntities);
            }

            //把vo放入集合中
            groupWithAttrs.add(vo);
        }
        //返回list,totalCount,pageSize,currentPages
        return new PageVo(groupWithAttrs,data.getTotal(),data.getSize(),data.getCurrent());
    }
//根据属性id找到分组信息
    @Override
    public AttrGroupEntity getGroupInfoByAttrId(Long attrId) {
//根据attrid找到关系表中的哪个组
        AttrAttrgroupRelationEntity one= relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrId));
        AttrGroupEntity attrGroupEntity=null;
        //根据分组id找到分组信息
        if(one!=null){
            Long attrGroupId=one.getAttrGroupId();
             attrGroupEntity=attrGroupDao.selectById(attrGroupId);
        }

        return attrGroupEntity;
    }

}