package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.example.demo.config.AlipayConfig;
import com.example.demo.service.AlipayService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Resource
    AlipayConfig aipayConfig;

    @Override
    public String createPayOrder() {
        AlipayClient alipayClient = createClient();
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(aipayConfig.getNotifyUrl());
        //同步跳转地址，仅支持http/https
        request.setReturnUrl(aipayConfig.getReturnUrl());
        /******必传参数******/
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", System.currentTimeMillis());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", 0.01);
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "测试商品");
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code",  aipayConfig.getAlipaypProductCode());
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            System.out.println("支付失败");
        }
        if(response.isSuccess()){
            System.out.println("创建支付订单成功");
        } else {
            System.out.println("创建支付订单失败");
        }
        System.out.println("订单号："+bizContent.get("out_trade_no"));
        System.out.println("支付宝交易单号："+ response.getTradeNo());
        return payHtml(response.getBody());
    }

    @Override
    /**
     * 订单查询接口
     * @param outTradeNo 商家订单号
     * @param tradeNo 支付宝交易单号
     * @return
     */
    public AlipayTradeQueryResponse queryPayOrder(String outTradeNo, String tradeNo) throws AlipayApiException {
        AlipayClient alipayClient = createClient();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no",outTradeNo);
        bizContent.put("trade_no",tradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("查询订单成功");
        } else {
            System.out.println("查询订单失败");
        }
        return response;
    }

    private String payHtml(String form){
        StringBuffer html = new StringBuffer();
        html.append("<html><body><div>")
                .append(form)
                .append("</div></body></html>");
        return html.toString();
    }

    private AlipayClient createClient(){
        return new DefaultAlipayClient(
                aipayConfig.getServerUrl(),
                aipayConfig.getAppId(),
                aipayConfig.getPrivateKey(),
                AlipayConstants.FORMAT_JSON,
                AlipayConstants.CHARSET_UTF8,
                aipayConfig.getAlipayPublicKey(),
                AlipayConstants.SIGN_TYPE_RSA2);
    }

    /**
     * 返回参数验证
     * @return
     */
    public boolean returnCheck(HttpServletRequest request) throws AlipayApiException {
        Map<String,String> map = parseRequestParam( request);
        return AlipaySignature.rsaCheckV1(map,aipayConfig.getAlipayPublicKey(),AlipayConstants.CHARSET_UTF8,AlipayConstants.SIGN_TYPE_RSA2);

    }

    /**
     * 支付宝支付成功页面跳转
     *
     * @param requeste
     * @return
     */
    @Override
    public String returnPay(HttpServletRequest requeste) throws AlipayApiException {
        return alpayCallBack(requeste);
    }

    /**
     * 关闭订单
     *
     * @param outTradeNo 商家订单号
     * @param tradeNo    支付宝交易单号
     * @return
     * @throws AlipayApiException
     */
    @Override
    public AlipayTradeCloseResponse closeOrder(String outTradeNo, String tradeNo) throws AlipayApiException {
        AlipayClient alipayClient = createClient();
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no",outTradeNo);
        bizContent.put("trade_no",tradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("订单关闭成功");
        } else {
            System.out.println("订单关闭失败");
        }
        return response;
    }

    /**
     * 订单退款
     *
     * @param outTradeNo   商家订单号
     * @param tradeNo      支付宝交易单号
     * @param refundAmount 退款金额
     * @return
     * @throws AlipayApiException
     */
    @Override
    public String refundOrder(String outTradeNo, String tradeNo, String refundAmount) throws AlipayApiException {
        AlipayClient alipayClient =  createClient();
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(aipayConfig.getNotifyUrl());
        //同步跳转地址，仅支持http/https
        request.setReturnUrl(aipayConfig.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", tradeNo);
        bizContent.put("out_trade_no",outTradeNo);
        bizContent.put("refund_amount",refundAmount);
        //退款发起的单号
        bizContent.put("out_request_no", System.currentTimeMillis());
        request.setBizContent(bizContent.toString());
        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("退款成功");
        } else {
            System.out.println("退款失败");
        }
        System.out.println("退款单号："+bizContent.get("out_request_no"));
        //{"alipay_trade_refund_response":{"code":"10000","msg":"Success","buyer_logon_id":"kgm***@sandbox.com","buyer_user_id":"2088722024105153","fund_change":"Y","gmt_refund_pay":"2023-12-10 21:16:57","out_trade_no":"1702214148175","refund_fee":"0.01","send_back_fee":"0.00","trade_no":"2023121022001405150501396838"},"sign":"OjQ2zYdxBhKiFzYMnQgDwvU5GboGig4/s4FSI27jCBXRFDRaNBI8vvSuQiEFZTO8utip0ARycUmA/6TTyQ0c74uWNeaPfYIQKf9KkHjj8MO0V+fqX4s9EjbYUQFgnzlIBiT27flt2bkhtS2TGjcGNJ7OmDrsoSrqpyzg2R8BDStgozMOGWifqOsks/sQOXhmR6+tLu4W+N+wk5+3ARZvXXPcVjWyI0404uNGf5CGSsinHKYxgyOwgla/GXDMbcT/fQcnaNPx6GepTFs1TwqJ9G7FofLZA3LrPPoM3DoK8KNRTM82DhrMMcuWHc7uermKnqkceIl6SW/aXtfY6Kr4eg=="}
        return response.getBody();
    }

    /**
     * 支付宝回调通知
     *
     * @param requeste
     * @return
     */
    @Override
    public String notifyPay(HttpServletRequest requeste) throws AlipayApiException {
        return alpayCallBack(requeste);
    }

    /**
     * 查询退款订单
     *
     * @param outTradeNo 商家订单号
     * @param tradeNo    支付宝交易单号
     * @return
     * @throws AlipayApiException
     */
    @Override
    public AlipayTradeFastpayRefundQueryResponse refundOrderQuery(String outTradeNo, String tradeNo) throws AlipayApiException {
        AlipayClient alipayClient =  createClient();
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", tradeNo);
        bizContent.put("out_request_no", outTradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("查询退款订单成功");
        } else {
            System.out.println("查询退款订单失败");
        }
        return response;
    }

    /**
     * 解析url参数
     * @param request
     * @return
     */
    private Map<String,String> parseRequestParam(HttpServletRequest request){
        Map<String,String[]> parama = request.getParameterMap();
        Map<String,String> map = new HashMap<String,String>(6);
        parama.keySet().stream().forEach(e->{map.put(e,parama.get(e)[0]);});
        return map;
    }

    private String alpayCallBack(HttpServletRequest request) throws AlipayApiException {
        if(!returnCheck( request)){
            return "check_fail";
        }

        Map<String,String> map = parseRequestParam( request);
        System.out.println("回调函数参数："+map);
        //http://hywne6.natappfree.cc/alipay_return?charset=UTF-8&
        // out_trade_no=1702202190135&method=alipay.trade.page.pay.return&
        // total_amount=0.01
        // &sign=ag9o55chdokN772cvce%2BRUwjFUeBRQuxpPPM%2B7n1yPzuJUqx4oAX4LSw2t1s8LEwv0KehLWtkR0%2BBdqXTlrhes7Le0bL1UOZ0jgbzXd9pZWE3gezBlMmk%2FK0BGX%2FfqieYJRT23GJ25lxSonyZdpeRwSgLJGf0OLxpInHhrO02yiJy1m4UxNk36q%2FcG0iU5nNpa4FDeG5Uf86ss3mKS765h%2FumULmX8s8LeLuXWOKQF5m8xGBjB3pyWZuz4rb3Vaa%2FhGrXEGUxTlBhLcAfyoVyojNSJN7pWPz30sFLRjVmmbM8MCTY%2Fzo4c%2FK29itSWUMxQ08DsFYGhMvZs6zG9C7lw%3D%3D&
        // trade_no=2023121022001405150501396837&auth_app_id=9021000132680387&version=1.0&app_id=9021000132680387&sign_type=RSA2&seller_id=2088721024133643&timestamp=2023-12-10+17%3A57%3A12
        if(StringUtils.isEmpty(map.get("refund_fee"))){
            AlipayTradeQueryResponse queryResponse = queryPayOrder(map.get("out_trade_no"), map.get("trade_no"));
            if(!queryResponse.isSuccess()){
                return "queryPayOrder_fail";
            }
            if(!map.get("total_amount").equals(queryResponse.getTotalAmount())){
                return "total_amount_fail";
            }
            // TODO: 2023/12/10 验证与数据库是否一样 if(!map.get("total_amount").equals("")){ }
            // 交易状态
            // TODO: 2023/12/10 全部验证通过 修改订单 支付完成
            if("TRADE_SUCCESS".equals(queryResponse.getTradeStatus())){
                // TODO: 2023/12/10  更新订单成功

            }else{
                // TODO: 2023/12/10  更新订单失败
            }
        }else{
            AlipayTradeFastpayRefundQueryResponse refundQueryResponse = refundOrderQuery(map.get("out_trade_no"), map.get("trade_no"));
            if(!refundQueryResponse.isSuccess()){
                return "queryPayOrder_fail";
            }
            // TODO: 2023/12/10 验证与数据库是否一样 if(!map.get("total_amount").equals("")){ }
            // 交易状态
            // TODO: 2023/12/10 全部验证通过 修改订单 支付完成
            if("REFUND_SUCCESS".equals(refundQueryResponse.getRefundStatus())){
                // TODO: 2023/12/10  更新订单成功

            }else{
                // TODO: 2023/12/10  更新订单失败
            }
        }
        System.out.println("回调成功");
        return "success";
    }
}
