package com.jimi.smt.eps_server.controller;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.User;
import com.jimi.smt.eps_server.entity.filler.UserToUserVOFiller;
import com.jimi.smt.eps_server.entity.vo.PageVO;
import com.jimi.smt.eps_server.entity.vo.UserVO;
import com.jimi.smt.eps_server.service.UserService;
import com.jimi.smt.eps_server.util.QRCodeUtil;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.ResultUtil2;
import com.jimi.smt.eps_server.util.TokenBox;
import org.apache.commons.io.*;
import cc.darhao.dautils.api.FontImageUtil;

/**
 * 用户控制器
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/user")
public class UserController {

	private static final Object lock = new Object();

	public static final String SESSION_KEY_LOGIN_USER = "loginUser";
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserToUserVOFiller filler;

	
	@Log
	@ResponseBody
	@RequestMapping("/add")
	public ResultUtil add(String id, Integer classType, String name, Integer type, String password) {
		if (id == null && type == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result;
		try {
			result = userService.add(id, classType, name, type, password);
		} catch (Exception e) {
			return ResultUtil.failed("更新失败，请检查数据格式是否正确或者长度是否过长");
		}
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	
	@ResponseBody
	@RequestMapping("/list")
	public PageVO<UserVO> list(String id, Integer classType, String name, Integer type, String password, String orderBy, Boolean enabled, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<UserVO> pageVO = new PageVO<UserVO>();
		pageVO.setList(userService.list(id, classType, name, type, orderBy, enabled, page,password));
		pageVO.setPage(page);
		return pageVO;
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
		String result;
		try {
			result = userService.update(id, classType, name, type, password, enabled);
		} catch (Exception e) {
			return ResultUtil.failed("更新失败，请检查数据格式是否正确或者长度是否过长");
		}
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
		if(id == null || password == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		if("".equals(password)) {
			return ResultUtil.failed("默认密码为000000，请输入你的密码");
		}
		User user = userService.selectUserById(id);
		if (user == null) {
			return ResultUtil.failed("failed_not_found");
		}
		if (user.getEnabled() == false) {
			return ResultUtil.failed("failed_not_enabled");
		}
		if (user.getPassword() != null && !user.getPassword().equals(password)) {
			return ResultUtil.failed("failed_wrong_password");
		}
		String tokenId = request.getParameter(TokenBox.TOKEN_ID_KEY_NAME);
		if (tokenId != null) {
			UserVO user2 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
			if (user2 != null && user.getId().equals(user2.getId())) {
				return ResultUtil.failed("请勿重复登录！");
			}
		}
		tokenId = TokenBox.createTokenId();
		UserVO userVO = filler.fill(user);
		userVO.setTokenId(tokenId);
		TokenBox.put(tokenId, SESSION_KEY_LOGIN_USER, userVO);
		return ResultUtil.succeed("200", userVO);
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/getCodePic")
	public ResultUtil getCodePic(HttpSession session, String id) throws Exception {
		if (id == null || id.equals("")) {
			return ResultUtil.failed("参数不足");
		}
		// 生成加密信息
		String encodedId = new String(Base64.getEncoder().encode(id.getBytes()));
		encodedId += "?";
		// 生成二维码
		Font font = new Font("宋体", Font.PLAIN, 128 / id.length());
		FileOutputStream fileOutputStream = null;
		String fileName = "";
		synchronized (lock) {
			fileName = "codePic";
			String filePath = session.getServletContext().getRealPath("/static/png");
			File file = new File(filePath);
			if (file.exists()) {
				FileUtils.deleteDirectory(file);
			}
			file.mkdirs();
			fileName = fileName + System.currentTimeMillis() + ".png";
			fileOutputStream = new FileOutputStream(new File(filePath + File.separator + fileName));
			BufferedImage textImage = FontImageUtil.createImage(id, font, 64, 64);
			QRCodeUtil.encode(encodedId, textImage, "", fileOutputStream, false);
			fileOutputStream.close();
		}
		return ResultUtil.succeed("/static/png/" + fileName);
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/selectById")
	public ResultUtil2 selectUserById(String id) {
		User user = userService.selectUserById(id);
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (user == null) {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("操作员不存在");
		} else {
			if (!user.getEnabled()) {
				resultUtil2.setCode(-1);
				resultUtil2.setMsg("操作员离职");
			} else {
				resultUtil2.setCode(1);
				resultUtil2.setMsg("操作员存在");
				resultUtil2.setData(user);
			}
		}
		return resultUtil2;
	}

	
	public static File[] findFile(String filePath, String fileName) {
		File dir = new File(filePath);
		File[] tempFile = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {

				String name = file.getName();
				if (name.contains(fileName)) {
					return true;
				}
				return false;
			}
		});
		if (tempFile == null || tempFile.length <= 0) {
			return null;
		}
		return tempFile;
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/select")
	public User select(String id) {
		User user = userService.selectUserById(id);
		if (user != null && user.getEnabled()) {
			return user;
		}
		return null;
	}
	
	@Open
	@ResponseBody
	@RequestMapping("/updatePassword")
	public ResultUtil2 updatePassword(String id, String oldPassword,String newPassword) {
		if (id == null || oldPassword == null || newPassword == null) {
			return new ResultUtil2(400,"参数不足");
		}
		if("".equals(id) || "".equals(oldPassword) || "".equals(newPassword)) {
			return new ResultUtil2(400,"参数不能为空");
		}
		User user = userService.selectUserById(id);
		if (user == null) {
			return new ResultUtil2(400,"账号不存在");
		}
		if (user.getEnabled() == false) {
			return new ResultUtil2(400,"工人已离职");
		}
		if(!oldPassword.equals(user.getPassword())) {
			return new ResultUtil2(400,"旧密码错误");
		}
		String result;
		try {
			result = userService.updatePassword(id, newPassword);
		} catch (Exception e) {
			return new ResultUtil2(400,"修改失败,请检查所传参数");
		}
		if("succeed".equals(result)) {
			return new ResultUtil2(200,"操作成功");
		}else {
			return new ResultUtil2(400,"修改失败");
		}
	}
}
