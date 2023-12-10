package com.example.demo.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import javax.servlet.http.HttpServletRequest;

public interface AlipayService {

    /**
     * 支付接口
     * @return
     */
    String createPayOrder();

    /**
     * 订单查询接口
     * @param outTradeNo 商家订单号
     * @param tradeNo 支付宝交易单号
     * @return
     */
    AlipayTradeQueryResponse queryPayOrder(String outTradeNo, String tradeNo) throws AlipayApiException;

    /**
     * 返回参数校验
     * @param request
     * @return
     * @throws AlipayApiException
     */
    boolean returnCheck(HttpServletRequest request) throws AlipayApiException;

    /**
     * 支付宝支付成功页面跳转
     * @param requeste
     * @return
     */
    String returnPay(HttpServletRequest requeste) throws AlipayApiException;

    /**
     * 关闭订单
     * @param outTradeNo 商家订单号
     * @param tradeNo 支付宝交易单号
     * @return
     * @throws AlipayApiException
     */
    AlipayTradeCloseResponse closeOrder(String outTradeNo, String tradeNo)  throws AlipayApiException;

    /**
     * 订单退款
     * @param outTradeNo 商家订单号
     * @param tradeNo 支付宝交易单号
     * @param refundAmount 退款金额
     * @return
     * @throws AlipayApiException
     */
    String refundOrder(String outTradeNo, String tradeNo,String refundAmount)  throws AlipayApiException;

    /**
     * 支付宝回调通知
     * @param requeste
     * @return
     */
    String notifyPay(HttpServletRequest requeste) throws AlipayApiException;


    /**
     * 查询退款 订单
     * @param outTradeNo 商家订单号
     * @param tradeNo 支付宝交易单号
     * @return
     * @throws AlipayApiException
     */
    AlipayTradeFastpayRefundQueryResponse refundOrderQuery(String outTradeNo, String tradeNo)  throws AlipayApiException;
}
