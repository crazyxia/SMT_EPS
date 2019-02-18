package com.jimi.smt.eps_server.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.annotation.Role;
import com.jimi.smt.eps_server.controller.UserController;
import com.jimi.smt.eps_server.entity.User;
import com.jimi.smt.eps_server.entity.filler.UserToUserVOFiller;
import com.jimi.smt.eps_server.entity.vo.UserVO;
import com.jimi.smt.eps_server.mapper.UserMapper;
import com.jimi.smt.eps_server.util.TokenBox;

/**
 * 权限拦截器，对带有@Role注解的方法进行选择性放行
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class JurisdictionInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserToUserVOFiller userToUserVOFiller;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			// 如果是带@Open注解直接放行
			Open open = ((HandlerMethod) handler).getMethodAnnotation(Open.class);
			if (open != null) {
				return true;
			}
			// 如果不是POST则放行
			if (!request.getMethod().equals("POST")) {
				return true;
			}
			String token = request.getParameter(TokenBox.TOKEN_ID_KEY_NAME);
			UserVO tokenUserVO = TokenBox.get(token, UserController.SESSION_KEY_LOGIN_USER);

			if (tokenUserVO == null) {
				response.getWriter().println("{\"result\":\"failed_access_denied\"}");
				return false;
			}
			// 获取用户最新信息
			User user = userMapper.selectByPrimaryKey(tokenUserVO.getId());

			if (!user.getEnabled()) {
				response.getWriter().println("{\"result\":\"failed_access_denied\"}");
				return false;
			}

			if (user != null && user.getType() == 3) {
				return true;
			}

			// 如果没有注解，则拦截
			Role role = ((HandlerMethod) handler).getMethodAnnotation(Role.class);
			if (role == null) {
				response.getWriter().println("{\"result\":\"failed_access_denied\"}");
				return false;
			}
			// 角色与权限匹配则放行
			UserVO userVO = userToUserVOFiller.fill(user);
			for (Role.RoleType roleName : role.value()) {
				if (roleName.toString().equals(userVO.getTypeName())) {
					return true;
				}
			}
			// 其他拦截
			response.getWriter().println("{\"result\":\"failed_access_denied\"}");
			return false;
		}
		return true;
	}

}
