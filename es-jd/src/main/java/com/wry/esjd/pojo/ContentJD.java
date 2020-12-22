package com.wry.esjd.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 京东返回的数据封装对象
 * </p>
 *
 * @author wangruiyu
 * @since 2020/8/3
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentJD {
    private String title;
    private String img;
    private String price;
}
