package com.bounter.openapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bounter.rest.client.httpclient.RestHttpClient;

/**
 * 微信开放平台控制器
 * @author simon
 *
 */
@RestController
public class WeiXinController {
	
	private static final String appid = "";
	private static final String secret = "";
	private static final String redirectUri = "http://127.0.0.1:8080/weixin/callback";
	
	private static final String baseCodeUrl = "https://open.weixin.qq.com/connect/qrconnect";
	private static final String baseTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private static final String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo/access_token";
	
	/**
	 * Step 1: 重定向到微信登录,请求CODE
	 * https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
	 * @param resp
	 * @throws Exception
	 */
	@RequestMapping("/weixin/login")
	public void qqLogin(HttpServletResponse resp) throws Exception {
		String codeURL =  baseCodeUrl + "?appid=" + appid + "&redirect_uri=" + redirectUri + "&response_type=code&scope=snsapi_login";
		resp.sendRedirect(codeURL);
	}
	
	/**
	 * Step 2: 登录回调，获取Code
	 * @param req
	 * @throws Exception
	 */
	@RequestMapping("/weixin/callback")
	public String qqCallback(HttpServletRequest req) throws Exception {
		String code = req.getParameter("code");
		//获取Access Token,并返回给页面
		return getAccessToken(code);
	}
	
	/**
	 * Step 3: 通过code获取access_token
	 * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
	 * @param resp
	 * @param code  认证码
	 * @return
	 * @throws Exception 
	 */
	private String getAccessToken(String code) throws Exception {
		//拼接请求URL
		String tokenUrl = baseTokenUrl + "?appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
		
		//发送请求,获取响应字符串
		String resp = RestHttpClient.sendHttpGetRequest(tokenUrl);
		
		return resp;
	}
	
	/**
	 * Step 4: 通过access_token和openid调用接口
	 * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
	 * @param access_token
	 * @param openid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/weixin/userInfo")
	public String getUserInfo(String access_token, String openid) throws Exception {
		//拼接请求URL
		String userInfoUrl = baseUserInfoUrl + "?access_token=" + access_token + "&openid=" + openid;
		
		//发送请求,获取响应字符串
		String resp = RestHttpClient.sendHttpGetRequest(userInfoUrl);
		
		//返回响应字符串
		return resp;
	}
}
