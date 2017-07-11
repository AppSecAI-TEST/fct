package com.fct.finance.service;

import com.fct.finance.data.entity.*;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.finance.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by jon on 2017/4/7.
 */
@Service(value = "financeService")
public class FinanceServiceImpl implements FinanceService {
    
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

    public PayOrder createPayOrder(PayOrder payOrder) {

        try
        {
            return payOrderManager.create(payOrder);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayOrder getPayOrder(String orderId){
        try
        {
            return  payOrderManager.findOne(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayOrder getPayOrderByTrade(String tradeType, String tradeId)
    {
        try
        {
            return  payOrderManager.findByTrade(tradeType,tradeId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayOrder paySuccess(String orderId, String platform, String notifyData){

        try
        {
            return  payOrderManager.paySuccess(orderId,platform,notifyData);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void payTradeNotify(String jsonMQPayTrade){

        try
        {
            payOrderManager.tradeNotify(jsonMQPayTrade);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<PayOrder> findPayRecord(Integer memberId, String cellPhone, String orderId, String platform,String payOrderId, String tradeId,
                                                String tradeType,Integer status, Integer timeType,String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        try
        {
            return  payOrderManager.findAll(memberId,cellPhone,orderId,platform,payOrderId,tradeId,tradeType,status,
                    timeType,beginTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }


    public MemberAccount getMemberAccount(Integer memberId)
    {
        try
        {
            return memberAccountManager.findById(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<MemberAccount> findMemberAccount(String cellPhone, Integer orderBy, Integer pageIndex, Integer pageSize)
    {
        try
        {
            return  memberAccountManager.findAll(cellPhone,orderBy,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<MemberAccountHistory> findMemberAccountHistory(Integer memberId, String cellPhone, String tradeId, String tradeType,
                                                                       String startTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        try
        {
            return memberAccountHistoryManager.findAll(memberId,cellPhone,tradeId,tradeType,startTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public RefundRecord createRefundRecord(RefundRecord refund)
    {
        try
        {
            return refundRecordManager.create(refund);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public RefundRecord getRefundRecord(Integer id)
    {
        try
        {
            return refundRecordManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public RefundRecord getRefundRecordByTrade(String tradeType,String tradeId)
    {
        try
        {
            return refundRecordManager.findByTradeId(tradeType,tradeId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void refundConfirm(Integer omsOperaterId,String ids)
    {
        try
        {
            refundRecordManager.confirm(omsOperaterId,ids);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void refundClose(Integer omsOperaterId,Integer refundId,String remark)
    {
        try
        {
            refundRecordManager.close(omsOperaterId,refundId,remark);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void refundSuccess(Integer refundId,String notifyData)
    {
        try
        {
            refundRecordManager.success(refundId,notifyData);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<RefundRecord> findRefundRecord(Integer memberId, String cellPhone, String payOrderId,String tradeId, String tradeType,
                                                       String payPlatform, Integer method,Integer status, String beginTime, String endTime,
                                                       Integer pageIndex, Integer pageSize)
    {
        try
        {
            return  refundRecordManager.findAll(memberId,cellPhone,payOrderId,tradeId,tradeType,payPlatform,method,
                status,beginTime,endTime, pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
    public void applyWithdraw(WithdrawRecord withdrawRecord)
    {
        try
        {
            withdrawRecordManager.apply(withdrawRecord);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void withdrawSuccess(Integer omsOperaterId,Integer id)
    {
        try
        {
            withdrawRecordManager.updateStatus(omsOperaterId,id,1,"");
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void withdrawFail(Integer omsOperaterId,Integer id,String desc)
    {
        try
        {
            withdrawRecordManager.updateStatus(omsOperaterId,id,2,desc);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<WithdrawRecord> findWithdrawRecord(Integer memberId, String cellPhone, Integer status,
                                                   String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        try
        {
            return  withdrawRecordManager.findAll(memberId,cellPhone,status,beginTime,endTime,pageIndex,
                    pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Integer createSettleRecord(SettleRecord settleRecord)
    {
        try
        {
            return  settleRecordManager.create(settleRecord);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }


    public void settleConfirm(Integer omsOperaterId,String ids)
    {
        try
        {
            settleRecordManager.updateStatus(omsOperaterId,1,ids,"");
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }


    public void settleRefuse(Integer omsOperaterId,Integer id,String remark)
    {
        try
        {
            settleRecordManager.updateStatus(omsOperaterId,3,id.toString(),remark);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }


    public void settleTask()
    {
        try
        {
            settleRecordManager.task();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public SettleRecord getSettleRecord(Integer recordId)
    {
        try
        {
            return settleRecordManager.findById(recordId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }


    public PageResponse<SettleRecord> findSettleRecord(Integer memberId, String cellPhone, String tradeType, String tradeId, Integer status,
                                               String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        try
        {
            return  settleRecordManager.findAll(memberId,cellPhone,tradeType,tradeId,status,beginTime,endTime,
                    pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<PayPlatform> findPayPlatform(String payment)
    {
        try
        {
            return  payPlatformManager.findAll(payment);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }


    public Integer createRechargeRecord(RechargeRecord record)
    {
        try
        {
            return rechargeRecordManager.create(record);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }


    public RechargeRecord getRechargeRecord(Integer id)
    {
        try
        {
            return rechargeRecordManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void rechargeExpiredTask()
    {
        try
        {
            rechargeRecordManager.handleExpired();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void updateRechargePayPlatform(Integer id,String payPlatform)
    {
        try
        {
            rechargeRecordManager.updatePayPlatform(id,payPlatform);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void rechargeSuccess(Integer id, String payOrderId, String payPlatform, String payTime, Integer payStatus)
    {
        try
        {
            rechargeRecordManager.paySuccess(id,payOrderId,payPlatform,payTime,payStatus);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }


    public PageResponse<RechargeRecord> findRechargeRecord(Integer memberId, String cellPhone, String payPlayform,String payOrderId,
                                                           Integer status,Integer timeType,String beginTime, String endTime,
                                                           Integer pageIndex, Integer pageSize)
    {
        try
        {
            return rechargeRecordManager.findAll(memberId,cellPhone,payPlayform,payOrderId,status,timeType,beginTime,
                    endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public  void giftPoints(String tradeId,String tradeType,Integer memberId,Integer points)
    {
        try
        {
            memberAccountManager.giftPoints(tradeId,tradeType,memberId,points);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}
