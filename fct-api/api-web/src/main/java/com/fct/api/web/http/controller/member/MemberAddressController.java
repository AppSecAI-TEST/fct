package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-7-10.
 */
@RestController
@RequestMapping(value = "/member/address")
public class MemberAddressController extends BaseController {


    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<List<MemberAddress>> findAddress() {

        MemberLogin member = this.memberAuth();

        List<MemberAddress> lsAddress = memberService.findMemberAddress(member.getMemberId());

        ReturnValue<List<MemberAddress>> response = new ReturnValue<>();
        response.setData(lsAddress);

        return response;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<MemberAddress> getAddress(@PathVariable("id") Integer id) {

        if (id < 1)
            return new ReturnValue<>(404, "收货地址不存在");

        MemberLogin member = this.memberAuth();

        MemberAddress address = memberService.getMemberAddress(id);
        if (address != null && member.getMemberId() != address.getMemberId()) {

            return new ReturnValue<MemberAddress>(200, "不存在此收货地址");
        }

        ReturnValue<MemberAddress> response = new ReturnValue<>();
        response.setData(address);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveAddress(Integer id, String name, String phone,
                                   String province, String city, String region,
                                   String address, Integer is_default) {

        MemberLogin member = this.memberAuth();

        id = ConvertUtils.toInteger(id, 0);
        name = ConvertUtils.toString(name);
        phone = ConvertUtils.toString(phone);
        province = ConvertUtils.toString(province);
        city = ConvertUtils.toString(city);
        region = ConvertUtils.toString(region);
        address = ConvertUtils.toString(address);
        is_default = ConvertUtils.toInteger(is_default, 0);

        if (StringUtils.isEmpty(name))
            return new ReturnValue(404, "姓名不能为空");
        if (StringUtils.isEmpty(phone))
            return new ReturnValue(404, "电话不能为空");
        if (StringUtils.isEmpty(address))
            return new ReturnValue(404, "地址不能为空");

        MemberAddress addr = new MemberAddress();
        addr.setId(id);
        addr.setMemberId(member.getMemberId());
        addr.setName(name);
        addr.setCellPhone(phone);
        addr.setProvince(province);
        addr.setCityId(city);
        addr.setTownId(region);
        addr.setAddress(address);
        addr.setIsDefault(is_default);

        memberService.saveMemberAddress(addr);

        return new ReturnValue(200, id > 0 ? "修改成功" : "添加成功");
    }

    @RequestMapping(value = "{id}/default", method = RequestMethod.POST)
    public ReturnValue saveDefault(@PathVariable("id") Integer id) {

        if (id < 1)
            return new ReturnValue<>(404, "收货地址不存在");

        MemberLogin member = this.memberAuth();
        memberService.setDefaultAddress(id, member.getMemberId());

        return new ReturnValue(200, "设置默认成功");
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public ReturnValue saveDelete(@PathVariable("id") Integer id) {

        if (id < 1)
            return new ReturnValue<>(404, "收货地址不存在");

        MemberLogin member = this.memberAuth();

        memberService.deleteAddress(id, member.getMemberId());

        return new ReturnValue(200, "删除成功");
    }
}
