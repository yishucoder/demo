package com.example.demo.controller;

import com.alipay.api.AlipayApiException;
import com.example.demo.service.AlipayService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AlipayController {

    @Resource
    private AlipayService alipayService;

    @GetMapping("/pay")
    public String createPayOrder(){
        return alipayService.createPayOrder();
    }



    @GetMapping("/alipay_return")
    @ResponseBody
    public String returnPay(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException {
        return alipayService.returnPay( request);
    }

    @GetMapping("/alipay_query_order")
    @ResponseBody
    public String queryOrder(HttpServletRequest request) throws AlipayApiException {
        String outTradeNo = request.getParameter("outTradeNo");
        String tradeNo = request.getParameter("tradeNo");
        return alipayService. queryPayOrder( outTradeNo,  tradeNo).getBody() ;
    }

    @GetMapping("/alipay_close_order")
    @ResponseBody
    public String closeOrder(HttpServletRequest request) throws AlipayApiException {
        String outTradeNo = request.getParameter("outTradeNo");
        String tradeNo = request.getParameter("tradeNo");
        return alipayService. closeOrder( outTradeNo,  tradeNo).getBody() ;
    }

    @GetMapping("/alipay_refund_order")
    @ResponseBody
    public String refundOrder(HttpServletRequest request) throws AlipayApiException {
        String outTradeNo = request.getParameter("outTradeNo");
        String tradeNo = request.getParameter("tradeNo");
        String refundAmount = request.getParameter("refundAmount");
        return alipayService.refundOrder( outTradeNo,  tradeNo,refundAmount) ;
    }

    @PostMapping("/alipay_notify")
    @ResponseBody
    public String notifyPay(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException {
        return alipayService.notifyPay( request);
    }

    @GetMapping("/alipay_refund_order_query")
    @ResponseBody
    public String refundOrderQuery(HttpServletRequest request) throws AlipayApiException {
        String outTradeNo = request.getParameter("outTradeNo");
        String tradeNo = request.getParameter("tradeNo");
        String refundAmount = request.getParameter("refundAmount");
        return alipayService. refundOrderQuery( outTradeNo,  tradeNo).getBody() ;
    }
}
