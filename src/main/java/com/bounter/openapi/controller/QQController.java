package com.bounter.openapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bounter.openapi.entity.QQToken;
import com.bounter.rest.client.httpclient.RestHttpClient;

/**
 * qq开放平台控制器
 * @author simon
 *
 */
@RestController
public class QQController {
	
	private static final String client_id = "";
	private static final String client_secret = "";
	private static final String redirectURL = "http://127.0.0.1:8080/qq/callback";
	
	private static final String baseAuthorizeURL = "https://graph.qq.com/oauth2.0/authorize";
	private static final String baseAccessTokenURL = "https://graph.qq.com/oauth2.0/token";
	private static final String baseOpenIdURL = "https://graph.qq.com/oauth2.0/me";
	private static final String baseUserURL = "https://graph.qq.com/user";
	
	/**
	 * Step 1: 重定向到QQ登录
	 * authorizeURL:"https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=1106292090&redirect_uri=http://127.0.0.1:8080/qq/callback&state=test";
	 * @param resp
	 * @throws Exception
	 */
	@RequestMapping("/qq/login")
	public void qqLogin(HttpServletResponse resp) throws Exception {
		String authorizeURL =  baseAuthorizeURL + "?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectURL + "&state=test";
		resp.sendRedirect(authorizeURL);
	}
	
	/**
	 * Step 2: 登录回调，获取Authorization Code
	 * @param req
	 * @throws Exception
	 */
	@RequestMapping("/qq/callback")
	public QQToken qqCallback(HttpServletRequest req) throws Exception {
		String code = req.getParameter("code");
		//获取Access Token,并返回给页面
		return getAccessToken(code);
	}
	
	/**
	 * Step 3: 通过Authorization Code获取Access Token
	 * accessTokenURL:"https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=1106292090&client_secret=0RCBq5tEs2oSrVuJ&code=54972E2E856D1080E9EC1500D042B480&redirect_uri=http://127.0.0.1:8080/qq/callback";
	 * @param resp
	 * @param code  认证码
	 * @return
	 * @throws Exception 
	 */
	private QQToken getAccessToken(String code) throws Exception {
		//拼接请求URL
		String accessTokenURL = baseAccessTokenURL + "?grant_type=authorization_code&client_id=" + client_id + "&client_secret=" + client_secret + "&code=" + code + "&redirect_uri=" + redirectURL;
		
		//发送请求,获取响应字符串“access_token=412BB47FDFACFB466DFA7D64EC734BFE&expires_in=7776000&refresh_token=9DB37F281C26C5B425DF70D7BB058DD9”
		String resp = RestHttpClient.sendHttpGetRequest(accessTokenURL);
		
		//解析响应字符串
		QQToken qqToken = null;
		if(resp != null && resp.indexOf("access_token") != -1) {
			qqToken = new QQToken();
			String[] keyValuePairs = resp.split("&");
			qqToken.setAccessToken(keyValuePairs[0].split("=")[1]);
			qqToken.setExpiresIn(keyValuePairs[1].split("=")[1]);
			qqToken.setRefreshToken(keyValuePairs[2].split("=")[1]);
		}
		
		return qqToken;
	}
	
	/**
	 * Step 4: 通过Access Token，得到对应用户身份的OpenID
	 * OpenID是此网站上或应用中唯一对应用户身份的标识，网站或应用可将此ID进行存储，便于用户下次登录时辨识其身份，或将其与用户在网站上或应用中的原有账号进行绑定
	 * @param accessToken
	 * @throws Exception
	 */
	@RequestMapping("/qq/openId")
	public String getOpenId(String accessToken) throws Exception {
		//拼接请求URL，https://graph.qq.com/oauth2.0/me?access_token=412BB47FDFACFB466DFA7D64EC734BFE
		String openIdURL =  baseOpenIdURL + "?access_token=" + accessToken;
		
		//发送请求,获取响应字符串,如：callback( {"client_id":"1106292090","openid":"F7F4DE3E2261DAB42144ADADF0700E3F"} )
		String resp = RestHttpClient.sendHttpGetRequest(openIdURL);
		
		//解析响应字符串
		String respJson = resp.substring(resp.indexOf("(") + 1, resp.indexOf(")"));
		
		return respJson;
	}
	
	/**
	 * Step 5: 调用OpenAPI来获取用户个人信息
	 * @param access_token
	 * @param oauth_consumer_key
	 * @param openid
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/qq/userInfo")
	public String getUserInfo(String access_token, String oauth_consumer_key, String openid) throws Exception {
		//拼接请求URL
		String userInfoURL = baseUserURL + "/get_user_info?access_token=" + access_token + "&oauth_consumer_key=" + oauth_consumer_key + "&openid=" + openid;
		
		//发送请求,获取响应字符串
		String resp = RestHttpClient.sendHttpGetRequest(userInfoURL);
		
		//返回响应字符串
		return resp;
	}
}
