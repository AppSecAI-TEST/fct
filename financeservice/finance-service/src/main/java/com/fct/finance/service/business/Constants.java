package com.fct.finance.service.business;

/**
 * Created by jon on 2017/4/10.
 */
public class Constants {

    /// <summary>
    /// 支付状态
    /// </summary>
    public  enum enumPayStatus
    {

        waitpay("waitpay",0),success("success",1),
        close("close",2),exception("exception",3),
        partrefund("partrefund",5),fullrefund("fullrefund",6);

        private Integer value;
        private String key;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getkey() {
            return key;
        }

        public void setkey(String key) {
            this.key = key;
        }

        private enumPayStatus(String key,Integer value) {
            this.value = value;
            this.key = key;
        }
    }

    /// <summary>
    /// 账号变动行为
    /// </summary>
    public enum enumTradeType
    {
        /// <summary>
        /// 充值
        /// </summary>
        recharge,

        /// <summary>
        /// 购买产品
        /// </summary>
        buy,

        /// <summary>
        /// 提现
        /// </summary>
        withdrawal,

        /// <summary>
        /// 结算
        /// </summary>
        settle,

        /// <summary>
        /// 退款
        /// </summary>
        refund
    }

    /// <summary>
    /// 退款行为方式
    /// </summary>
    public enum enumRefundMethod
    {
        /// <summary>
        /// 退款至虚拟账户
        /// </summary>
        account(0),
        /// <summary>
        /// 原路返回
        /// </summary>
        wayback(1),
        /// <summary>
        /// 线下处理
        /// </summary>
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

    /// <summary>
    /// 退款状态
    /// </summary>
    public enum enumRefundStatus
    {
        wait_handle(0),confirmed(1),
        success(2);

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
}
