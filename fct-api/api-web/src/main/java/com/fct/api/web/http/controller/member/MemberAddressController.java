package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-7-10.
 */
@RestController
@RequestMapping(value = "address")
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

        MemberLogin member = this.memberAuth();

        MemberAddress address = memberService.getMemberAddress(id);
        if (address != null && member.getMemberId() != address.getMemberId()) {

            return new ReturnValue<MemberAddress>(200, "不存在此收货地址");
        }

        ReturnValue<MemberAddress> response = new ReturnValue<>();
        response.setData(address);

        return response;
    }

    @RequestMapping(value = "{id}/default", method = RequestMethod.POST)
    public ReturnValue saveDefault(@PathVariable("id") Integer id) {

        MemberLogin member = this.memberAuth();
        memberService.setDefaultAddress(id, member.getMemberId());

        return new ReturnValue(200, "设置默认成功");
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public ReturnValue saveDelete(@PathVariable("id") Integer id) {

        MemberLogin member = this.memberAuth();

        memberService.deleteAddress(id, member.getMemberId());

        return new ReturnValue(200, "删除成功");
    }
}
