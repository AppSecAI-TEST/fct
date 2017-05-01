package com.fct.api.interfaces.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created by ningyang on 2017/4/6.
 * 用户 DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String id;

    private String name;

}
