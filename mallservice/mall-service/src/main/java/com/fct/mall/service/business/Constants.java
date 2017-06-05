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

    public enum multiRefundStatus
    {
        //默认无用
        NORMAL_REFUND(0),
        //等待处理
        WAIT_REFUND(1),
        //同意退款
        ACCEPT_REFUND(2),
        //同意退换货
        ACCEPT_REFUND_GOODS(3),
        //用户快递回公司
        USER_EXPRESS_GOODS(4),
        //公司重新发出快递
        ADMIN_EXPRESS_GOODS(5),
        //退款
        REFUND_MONEY(6),
        //退款
        REFUND_MONEY_SUCCESS(7),
        //拒绝处理
        REFUSE_REFUND(8),
        //关闭退换货
        CLOSE_REFUND(9);

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        private multiRefundStatus(Integer value) {
            this.value = value;
        }
    }

    public enum refundMoneyTypes
    {
        //默认余额
        NORMAL(0),
        //原路返回
        ONLINE(1),
        //线下转账
        OFFLINE(2);

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        private refundMoneyTypes(Integer value) {
            this.value = value;
        }
    }
}
