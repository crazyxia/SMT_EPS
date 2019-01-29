package com.jimi.smt.eps_server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.annotation.Role;
import com.jimi.smt.eps_server.annotation.Role.*;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.vo.PageVO;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;
import com.jimi.smt.eps_server.service.LineService;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.ResultUtil2;

/**
 * 排位表控制器
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/program")
public class ProgramController {

	@Autowired
	private ProgramService programService;
	@Autowired
	private LineService lineService;

	
	/**@author HCJ
	 * 查询站位表
	 * @date 2019年1月29日 下午5:39:43
	 */
	@Role({ RoleType.ENGINEER, RoleType.PRODUCER })
	@ResponseBody
	@RequestMapping("/list")
	public PageVO<ProgramVO> list(String programName, String fileName, Integer line, String workOrder, Integer state, String ordBy, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<ProgramVO> pageVO = new PageVO<ProgramVO>();
		pageVO.setPage(page);
		pageVO.setList(programService.list(programName, fileName, line, workOrder, state, ordBy, page));
		return pageVO;
	}

	
	/**@author HCJ
	 * 开始工单
	 * @date 2019年1月29日 下午5:39:55
	 */
	@Log
	@Role(RoleType.PRODUCER)
	@ResponseBody
	@RequestMapping("/start")
	public ResultUtil start(String id) {
		if (id == null) {
			return ResultUtil.failed("参数不足");
		}
		String result = null;
		if ((result = programService.start(id)).equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	
	/**@author HCJ
	 * 完成工单
	 * @date 2019年1月29日 下午5:40:09
	 */
	@Log
	@Role(RoleType.PRODUCER)
	@ResponseBody
	@RequestMapping("/finish")
	public ResultUtil finish(String id) {
		if (id == null) {
			return ResultUtil.failed("参数不足");
		}
		if (programService.finish(id)) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed();
		}
	}

	
	/**@author HCJ
	 * 作废工单
	 * @date 2019年1月29日 下午5:40:19
	 */
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/cancel")
	public ResultUtil cancel(String id) {
		if (id == null) {
			return ResultUtil.failed("参数不足");
		}
		if (programService.cancel(id)) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed();
		}
	}

	
	/**@author HCJ
	 * 查询对应站位表的详情
	 * @date 2019年1月29日 下午5:40:31
	 */
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/listItem")
	public List<ProgramItemVO> listItem(String id) {
		if (id == null) {
			ResultUtil.failed("参数不足");
			return null;
		}	
		return programService.listItem(id);		
	}

	
	/**@author HCJ
	 * 更新站位表详情
	 * @date 2019年1月29日 下午5:41:01
	 */
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/updateItem")
	public ResultUtil updateItem(String operations) {
		// 去除isAlternative
		operations = operations.replaceAll("\"isAlternative\"[^\\S]*:[^\\S]*\".+\",?", "");
		List<EditProgramItemBO> BOs = null;
		try {
			BOs = JSONArray.parseArray(operations, EditProgramItemBO.class);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.failed("参数格式不正确");
		}
		if (BOs == null || BOs.isEmpty()) {
			return ResultUtil.failed("参数为空");
		}
		try {
			programService.updateItem(BOs);
			return ResultUtil.succeed();
		} catch (Exception e) {
			return ResultUtil.failed(e.getMessage());
		}
	}

	
	/**@author HCJ
	 * 上传站位表
	 * @date 2019年1月29日 下午5:41:34
	 */
	@Log
	@Role(RoleType.ENGINEER)
	@ResponseBody
	@RequestMapping("/upload")
	public ResultUtil upload(MultipartFile programFile, Integer boardType) {
		if (programFile == null || boardType == null) {
			return ResultUtil.failed("参数不足");
		}

		// 文件名检查
		String originalFileName = programFile.getOriginalFilename();
		if (!originalFileName.endsWith(".xls") && !originalFileName.endsWith(".xlsx")) {
			return ResultUtil.failed("上传失败，必须为xls\\xlsx格式的文件");
		}

		List<Map<String, Object>> resultList = null;

		// 格式检查
		try {
			resultList = programService.upload(programFile, boardType);
		} catch (IOException e) {
			return ResultUtil.failed("上传失败，IO错误，请重试，或联系开发者", e);
		}

		// 结果检查
		StringBuffer sb = new StringBuffer();
		boolean isSucceed = true;
		for (Map<String, Object> result : resultList) {
			int realParseNum = (int) result.get("realParseNum");
			int planParseNum = (int) result.get("planParseNum");
			String actionName = (String) result.get("actionName");
			List<String> errorInfos = (List<String>) result.get("errorInfos");
			if (errorInfos != null && errorInfos.size() > 0) {
				isSucceed = false;
				if (realParseNum == 0) {
					sb.append("操作失败，请检查是否有空表或表中是否有空行，错误信息如下:\n");
				} else if (realParseNum < planParseNum) {
					sb.append(actionName + "完成，共检测到" + planParseNum + "张表，但只解析成功" + realParseNum + "张表，请检查是否有空表或表中是否有空行，错误信息如下:\n");
				} else {
					sb.append("请检查表中是否有空行或者内容长度是否过长，错误信息如下:\n");
				}
				for (String errorInfo : errorInfos) {
					sb.append(errorInfo + "\n");
				}
			} else {
				sb.append(actionName + "完成，共解析到" + realParseNum + "张表\n");
			}
		}
		if (isSucceed) {
			return ResultUtil.succeed(sb.toString());
		} else {
			return ResultUtil.failed(sb.toString());
		}
	}

	
	/**@author HCJ
	 * Display切换工单时更新数据库记录
	 * @date 2019年1月29日 下午5:42:11
	 */
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/switch")
	public ResultUtil switchWorkOrder(String line, String workOrder, Integer boardType) {
		String result = programService.switchWorkOrder(line, workOrder, boardType);
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	
	/**@author HCJ
	 * 更新操作记录
	 * @date 2019年1月29日 下午5:42:43
	 */
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/operate")
	public ResultUtil operate(String line, String workOrder, Integer boardType, Integer type, String lineseat,
			String materialNo, String scanLineseat, String scanMaterialNo, Integer operationResult) {
		String result = programService.operate(line, workOrder, boardType, type, lineseat, scanLineseat, scanMaterialNo,
				operationResult);
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	
	/**@author HCJ
	 * 重置工单操作记录
	 * @date 2019年1月29日 下午5:42:57
	 */
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/reset")
	public ResultUtil reset(String line, String workOrder, Integer boardType) {
		String result = programService.reset(line, workOrder, boardType);
		if (result.equals("succeed")) {
			return ResultUtil.succeed();
		} else {
			return ResultUtil.failed(result);
		}
	}

	
	/**@author HCJ
	 * 根据产线名称查询所有进行中工单
	 * @date 2019年1月29日 下午5:43:17
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectWorkingProgram")
	public ResultUtil2 selectWorkingProgram(String line) {
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (lineService.getLineIdByName(line) == null) {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("此线号不存在");
			return resultUtil2;
		}
		List<ProgramVO> list = programService.selectWorkingProgram(line);
		if (list.size() == 0) {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("此线号不存在工单");
		} else {
			resultUtil2.setCode(1);
			resultUtil2.setMsg("此线号存在工单");
			resultUtil2.setData(list);
		}
		return resultUtil2;
	}

	
	/**@author HCJ
	 * 根据站位表id查询站位表详情
	 * @date 2019年1月29日 下午5:43:43
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectProgramItem")
	public ResultUtil2 selectProgramItem(String programId) {
		List<ProgramItemVO> list = programService.listItem(programId);
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (list.size() == 0) {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("工单不存在");
		} else {
			resultUtil2.setCode(1);
			resultUtil2.setMsg("工单存在");
			resultUtil2.setData(list);
		}
		return resultUtil2;
	}

	
	/**@author HCJ
	 * 更新操作记录
	 * @date 2019年1月29日 下午5:44:03
	 */
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/updateItemVisit")
	public ResultUtil2 updateItemVisit(@RequestBody ProgramItemVisit programItemVisit) {
		int result = programService.updateItemVisit(programItemVisit);
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (result > 0) {
			resultUtil2.setCode(1);
			resultUtil2.setMsg("更新成功");
		} else {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("更新失败");
		}
		return resultUtil2;
	}

	
	/**@author HCJ
	 * 首检完成后重置全检时间和全检结果
	 * @date 2019年1月29日 下午5:44:49
	 */
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/resetCheckAll")
	public int resetCheckAll(String programId) {
		if (programId == null || programId.equals("")) {
			return 0;
		}
		int result = programService.resetCheckAll(programId);
		if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	
	/**@author HCJ
	 * 判断工单是否重置了
	 * @date 2019年1月29日 下午5:45:04
	 */
	@Open
	@ResponseBody
	@RequestMapping("/checkIsReset")
	public int checkIsReset(String programId, int type) {
		return programService.checkIsReset(programId, type);
	}

	
	/**@author HCJ
	 * 判断某个工单是否全部完成某项操作
	 * @date 2019年1月29日 下午5:45:15
	 */
	@Open
	@ResponseBody
	@RequestMapping("/isAllDone")
	public ResultUtil2 isAllDone(String programId, String type) {
		return programService.isAllDone(programId, type);
	}

	
	/**@author HCJ
	 * 核料时判断某个站位是否换料成功
	 * @date 2019年1月29日 下午5:45:45
	 */
	@Open
	@ResponseBody
	@RequestMapping("/isChangeSucceed")
	public int isChangeSucceed(String programId, String lineseat) {
		return programService.isChangeSucceed(programId, lineseat);
	}

	
	/**@author HCJ
	 * 返回产线所有进行中的工单的值
	 * @date 2019年1月29日 下午5:46:01
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectWorkingOrder")
	public List<String> selectWorkingOrder(String line) {
		return programService.selectWorkingOrder(line);
	}

	
	/**@author HCJ
	 * 返回产线所有进行中工单版面类型的值
	 * @date 2019年1月29日 下午5:46:41
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectWorkingBoardType")
	public List<Integer> selectWorkingBoardType(String line, String workOrder) {
		return programService.selectWorkingBoardType(line, workOrder);
	}

	
	/**@author HCJ
	 * 返回站位表每个站位的操作结果
	 * @date 2019年1月29日 下午5:46:51
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectItemVisitByProgram")
	public List<ProgramItemVisit> selectItemVisitByProgram(String line, String workOrder, Integer boardType) {
		return programService.selectItemVisitByProgram(line, workOrder, boardType);
	}

	
	/**@author HCJ
	 * 返回最新操作时的操作人员
	 * @date 2019年1月29日 下午5:47:26
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectLastOperatorByProgram")
	public String selectLastOperatorByProgram(String line, String workOrder, Integer boardType) {
		return programService.selectLastOperatorByProgram(line, workOrder, boardType);
	}
	
	
	/**@author HCJ
	 * 返回工单id
	 * @date 2019年1月29日 下午5:48:03
	 */
	@Open
	@ResponseBody
	@RequestMapping("/getProgramId")
	public ResultUtil getProgramId(String line, String workOrder, Integer boardType) {
		String result = programService.getProgramId(line, workOrder, boardType);
		if(result == null) {
			return ResultUtil.failed("fail");
		}
		return ResultUtil.succeed(result);
	}
	
	
	/**@author HCJ
	 * 返回当前时间戳
	 * @date 2019年1月29日 下午5:48:12
	 */
	@Open
	@ResponseBody
	@RequestMapping("/getTimesTamp")
	public long getTimesTamp() {
		return programService.getCurrentTime().getTime();
	}
	
	
	/**@author HCJ
	 * 判断当前工单全检操作是否超时
	 * @date 2019年1月29日 下午5:48:42
	 */
	@Open
	@ResponseBody
	@RequestMapping("/isCheckAllTimeOut")
	public String isCheckAllTimeOut(String line, String workOrder, Integer boardType) {
		return programService.isCheckAllTimeOut(line, workOrder, boardType);
	}
	
	
	/**@author HCJ
	 * 下载标准站位表
	 * @date 2019年1月29日 上午11:46:49
	 */
	@Role(RoleType.ENGINEER)
	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(HttpServletRequest request) {
		String fileName = "标准站位表.xls";
		String path = request.getSession().getServletContext().getRealPath("WEB-INF") + File.separator + fileName;
		byte[] body = null;
		InputStream in = null;
		try {
			in = new FileInputStream(new File(path));
			body = new byte[in.available()];
			in.read(body);
			fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment;filename=" + fileName);
			HttpStatus statusCode = HttpStatus.OK;
			ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(body, headers, statusCode);
			return response;
		} catch (IOException e) {
			ResultUtil.failed("下载标准站位表出现IO错误 | " + e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
		/*String fileName = "标准站位表.xls";
		String path = request.getSession().getServletContext().getRealPath("WEB-INF") + File.separator + fileName;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(new File(path)));
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("multipart/form-data");
			out = new BufferedOutputStream(response.getOutputStream());
			int len = 0;
			while ((len = in.read()) != -1) {
				out.write(len);
				out.flush();
			}
		} catch (IOException e) {
			ResultUtil.failed("下载标准站位表出现IO错误 | " + e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
}
