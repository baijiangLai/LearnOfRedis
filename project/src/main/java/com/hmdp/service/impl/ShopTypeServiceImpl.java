package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public Result queryList() {
        String listJson = redisTemplate.opsForValue().get(RedisConstants.CACHE_LIST_KEY);
        if (!StrUtil.isEmpty(listJson)) {
            ShopType shopType = JSONUtil.toBean(listJson, ShopType.class);
            return Result.ok(shopType);
        }
        QueryWrapper<ShopType> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        List<ShopType> typeList = baseMapper.selectList(wrapper);
        if (typeList == null) {
            return Result.fail("目前数据为kong");
        }

        redisTemplate.opsForValue().set(RedisConstants.CACHE_LIST_KEY, JSONUtil.toJsonStr(typeList));
        return Result.ok(typeList);
    }
}
