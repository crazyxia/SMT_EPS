package com.jimi.smt.eps_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Role;
import com.jimi.smt.eps_server.annotation.Role.RoleType;
import com.jimi.smt.eps_server.entity.MaterialInfo;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.vo.PageVO;
import com.jimi.smt.eps_server.service.MaterialService;
import com.jimi.smt.eps_server.util.ResultUtil;

@Controller
@RequestMapping("/material")
public class MaterialController {

	@Autowired
	private MaterialService materialService;
		
	
	@Role(RoleType.IPQC)
	@Log
	@ResponseBody
	@RequestMapping("/add")
	public ResultUtil add(String materialNo, Integer perifdOfValidity) {
		if(materialNo == null &&  perifdOfValidity == null){
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result = materialService.add(materialNo, perifdOfValidity);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Role(RoleType.IPQC)
	@Log
	@ResponseBody
	@RequestMapping("/update")
	public ResultUtil update(Integer id, Integer perifdOfValidity) {
		if(id == null && perifdOfValidity == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result = materialService.update(id, perifdOfValidity);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Role(RoleType.IPQC)
	@Log
	@ResponseBody
	@RequestMapping("/delete")
	public ResultUtil delete(Integer id) {
		if(id == null) {
			ResultUtil.failed("参数不足");
			return ResultUtil.failed();
		}
		String result = materialService.delete(id);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Role(RoleType.IPQC)
	@Log
	@ResponseBody
	@RequestMapping("/list")
	public PageVO<MaterialInfo> list(Integer id, String materialNo, Integer perifdOfValidity, String orderBy, Integer currentPage, Integer pageSize) {		
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<MaterialInfo> pageVO = new PageVO<MaterialInfo>();
		pageVO.setList(materialService.list(id, materialNo, perifdOfValidity, orderBy, page));
		pageVO.setPage(page);
		return pageVO;
	}
}
