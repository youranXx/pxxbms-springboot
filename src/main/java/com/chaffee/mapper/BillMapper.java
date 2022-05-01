package com.chaffee.mapper;

import com.chaffee.entity.pojo.Bill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author RGB
* @description 针对表【bill】的数据库操作Mapper
* @createDate 2022-04-30 10:31:55
* @Entity Bill
*/
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

}



