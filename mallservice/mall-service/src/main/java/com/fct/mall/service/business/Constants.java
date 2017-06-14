package com.fct.mall.service.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jon on 2017/5/17.
 */
public class Constants {

    public static final Logger logger = LoggerFactory.getLogger("EX");

    /// <summary>
    /// 订单状态
    /// </summary>
    public enum enumOrderStatus
    {
        /// <summary>
        /// 等待买家付款
        /// </summary>
        waitPay(0),

        /// <summary>
        /// 付款成功
        /// </summary>
        paySuccess(1),

        /// <summary>
        /// 已发货
        /// </summary>
        delivered(2),

        /// <summary>
        /// 交易完成
        /// </summary>
        finished(3),

        /// <summary>
        /// 交易关闭
        /// </summary>
        close(4);

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        private enumOrderStatus(Integer value) {
            this.value = value;
        }
    }

    public enum enumRefundStatus
    {
        //等待处理
        wait(0),
        //接受申请退货退款
        accept(1),
        //用户快递回公司
        express(2),
        //同意退款
        agree(3),
        //退款成功
        success(4),
        //拒绝处理
        refuse(5),
        //关闭退换货
        close(6);

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        private enumRefundStatus(Integer value) {
            this.value = value;
        }
    }

    public enum enumRefundMethod
    {
        //默认余额
        account(0),
        //原路返回
        return_original_road(1),
        //线下转账
        offline(2);

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        private enumRefundMethod(Integer value) {
            this.value = value;
        }
    }
}
