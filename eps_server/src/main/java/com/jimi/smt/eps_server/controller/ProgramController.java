package com.jimi.smt.eps_server.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.annotation.Role;
import com.jimi.smt.eps_server.annotation.Role.*;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.ResultJson;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.util.ResultUtil;


/**
 * 排位表控制器
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/program")
public class ProgramController {

	@Autowired
	private ProgramService programService;
	
	
	@RequestMapping("/goUpload")
	public ModelAndView goUpload() {
		return new ModelAndView("upload");
	}
	
	
	@RequestMapping("/goUpload2")
	public ModelAndView goUpload2() {
		return new ModelAndView("upload2");
	}
	
	
	@RequestMapping("/goManage")
	public ModelAndView goManage() {
		return new ModelAndView("program/goManage");
	}
	
	@Role({RoleType.ENGINEER, RoleType.PRODUCER})
	@ResponseBody
	@RequestMapping("/list")
	public List<ProgramVO> list(String programName, String fileName, String line, String workOrder, Integer state, String ordBy) {
		return programService.list(programName, fileName, line, workOrder, state, ordBy);
	}
	
	@Log
	@Role(RoleType.PRODUCER)
	@ResponseBody
	@RequestMapping("/start")
	public ResultUtil start(String id) {
		if(id == null) {
			return ResultUtil.failed("参数不足");
		}
		String result = null;
		if((result = programService.start(id)).equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	@Log
	@Role(RoleType.PRODUCER)
	@ResponseBody
	@RequestMapping("/finish")
	public ResultUtil finish(String id) {
		if(id == null) {
			return ResultUtil.failed("参数不足");
		}
		if(programService.finish(id)) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed();
		}
	}
	
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/cancel")
	public ResultUtil cancel(String id) {
		if(id == null) {
			return ResultUtil.failed("参数不足");
		}
		if(programService.cancel(id)) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed();
		}
	}
	
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/listItem")
	public List<ProgramItemVO> listItem(String id) {
		if(id == null) {
			ResultUtil.failed("参数不足");
			return null;
		}
		return programService.listItem(id);
	}
	
	
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/updateItem")
	public ResultUtil updateItem(String operations) {
		//去除isAlternative
		operations = operations.replaceAll("\"isAlternative\"[^\\S]*:[^\\S]*\".+\",?", "");
		List<EditProgramItemBO> BOs = null;
		try {
			BOs = JSONArray.parseArray(operations, EditProgramItemBO.class);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.failed("参数格式不正确");
		}
		if(BOs == null || BOs.isEmpty()) {
			return ResultUtil.failed("参数为空");
		}
		try {
			programService.updateItem(BOs);
			return ResultUtil.succeed();
		} catch (Exception e) {
			return ResultUtil.failed(e.getMessage());
		}
	}
	
	
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/upload")
	public ResultUtil upload(MultipartFile  programFile, Integer boardType) {
		if(programFile == null || boardType == null) {
			return ResultUtil.failed("参数不足");
		}
		
		//文件名检查
		String originalFileName = programFile.getOriginalFilename();
		if(!originalFileName.endsWith(".xls") && !originalFileName.endsWith(".xlsx")){
			return ResultUtil.failed("上传失败，必须为xls\\xlsx格式的文件");
		}
		
		List<Map<String, Object>> resultList = null;
		
		//格式检查
		try {
			resultList = programService.upload(programFile, boardType);
		} catch (IOException e) {
			return ResultUtil.failed("上传失败，IO错误，请重试，或联系开发者",e);
		} catch (RuntimeException e) {
			return ResultUtil.failed("上传失败，解析文件时出错，请参考排位表标准格式规范：http://wx.jimi-iot.com/eps_server/static/standard.docx",e);
		}
		
		//结果检查
		StringBuffer sb = new StringBuffer();
		boolean isSucceed = true;
		for (Map<String, Object> result : resultList) {
			int realParseNum = (int)result.get("real_parse_num");
			int planParseNum = (int)result.get("plan_parse_num");
			String actionName = (String)result.get("action_name");
			if(realParseNum == 0) {
				isSucceed = false;
				sb.append("操作失败，请检查表格内是否有空行，若有去掉该行再重试\n");
			}else if(realParseNum < planParseNum){
				isSucceed = false;
				sb.append(actionName + "完成，共检测到"+ planParseNum +"张表，但只解析成功"+ realParseNum +"张表，请检查是否有空表或表中是否有空行\n");
			}else {
				sb.append(actionName + "完成，共解析到"+ realParseNum +"张表\n");
			}
		}
		String string = sb.substring(0, sb.length() - 1);
		if(isSucceed) {
			return ResultUtil.succeed(string);
		}else {
			return ResultUtil.failed(string);
		}
	}
	
	
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/switch")
	public ResultUtil switchWorkOrder(String line, String workOrder, Integer boardType) {
		String result = programService.switchWorkOrder(line, workOrder, boardType);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/operate")
	public ResultUtil operate(String line, String workOrder, Integer boardType, Integer type, String lineseat, String materialNo, String scanLineseat, String scanMaterialNo, Integer operationResult) {
		String result = programService.operate(line, workOrder, boardType, type, lineseat, materialNo, scanLineseat, scanMaterialNo, operationResult);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/reset")
	public ResultUtil reset(String line, String workOrder, Integer boardType) {
		String result = programService.reset(line, workOrder, boardType);
		if(result.equals("succeed")) {
			return ResultUtil.succeed();
		}else {
			return ResultUtil.failed(result);
		}
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/selectWorkingProgram")
	public ResultJson selectWorkingProgram(String line){
		int result = programService.selectLine(line);
		ResultJson resultJson = new ResultJson();
		if(result == 0) {
			resultJson.setCode(0);
			resultJson.setMsg("此线号不存在");
			return resultJson;
		}
		List<Program> list = programService.selectWorkingProgram(line);		
		if(list.size() == 0) {
			resultJson.setCode(0);
			resultJson.setMsg("此线号不存在工单");
		}else {
			resultJson.setCode(1);
			resultJson.setMsg("此线号存在工单");
			resultJson.setData(list);		
		}
		return resultJson;
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/selectProgramItem")
	public ResultJson selectProgramItem(String line, String workOrder, Integer boardType) {
		List<ProgramItem> list = programService.selectProgramItem(line, workOrder, boardType);		
		ResultJson resultJson = new ResultJson();
		if(list.size()==0) {
			resultJson.setCode(0);
			resultJson.setMsg("工单不存在");
		}else {
			resultJson.setCode(1);
			resultJson.setMsg("工单存在");
			resultJson.setData(list);		
		}
		return resultJson;
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/updateItemVisit")
	public ResultJson updateItemVisit(@RequestBody ProgramItemVisit programItemVisit) {
		int result = programService.updateItemVisit(programItemVisit);
		ResultJson resultJson = new ResultJson();
		if(result > 0) {
			resultJson.setCode(1);
			resultJson.setMsg("更新成功");
		}else {
			resultJson.setCode(0);
			resultJson.setMsg("更新失败");
		}
		return resultJson;
	}
	
	@Open
	@ResponseBody
	@RequestMapping("/resetCheckAll")
	public int resetCheckAll(String programId) {
		if(programId == null || programId.equals("")) {
			return 0;
		}
		int result = programService.resetCheckAll(programId);
		if(result > 0) {
			return 1;
		}else {
			return 0;
		}	
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/checkIsReset")
	public int checkIsReset(String programId, int type) {				
		return programService.checkIsReset(programId, type);		
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/isAllDone")
	public int isAllDone(String programId, int type) {			
		return programService.isAllDone(programId, type);		
	}
	
	
	@Open
	@ResponseBody
	@RequestMapping("/isChangeSucceed")
	public int isChangeSucceed(String programId, String lineseat) {				
		return programService.isChangeSucceed(programId, lineseat);		
	}
}
