package com.wry.esapi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 创建一个User 对应提供全参，无参构造器
 * </p>
 *
 * @author wangruiyu
 * @since 2020/8/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {
    private String name;
    private Integer age;
}
