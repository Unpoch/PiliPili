package com.wz.pilipili.entity.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页查询返回结果
 */
@Data
@AllArgsConstructor
public class PageResult<T> {

    private Long total;//返回的总记录数

    private List<T> list;//分页数据

}
