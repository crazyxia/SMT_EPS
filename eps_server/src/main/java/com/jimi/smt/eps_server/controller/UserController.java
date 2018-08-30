package com.jimi.smt.eps_server.controller;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.ResultJson;
import com.jimi.smt.eps_server.entity.User;
import com.jimi.smt.eps_server.entity.vo.UserVO;
import com.jimi.smt.eps_server.service.UserService;
import com.jimi.smt.eps_server.util.QRCodeUtil;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.TokenBox;

import cc.darhao.dautils.api.FontImageUtil;

/**
 * 用户控制器
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	public static final String SESSION_KEY_LOGIN_USER = "loginUser";

	@Autowired
	private UserService userService;

	@RequestMapping("/goManage")
	public ModelAndView goManage() {
		return new ModelAndView("user/goManage");
	}

	@Open
	@RequestMapping("/goLogin")
	public ModelAndView goLogin() {
		return new ModelAndView("user/goLogin");
	}

	@Log
	@ResponseBody
	@RequestMapping("/add")
	public ResultUtil add(String id, Integer classType, String name, Integer type, String password) {
		if (id == null && type == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result = userService.add(id, classType, name, type, password);
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	@ResponseBody
	@RequestMapping("/list")
	public List<UserVO> list(String id, Integer classType, String name, Integer type, String password, String orderBy,
			Boolean enabled) {
		return userService.list(id, classType, name, type, orderBy, enabled);
	}

	@Log
	@ResponseBody
	@RequestMapping("/update")
	public ResultUtil update(String id, Integer classType, String name, Integer type, String password,
			Boolean enabled) {
		if (id == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result = userService.update(id, classType, name, type, password, enabled);
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	@Open
	@ResponseBody
	@RequestMapping("/login")
	public ResultUtil login(String id, String password, HttpServletRequest request) {
		if (id == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		UserVO user = userService.login(id, password);
		if(user == null || user.getType() < 3) {
			return ResultUtil.failed("failed_not_admin");
		}
		if(user.getEnabled() == false) {			
			return ResultUtil.failed("failed_not_enabled");
		}
		if(user.getPassword() != null && !user.getPassword().equals(password)) {
			return ResultUtil.failed("failed_wrong_password");
		}
		String tokenId = request.getParameter(TokenBox.TOKEN_ID_KEY_NAME);
		if(tokenId != null) {
			UserVO user2 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
			if(user2 != null && user.getId().equals(user2.getId())) {
				return ResultUtil.failed("请勿重复登录！");
			}
		}
		tokenId = TokenBox.createTokenId();
		user.setTokenId(tokenId);
		user.setPassword("***");
		TokenBox.put(tokenId, SESSION_KEY_LOGIN_USER, user);
		return ResultUtil.succeed(user);
	}

	@Open
	@RequestMapping("/getCodePic")
	public ModelAndView getCodePic(HttpServletResponse response, HttpSession session, String id, Integer size)
			throws Exception {
		if (id == null || id.equals("")) {
			return new ModelAndView("user/goManage");
		}
		// 从session读取size
		if (size == null) {
			if (session.getAttribute("code_size") == null) {
				size = 300;
			} else {
				size = (Integer) session.getAttribute("code_size");
			}
		}
		// 把size存到session
		session.setAttribute("code_size", size);
		response.setContentType("image/png");
		// 生成加密信息
		String encodedId = new String(Base64.getEncoder().encode(id.getBytes()));
		encodedId += "?";
		// 生成二维码
		Font font = new Font("宋体", Font.PLAIN, 128 / id.length());
		FileOutputStream fileOutputStream = new FileOutputStream(
				new File(session.getServletContext().getRealPath("/static/temp.png")));
		BufferedImage textImage = FontImageUtil.createImage(id, font, 64, 64);
		QRCodeUtil.encode(encodedId, textImage, "", fileOutputStream, false);
		// 传递参数
		ModelAndView modelAndView = new ModelAndView("user/getCodePic");
		modelAndView.addObject("id", id);
		modelAndView.addObject("size", size);
		return modelAndView;
	}

	@Open
	@ResponseBody
	@RequestMapping("selectById")
	public ResultJson selectUserById(String id) {
		User user = userService.selectUserById(id);
		ResultJson resultJson = new ResultJson();		
		if (user == null) {
			resultJson.setCode(0);
			resultJson.setMsg("操作员不存在");
		} else {
			if (!user.getEnabled()) {
				resultJson.setCode(-1);
				resultJson.setMsg("操作员离职");
			} else {
				resultJson.setCode(1);
				resultJson.setMsg("操作员存在");				
				resultJson.setData(user);
			}
		}
		return resultJson;
	}
}
