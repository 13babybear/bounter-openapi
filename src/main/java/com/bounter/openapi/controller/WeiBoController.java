package com.bounter.openapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bounter.rest.client.httpclient.RestHttpClient;

/**
 * 新浪微博开放平台控制器
 * @author simon
 *
 */
@RestController
public class WeiBoController {
	
	private static final String appKey = "";
	private static final String appSecret = "";
	private static final String redirectUri = "http://127.0.0.1:8080/weibo/callback";
	
	private static final String baseAuthorizeUrl = "https://api.weibo.com/oauth2/authorize";
	private static final String baseTokenUrl = "https://api.weibo.com/oauth2/access_token";
	private static final String baseUserInfoUrl = "https://api.weibo.com/2/users/show.json";
	
	/**
	 * Step 1: 重定向到sina微博登录,请求授权码CODE
	 * https://api.weibo.com/oauth2/authorize?client_id=123050457758183&redirect_uri=http://www.example.com/response&response_type=code
	 * @param resp
	 * @throws Exception
	 */
	@RequestMapping("/weibo/login")
	public void weiboLogin(HttpServletResponse resp) throws Exception {
		String authorizeUrl =  baseAuthorizeUrl + "?client_id=" + appKey + "&redirect_uri=" + redirectUri + "&response_type=code";
		resp.sendRedirect(authorizeUrl);
	}
	
	/**
	 * Step 2: 登录回调，获取Code
	 * @param req
	 * @throws Exception
	 */
	@RequestMapping("/weibo/callback")
	public String qqCallback(HttpServletRequest req) throws Exception {
		String code = req.getParameter("code");
		//获取Access Token,并返回给页面
		return getAccessToken(code);
	}
	
	/**
	 * Step 3: 通过code获取access_token
	 * @param resp
	 * @param code  认证码
	 * @return
	 * @throws Exception 
	 */
	private String getAccessToken(String code) throws Exception {
		//拼接请求URL
		String tokenUrl = baseTokenUrl + "?client_id=" + appKey + "&client_secret=" + appSecret + "&grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri;
		
		//发送post请求,获取响应字符串
		String resp = RestHttpClient.sendHttpPostRequest(tokenUrl);
		
		return resp;
	}
	
	/**
	 * Step 4: 根据Token和用户ID获取用户信息
	 * access_token:2.00AZrTrB5jlucE809b31fc07I9H_XC, uid:1706396054
	 * @param access_token
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/weibo/userInfo")
	public String getUserInfo(String access_token, String uid) throws Exception {
		//拼接请求URL
		String userInfoUrl = baseUserInfoUrl + "?access_token=" + access_token + "&uid=" + uid;
		System.out.println(userInfoUrl);
		
		//发送请求,获取响应字符串
		String resp = RestHttpClient.sendHttpGetRequest(userInfoUrl);
		
		//返回响应字符串
		return resp;
	}
}
