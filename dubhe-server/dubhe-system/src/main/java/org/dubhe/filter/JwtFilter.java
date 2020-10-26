/**
 * Copyright 2019-2020 Zheng Jie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dubhe.filter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.dubhe.base.DataResponseBody;
import org.dubhe.base.ResponseCode;
import org.dubhe.constatnts.UserConstant;
import org.dubhe.enums.LogEnum;
import org.dubhe.exception.BaseErrorCode;
import org.dubhe.shiro.JwtToken;
import org.dubhe.utils.JwtUtils;
import org.dubhe.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * @description 自定义的认证过滤器，用来拦截Header中携带 JWT token的请求
 * @date 2020-06-01
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 前置处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 后置处理
     */
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        // 添加跨域支持
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
    }

    /**
     * 过滤器拦截请求的入口方法
     * 返回 true 则允许访问
     * 返回false 则禁止访问，会进入 onAccessDenied()
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 用来检测Header中是否包含 JWT token 字段
        if (this.isLoginRequest(request, response)) {
            return false;
        }

        boolean allowed = false;
        try {
            // 检测Header里的 JWT token内容是否正确，尝试使用 token进行登录
            allowed = executeLogin(request, response);

        } catch (Exception e) {
            LogUtil.error(LogEnum.SYS_ERR, "JwtFilter isAccessAllowed error:", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 检测Header中是否包含 JWT token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getHeader(JwtUtils.AUTH_HEADER) == null;
    }

    /**
     * 身份验证,检查 JWT token 是否合法
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
                    + "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    /**
     * 从 Header 里提取 JWT token
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authorization = httpServletRequest.getHeader(JwtUtils.AUTH_HEADER);
        JwtToken token = new JwtToken(authorization);
        return token;
    }

    /**
     * isAccessAllowed()方法返回false，会进入该方法，表示拒绝访问
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(servletRequest);
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = httpResponse.getWriter();
        DataResponseBody dataResponseBody;
        if (ObjectUtil.isNull(httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY))) {
            httpResponse.setStatus(HttpStatus.OK.value());
            dataResponseBody = new DataResponseBody(BaseErrorCode.SYSTEM_USER_TOKEN_INFO_IS_NULL.getCode(),
                    BaseErrorCode.SYSTEM_USER_TOKEN_INFO_IS_NULL.getMsg());
        } else {
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            dataResponseBody = new DataResponseBody(ResponseCode.UNAUTHORIZED, BaseErrorCode.UNAUTHORIZED.getMsg());
        }

        writer.write(JSON.toJSONString(dataResponseBody));
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpResponse);
        return false;
    }

    /**
     * Shiro 利用 JWT token 登录成功，会进入该方法
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JwtToken) {
            String tokenStr = (String) token.getCredentials();
            if (JwtUtils.isTokenExpired(tokenStr)) {
                newToken = JwtUtils.refreshTokenExpired(tokenStr);
                if (newToken == null) {
                    log.error("token已过期");
                    return false;
                }
            }
        }
        if (newToken != null) {
            httpResponse.setHeader(JwtUtils.AUTH_HEADER, newToken);
        }
        return true;
    }

    /**
     * Shiro 利用 JWT token 登录失败，会进入该方法
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
                                     ServletResponse response) {
        // 此处直接返回 false ，交给后面的  onAccessDenied()方法进行处理
        return false;
    }

    /**
     * 添加跨域支持
     */
    protected void fillCorsHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
                httpServletRequest.getHeader("Access-Control-Request-Headers"));
    }
}
