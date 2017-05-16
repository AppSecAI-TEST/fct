package com.fct.finance.service;

import com.fct.finance.data.entity.*;
import com.fct.finance.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by jon on 2017/4/7.
 */
@Service(value = "financeService")
public class FinanceServiceImpl implements com.fct.finance.interfaces.FinanceService {
    
    @Autowired
    private PayOrderManager payOrderManager;

    @Autowired
    private MemberAccountManager memberAccountManager;

    @Autowired
    private MemberAccountHistoryManager memberAccountHistoryManager;

    @Autowired
    private  RefundRecordManager refundRecordManager;

    @Autowired
    private  WithdrawRecordManager withdrawRecordManager;

    @Autowired
    private SettleRecordManager settleRecordManager;

    @Autowired
    private PayPlatformManager payPlatformManager;

    @Autowired
    private RechargeRecordManager rechargeRecordManager;

    @Override
    public PayOrder createPayOrder(PayOrder payOrder) {

        return payOrderManager.create(payOrder);
    }

    @Override
    public PayOrder getPayOrder(String orderId){
        return  payOrderManager.findOne(orderId);
    }

    @Override
    public PayOrder getPayOrderByTrade(String tradeType, String tradeId)
    {
        return  payOrderManager.findByTrade(tradeType,tradeId);
    }

    @Override
    public PayOrder paySuccess(String orderId, String platform, String notifyData){
        return  payOrderManager.paySuccess(orderId,platform,notifyData);
    }

    @Override
    public void payTradeNotify(String jsonMQPayTrade){

        payOrderManager.tradeNotify(jsonMQPayTrade);
    }

    @Override
    public Page<PayOrder> findPayRecord(Integer memberId, String cellPhone, String platform, String tradeId, String tradeType,
                                        Integer status, String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return  payOrderManager.findAll(memberId,cellPhone,platform,tradeId,tradeType,status,
                beginTime,endTime,pageIndex,pageSize);
    }

    @Override
    public MemberAccount getMemberAccount(Integer memberId)
    {
        return memberAccountManager.findById(memberId);
    }

    @Override
    public Page<MemberAccount> findMemberAccount(String cellPhone, Integer orderBy, Integer pageIndex, Integer pageSize)
    {
        return  memberAccountManager.findAll(cellPhone,orderBy,pageIndex,pageSize);
    }

    @Override
    public Page<MemberAccountHistory> findMemberAccountHistory(Integer memberId, String cellPhone, String tradeId, String tradeType,
                                                               Integer pageIndex, Integer pageSize)
    {
        return memberAccountHistoryManager.findAll(memberId,cellPhone,tradeId,tradeType,pageIndex,pageSize);
    }
    @Override
    public RefundRecord createRefundRecord(RefundRecord refund)
    {
        return refundRecordManager.create(refund);
    }

    @Override
    public void refundConfirm(Integer omsOperaterId,String ids)
    {
        refundRecordManager.confirm(omsOperaterId,ids);
    }

    @Override
    public void refundSuccess(Integer refundId,String notifyData)
    {
        refundRecordManager.success(refundId,notifyData);
    }

    @Override
    public Page<RefundRecord> findRefundRecord(Integer memberId, String cellPhone, String tradeId, String tradeType, String payPlatform,
                                               Integer status, String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return  refundRecordManager.findAll(memberId,cellPhone,tradeId,tradeType,payPlatform,status,beginTime,endTime,
                pageIndex,pageSize);
    }
    @Override
    public void applyWithdraw(WithdrawRecord withdrawRecord)
    {
        withdrawRecordManager.apply(withdrawRecord);
    }

    @Override
    public void withdrawSuccess(Integer omsOperaterId,Integer id)
    {
        withdrawRecordManager.updateStatus(omsOperaterId,id,1,"");
    }

    @Override
    public void withdrawFail(Integer omsOperaterId,Integer id,String desc)
    {
        withdrawRecordManager.updateStatus(omsOperaterId,id,2,desc);
    }

    @Override
    public Page<WithdrawRecord> findWithdrawRecord(Integer memberId, String cellPhone, Integer status,
                                                   String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return  withdrawRecordManager.findAll(memberId,cellPhone,status,beginTime,endTime,pageIndex,
                pageSize);
    }
    @Override
    public Integer createSettleRecord(SettleRecord settleRecord)
    {
        return  settleRecordManager.create(settleRecord);
    }

    @Override
    public void settleConfirm(Integer omsOperaterId,String ids)
    {
        settleRecordManager.updateStatus(omsOperaterId,1,ids,"");
    }

    @Override
    public void settleRefuse(Integer omsOperaterId,Integer id,String remark)
    {
        settleRecordManager.updateStatus(omsOperaterId,3,id.toString(),remark);
    }

    @Override
    public void settleTask()
    {
        settleRecordManager.task();
    }


    @Override
    public SettleRecord getSettleRecord(Integer recordId)
    {
        return settleRecordManager.findById(recordId);
    }

    @Override
    public Page<SettleRecord> findSettleRecord(Integer memberId, String cellPhone, String tradeType, String tradeId, Integer status,
                                               String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return  settleRecordManager.findAll(memberId,cellPhone,tradeType,tradeId,status,beginTime,endTime,
                pageIndex,pageSize);
    }
    @Override
    public List<PayPlatform> findPayPlatform()
    {
        return  payPlatformManager.findAll();
    }

    @Override
    public Integer createRechargeRecord(RechargeRecord record)
    {
        return rechargeRecordManager.create(record);
    }

    @Override
    public RechargeRecord getRechargeRecord(Integer id)
    {
        return rechargeRecordManager.findById(id);
    }

    @Override
    public void rechargeSuccess(Integer id, String payOrderId, String payPlatform, String payTime,String payStatus)
    {
        rechargeRecordManager.paySuccess(id,payOrderId,payPlatform,payTime,payStatus);
    }

    @Override
    public Page<RechargeRecord> findRechargeRecord(Integer memberId, String cellPhone, Integer status,
                                                   String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return rechargeRecordManager.findAll(memberId,cellPhone,status,beginTime,endTime,pageIndex,pageSize);
    }

}
