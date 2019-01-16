package com.jimi.smt.eps_server.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.ProgramItemExample;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.ProgramItemVisitExample;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.filler.ProgramItemToProgramItemVOFiller;
import com.jimi.smt.eps_server.entity.filler.ProgramItemToProgramItemVisitFiller;
import com.jimi.smt.eps_server.entity.filler.ProgramToProgramVOFiller;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;
import com.jimi.smt.eps_server.mapper.DisplayMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.mapper.ProgramMapper;
import com.jimi.smt.eps_server.service.LineService;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.timer.TimeoutTimer;
import com.jimi.smt.eps_server.util.ExcelSpringHelper;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.SqlUtil;

import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.dautils.api.UuidUtil;

@Service
public class ProgramServiceImpl implements ProgramService {

	@Autowired
	private ProgramMapper programMapper;
	@Autowired
	private ProgramItemMapper programItemMapper;
	@Autowired
	private ProgramItemVisitMapper programItemVisitMapper;
	@Autowired
	private DisplayMapper displayMapper;
	@Autowired
	private ProgramToProgramVOFiller programToProgramVOFiller;
	@Autowired
	private ProgramItemToProgramItemVOFiller programItemToProgramItemVOFiller;
	@Autowired
	private ProgramItemToProgramItemVisitFiller programItemToProgramItemVisitFiller;
	@Autowired
	private TimeoutTimer timeoutTimer;
	@Autowired
	private LineService lineService;

	
	@Override
	public List<Map<String, Object>> upload(MultipartFile programFile, Integer boardType) throws IOException {
		// 读文件
		ExcelSpringHelper helper = ExcelSpringHelper.from(programFile);

		// 初始化结果
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		// 因添加流水号而导致的表格列号偏移量
		int offset = 1;

		// 因添加流水号而进行的站位表校验
		if (helper.getString(0, 0) != null && !"".equals(helper.getString(0, 0))) {
			throw new RuntimeException("站位表版本错误：请使用带有序列号的站位表");
		}

		// 校验头部
		final String header = "SMT FEEDER LIST";
		if (!header.equals(helper.getString(1, 0 + offset))) {
			throw new RuntimeException("头部错误：没有找到\"SMT FEEDER LIST\"标题栏");
		}
		
		// 校验文件内容是否有效且存在
		if(isProgramFileContentAvailable(helper) == false) {
			throw new RuntimeException("站位表部分内容为空或错误：请正确填写");
		}
		
		// 分割解析工单和线号
		String[] workOrders = helper.getString(4, 6 + offset).split("\\+");
		String[] lines = helper.getString(3, 6 + offset).split("\\+");
		
		// 判断线号是否正确
		for (String line : lines) {
			if (lineService.getLineIdByName(line) == null) {
				throw new RuntimeException("线号错误:请填写正确的线号");
			}
		}	
		
		// 创建所有工单
		List<Program> programs = new ArrayList<Program>(workOrders.length * lines.length);
		for (String line : lines) {
			for (String workOrder : workOrders) {
				Program program = new Program();
				program.setClient(helper.getString(2, 1 + offset));
				program.setMachineName(helper.getString(2, 4 + offset));
				program.setVersion(helper.getString(2, 6 + offset));
				program.setMachineConfig(helper.getString(3, 1 + offset));
				program.setProgramNo(helper.getString(3, 4 + offset));
				program.setLine(lineService.getLineIdByName(line));
				program.setEffectiveDate(helper.getDate(4, 1 + offset).toString());
				program.setPcbNo(helper.getString(4, 4 + offset));
				program.setBom(helper.getString(5, 1 + offset));
				program.setProgramName(helper.getString(6, 1 + offset));
				program.setPlanProduct(getPlanProduct(helper.getString(6, 5 + offset)));
				program.setStructure(Integer.parseInt(helper.getString(6, 8 + offset)));
				program.setAuditor(helper.getString(7, 4 + offset).substring(3));
				program.setFileName(programFile.getOriginalFilename());
				program.setId(UuidUtil.get32UUID());
				program.setCreateTime(getCurrentTime());
				program.setBoardType(boardType);
				program.setWorkOrder(workOrder);
				programs.add(program);
			}
		}

		for (Program program : programs) {
			// 初始化结果Item
			Map<String, Object> result = new HashMap<String, Object>();
			int sum = helper.getBook().getNumberOfSheets();
			result.put("real_parse_num", sum);
			result.put("plan_parse_num", sum);
			result.put("action_name", "上传");

			// 覆盖：如果“未开始”的工单列表中存在板面类型、工单号、线号同时一致的工单项目，将被新文件内容覆盖
			ProgramExample programExample = new ProgramExample();
			programExample.createCriteria().andWorkOrderEqualTo(program.getWorkOrder()).andBoardTypeEqualTo(program.getBoardType()).andLineEqualTo(program.getLine()).andStateEqualTo(0);
			// 如果存在符合条件的工单
			List<Program> programs2 = programMapper.selectByExample(programExample);
			if (!programs2.isEmpty()) {
				programMapper.updateByExampleSelective(program, programExample);
				ProgramItemExample programItemExample = new ProgramItemExample();
				programItemExample.createCriteria().andProgramIdEqualTo(programs2.get(0).getId());
				programItemMapper.deleteByExample(programItemExample);
				result.put("action_name", "覆盖");
			} else {
				programMapper.insertSelective(program); 
			}

			// 打印到控制台
			FieldUtil.print(program);

			// 填充表项
			for (int i = 0; i < sum; i++) {
				helper.switchSheet(i);
				for (int j = 9; j < helper.getBook().getSheetAt(i).getLastRowNum() - 3; j++) {
					ProgramItem programItem = new ProgramItem();
					// 空表判断
					if (helper.getString(j, 0 + offset).equals("")) {
						int temp = (int) result.get("real_parse_num");
						result.put("real_parse_num", temp--);
						break;
					}
					// 排除手盖
					String lineseat = helper.getString(j, 0 + offset);
					if ("手盖".equals(lineseat)) {
						continue;
					}
					programItem.setLineseat(formatLineseat(lineseat));
					programItem.setMaterialNo(helper.getString(j, 1 + offset));
					programItem.setAlternative(helper.getBoolean(j, 2 + offset));
					programItem.setSpecitification(helper.getString(j, 3 + offset));
					programItem.setPosition(helper.getString(j, 4 + offset));
					programItem.setQuantity(helper.getInt(j, 5 + offset));
					programItem.setSerialNo(helper.getInt(j, -1 + offset));
					// 设置programId
					programItem.setProgramId(program.getId());
					// 忽略重复项
					try {
						// 插入表项
						programItemMapper.insert(programItem);
						// 打印到控制台
						FieldUtil.print(programItem);
					} catch (DuplicateKeyException e) {
					}
				}
			}
			resultList.add(result);
		}

		return resultList;
	}

	
	@Override
	public List<ProgramVO> list(String programName, String fileName, Integer line, String workOrder, Integer state, String ordBy, Page page) {
		ProgramExample programExample = new ProgramExample();
		ProgramExample.Criteria programCriteria = programExample.createCriteria();

		// 排序
		if (ordBy == null || ordBy.equals("")) {
			// 默认按时间降序
			programExample.setOrderByClause("create_time desc");
		} else {
			programExample.setOrderByClause(ordBy);
		}

		// 筛选程序名
		if (programName != null && !programName.equals("")) {
			programCriteria.andProgramNameLike("%" + SqlUtil.escapeParameter(programName) + "%");
		}
		// 筛选文件名
		if (fileName != null && !fileName.equals("")) {
			programCriteria.andFileNameLike("%" + SqlUtil.escapeParameter(fileName) + "%");
		}
		// 筛选线别
		if (line != null) {
			programCriteria.andLineEqualTo(line);
		}
		// 筛选工单号
		if (workOrder != null && !workOrder.equals("")) {
			programCriteria.andWorkOrderLike("%" + SqlUtil.escapeParameter(workOrder) + "%");
		}
		// 筛选状态
		if (state != null) {
			programCriteria.andStateEqualTo(state);
		}
		
		if(page != null) {
			page.setTotallyData(programMapper.countByExample(programExample));
			programExample.setLimitStart(page.getFirstIndex());			
			programExample.setLimitSize(page.getPageSize());
			
		}
		List<Program> programs = programMapper.selectByExample(programExample);
		List<ProgramVO> programVOs = new ArrayList<>();
		for (Program program : programs) {
			programVOs.add(programToProgramVOFiller.fill(program));
		}
		return programVOs;
	}

	
	@Override
	public List<ProgramItemVO> listItem(String id) {
		ProgramItemExample example = new ProgramItemExample();
		example.createCriteria().andProgramIdEqualTo(id);		
		return programItemToProgramItemVOFiller.fill(programItemMapper.selectByExample(example));
	}

	
	@Override
	public String updateItem(List<EditProgramItemBO> BOs) {
		// 获取ProgramId
		String oldProgramId = BOs.get(0).getProgramId();
		String newProgramId = UuidUtil.get32UUID();

		// 判断旧Program是否存在
		ProgramExample programExample = new ProgramExample();
		programExample.createCriteria().andIdEqualTo(oldProgramId);
		List<Program> programs = programMapper.selectByExample(programExample);
		if (programs == null || programs.isEmpty()) {
			ResultUtil.failed("找不到站位表，ID不存在");
			throw new RuntimeException("failed_program_not_found");
		}

		// 创建新Program
		Program newProgram = new Program();

		// 从旧Program复制属性
		Program oldProgram = programs.get(0);
		FieldUtil.copy(oldProgram, newProgram);

		// 根据旧Program状态设置新Program状态
		if (oldProgram.getState() > 1) {
			ResultUtil.failed("只能编辑未开始和进行中的工单");
			throw new RuntimeException("failed_illegal_state");
		} else if (oldProgram.getState() == 1) {
			newProgram.setState(1);
		}

		// 作废旧Program
		oldProgram.setState(3);

		// 对新Program进行赋值
		newProgram.setId(newProgramId);
		newProgram.setCreateTime(getCurrentTime());

		// 提交新旧Program
		programMapper.updateByPrimaryKey(oldProgram);
		programMapper.insert(newProgram);

		// 获取旧的Program_Item，根据修改记录，生成新的Item并插入
		ProgramItemExample programItemExample = new ProgramItemExample();
		programItemExample.createCriteria().andProgramIdEqualTo(oldProgramId);
		List<ProgramItem> items = programItemMapper.selectByExample(programItemExample);
		for (EditProgramItemBO bo : BOs) {
			ProgramItem newItem = new ProgramItem();
			FieldUtil.copy(bo, newItem);
			if (items != null && items.size() > 0) {
				for (int i = 0; i < items.size(); i++) {
					ProgramItem programItem = items.get(i);
					// 匹配记录
					if (bo.getTargetLineseat().equals(programItem.getLineseat())
							&& bo.getTargetMaterialNo().equals(programItem.getMaterialNo())) {
						int index = items.indexOf(programItem);
						// 执行编辑操作
						switch (bo.getOperation()) {
						case 0:
							items.add(index, newItem);
							break;
						case 1:
							items.set(index, newItem);
							break;
						case 2:
							items.remove(index);
							break;
						default:
							break;
						}
						break;
					} else if (bo.getTargetLineseat().equals("") && bo.getTargetMaterialNo().equals("") && bo.getOperation() == 0) {
						// 如果目标站位和料号为空并且操作类型为增加，则追加在列表尾部
						items.add(newItem);
						break;
					}
				}
			} else {
				if (bo.getOperation() == 0) {
					items.add(newItem);
				}
			}
		}
		// 插入数据库
		try {
			for (ProgramItem programItem : items) {
				// 设置新id
				programItem.setProgramId(newProgramId);
				// 插入
				programItemMapper.insert(programItem);
			}
		} catch (DuplicateKeyException e) {
			// 主键重复
			ResultUtil.failed("Item主键重复");
			throw new RuntimeException("failed_item_key_duplicate");
		}

		// 如果状态为进行中，则用旧visit表数据覆盖新visit数据（匹配的记录就覆盖，不匹配的就跳过）
		if (newProgram.getState() == 1) {
			List<ProgramItemVisit> newVisits = programItemToProgramItemVisitFiller.fill(items);
			ProgramItemVisitExample programItemVisitExample = new ProgramItemVisitExample();
			programItemVisitExample.createCriteria().andProgramIdEqualTo(oldProgramId);
			List<ProgramItemVisit> oldVisits = programItemVisitMapper.selectByExample(programItemVisitExample);
			if (oldVisits == null || oldVisits.isEmpty()) {
				for (ProgramItemVisit newVisit : newVisits) {
					// 设置新id
					newVisit.setProgramId(newProgramId);
					// 插入新visit数据
					programItemVisitMapper.insert(newVisit);
				}
			}else {
				for (ProgramItemVisit newVisit : newVisits) {
					for (ProgramItemVisit oldVisit : oldVisits) {
						if (newVisit.getLineseat().equals(oldVisit.getLineseat())
								&& newVisit.getMaterialNo().equals(oldVisit.getMaterialNo())) {
							// 覆盖数据
							FieldUtil.copy(oldVisit, newVisit);
							// 设置新id
							newVisit.setProgramId(newProgramId);
						}
						// 删除旧visit数据
						programItemVisitMapper.deleteByPrimaryKey(oldVisit);
					}
					// 插入新visit数据
					programItemVisitMapper.insert(newVisit);
				}
			}
		}
		return "succeed";
	}

	
	@Override
	public boolean cancel(String id) {
		ProgramExample example = new ProgramExample();
		example.createCriteria().andIdEqualTo(id);
		// 状态判断
		List<Program> programs = programMapper.selectByExample(example);
		if (programs.isEmpty()) {
			return false;
		}
		Program program = programs.get(0);
		if (program.getState() >= 2) {
			ResultUtil.failed("状态不可逆");
			return false;
		}
		Program program2 = new Program();
		program2.setState(3);
		int result = programMapper.updateByExampleSelective(program2, example);
		if (result != 0) {
			// 清除ProgramItemVisit
			clearVisits(program.getId());
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public boolean finish(String id) {
		ProgramExample example = new ProgramExample();
		example.createCriteria().andIdEqualTo(id);
		// 状态判断
		List<Program> programs = programMapper.selectByExample(example);
		if (programs.isEmpty()) {
			return false;
		}
		Program program = programs.get(0);
		if (program.getState() >= 2) {
			ResultUtil.failed("状态不可逆");
			return false;
		}
		Program program2 = new Program();
		program2.setState(2);
		int result = programMapper.updateByExampleSelective(program2, example);
		if (result != 0) {
			// 清除ProgramItemVisit
			clearVisits(program.getId());
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public String start(String id) {
		ProgramExample example = new ProgramExample();
		example.createCriteria().andIdEqualTo(id);
		// 状态判断
		List<Program> programs = programMapper.selectByExample(example);
		if (programs.isEmpty()) {
			ResultUtil.failed("id不存在");
			return "failed_id_not_exist";
		}
		Program program = programs.get(0);
		if (program.getState() >= 1) {
			ResultUtil.failed("状态不可逆");
			return "failed_state_error";
		}
		// 判断是否存在已开始的相同工单（相同的定义：工单号、板面类型、线号均一致）
		ProgramExample example2 = new ProgramExample();
		example2.createCriteria().andLineEqualTo(program.getLine()).andWorkOrderEqualTo(program.getWorkOrder()).andBoardTypeEqualTo(program.getBoardType()).andStateEqualTo(1);
		List<Program> programs2 = programMapper.selectByExample(example2);
		if (!programs2.isEmpty()) {
			ResultUtil.failed("已存在相同的正在进行的工单");
			return "failed_already_started";
		}
		Program program2 = new Program();
		program2.setState(1);
		int result = programMapper.updateByExampleSelective(program2, example);
		// 初始化Program_Item_Visit
		ProgramItemExample programItemExample = new ProgramItemExample();
		programItemExample.createCriteria().andProgramIdEqualTo(program.getId());
		List<ProgramItem> programItems = programItemMapper.selectByExample(programItemExample);
		List<ProgramItemVisit> programItemVisits = programItemToProgramItemVisitFiller.fill(programItems);
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			programItemVisitMapper.insertSelective(programItemVisit);
		}
		if (result != 0) {
			return "succeed";
		} else {
			return "failed_unknown";
		}
	}

	
	@Override
	public String switchWorkOrder(String line, String workOrder, Integer boardType) {
		if (line == null || line.equals("")) {
			return "succeed";
		}
		Integer lineId = lineService.getLineIdByName(line);
		if(lineId == null) {
			return "failed_not_exist";
		}
		// 获取Display
		DisplayExample displayExample = new DisplayExample();		
		displayExample.createCriteria().andLineEqualTo(lineId);
		Display display = displayMapper.selectByExample(displayExample).get(0);
		int flag = 0;
		// 判断是否是停止监控
		if (workOrder == null && boardType == null) {
			display.setWorkOrder(null);
			display.setBoardType(null);
		} else {
			List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
			if (programItemVisits.isEmpty()) {
				return "failed_not_exist";
			}
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getFirstCheckAllResult() != 1) {
					flag = 1;
					break;
				}
			}
			if (flag == 1) {
				for (ProgramItemVisit programItemVisit : programItemVisits) {
					programItemVisit.setCheckAllTime(getCurrentTime());
					programItemVisitMapper.updateByPrimaryKey(programItemVisit);
				}
			}
			display.setWorkOrder(workOrder);
			display.setBoardType(boardType);
		}
		displayMapper.updateByPrimaryKey(display);
		return "succeed";
	}

	
	@Override
	public String operate(String line, String workOrder, Integer boardType, Integer type, String lineseat,
			String scanLineseat, String scanMaterialNo, Integer operationResult) {
		if(lineService.getLineIdByName(line) == null) {
			return "failed_not_exist";
		}
		List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
		if (programItemVisits.isEmpty()) {
			return "failed_not_exist";
		}
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			if (programItemVisit.getLineseat().equals(lineseat)) {
				programItemVisit.setScanLineseat(scanLineseat);
				programItemVisit.setScanMaterialNo(scanMaterialNo);
				switch (type) {
				// 核料
				case 2:
					programItemVisit.setLastOperationType(2);
					programItemVisit.setLastOperationTime(getCurrentTime());
					programItemVisit.setCheckTime(getCurrentTime());
					programItemVisit.setCheckResult(operationResult);
					// 如果核料成功，把换料结果也置为成功
					if (operationResult == 1) {
						programItemVisit.setChangeResult(1);
					}
					break;
				// 全检
				case 3:
					programItemVisit.setLastOperationType(3);
					programItemVisit.setLastOperationTime(getCurrentTime());
					programItemVisit.setCheckAllTime(getCurrentTime());
					programItemVisit.setCheckAllResult(operationResult);
					break;
				default:
					break;
				}
				synchronized (timeoutTimer.getLock(line)) {
					updateVisit(programItemVisit);
				}
			}
		}
		return "succeed";
	}

	
	@Override
	public String reset(String line, String workOrder, Integer boardType) {
		if(lineService.getLineIdByName(line) == null) {
			return "failed_not_exist";
		}
		List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
		if (programItemVisits.isEmpty()) {
			return "failed_not_exist";
		}
		synchronized (timeoutTimer.getLock(line)) {
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				programItemVisit.setLastOperationTime(getCurrentTime());
				programItemVisit.setLastOperationType(null);
				programItemVisit.setFeedResult(2);
				programItemVisit.setChangeResult(2);
				programItemVisit.setCheckResult(2);
				programItemVisit.setCheckAllResult(2);
				programItemVisit.setFirstCheckAllResult(2);
				updateVisit(programItemVisit);
			}
		}
		return "succeed";
	}

	
	@Override
	public List<ProgramItemVisit> getVisits(String line, String workOrder, Integer boardType) {
		ProgramExample programExample = new ProgramExample();
		Integer lineId = lineService.getLineIdByName(line);
		if(lineId == null) {
			return null;
		}
		programExample.createCriteria().andLineEqualTo(lineId).andWorkOrderEqualTo(workOrder).andBoardTypeEqualTo(boardType).andStateEqualTo(1);
		List<Program> programs = programMapper.selectByExample(programExample);
		if (!programs.isEmpty()) {
			String programId = programs.get(0).getId();
			// 查询Visits
			ProgramItemVisitExample programItemVisitExample = new ProgramItemVisitExample();
			programItemVisitExample.createCriteria().andProgramIdEqualTo(programId);
			return programItemVisitMapper.selectByExample(programItemVisitExample);
		}
		return new ArrayList<ProgramItemVisit>();
	}

	
	@Override
	public void updateVisit(ProgramItemVisit visit) {
		programItemVisitMapper.updateByPrimaryKey(visit);
	}

	
	@Override
	public List<Display> listDisplays() {		
		return displayMapper.selectByExample(null);
	}

	
	@Override
	public List<ProgramVO> selectWorkingProgram(String line) {
		ProgramExample programExample = new ProgramExample();
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			programExample.createCriteria().andLineEqualTo(lineId).andStateEqualTo(1);
			List<Program> programs = programMapper.selectByExample(programExample);
			List<ProgramVO> programVOs = new ArrayList<>();
			for (Program program : programs) {
				programVOs.add(programToProgramVOFiller.fill(program));
			}
			return programVOs;
		}
		return null;
	}

	
	@Override
	public Integer updateItemVisit(ProgramItemVisit programItemVisit) {
		int result = 0;
		int type = programItemVisit.getLastOperationType();
		switch (type) {
		case 0:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setFeedTime(getCurrentTime());
			result = programItemVisitMapper.updateFeedResult(programItemVisit);
			break;
		case 1:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setChangeTime(getCurrentTime());
			programItemVisit.setCheckResult(2);
			result = programItemVisitMapper.updateChangeResult(programItemVisit);
			break;
		case 2:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setCheckTime(getCurrentTime());
			if (programItemVisit.getCheckResult() != null && programItemVisit.getCheckResult() == 0) {
				result = programItemVisitMapper.updateCheckFailResult(programItemVisit);
			} else if (programItemVisit.getCheckResult() != null && programItemVisit.getCheckResult() == 1) {
				result = programItemVisitMapper.updateCheckSucceedResult(programItemVisit);
			}
			break;
		case 3:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setCheckAllTime(getCurrentTime());
			result = programItemVisitMapper.updateAllResult(programItemVisit);
			break;
		case 4:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setStoreIssueTime(getCurrentTime());
			result = programItemVisitMapper.updateStoreResult(programItemVisit);
			break;
		case 5:
			programItemVisit.setLastOperationTime(getCurrentTime());
			programItemVisit.setFirstCheckAllTime(getCurrentTime());
			result = programItemVisitMapper.updateFirstAllResult(programItemVisit);
			break;
		default:
			break;
		}
		return result;
	}

	
	@Override
	public Integer resetCheckAll(String programId) {
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId);
		ProgramItemVisit programItemVisit = new ProgramItemVisit();
		programItemVisit.setCheckAllResult(1);
		programItemVisit.setCheckAllTime(getCurrentTime());
		return programItemVisitMapper.updateByExampleSelective(programItemVisit, example);
	}

	
	@Override
	public Integer checkIsReset(String programId, int type) {
		int lastResult = 0;
		List<Integer> results = new ArrayList<>();
		List<Integer> times = new ArrayList<>();
		int allResult = 1;
		int allTime = 0;
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId);
		List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(example);
		switch (type) {
		case 0:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				results.add(programItemVisit.getFeedResult());
				if (programItemVisit.getLastOperationTime() == null) {
					times.add(0);
				} else {
					times.add(1);
				}
			}
			break;
		case 3:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				results.add(programItemVisit.getCheckAllResult());
				if (programItemVisit.getLastOperationTime() == null) {
					times.add(0);
				} else {
					times.add(1);
				}
			}
			break;
		case 5:			
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				results.add(programItemVisit.getFirstCheckAllResult());
				if (programItemVisit.getLastOperationTime() == null) {
					times.add(0);
				} else {
					times.add(1);
				}
			}
			break;
		default:
			System.out.println("超出程序范围");
			return 0;
		}
		// 判断是否重置工单:上次操作时间是否存在和结果是否为2
		for (Integer result : results) {
			if (!result.equals(2)) {
				allResult = 0;
				break;
			}
		}
		for (Integer time : times) {
			if (time.equals(1)) {
				allTime = 1;
				break;
			}
		}
		if (allResult == 1 && allTime == 1) {
			lastResult = 1;
		}
		return lastResult;
	}

	
	@Override
	public Integer isAllDone(String programId, int type) {
		int result = 1;
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId);
		List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(example);
		switch (type) {
		case 4:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getStoreIssueResult() != 1) {
					result = 0;
					break;
				}
			}
			break;
		case 0:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getFeedResult() != 1) {
					result = 0;
					break;
				}
			}
			break;
		case 3:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getCheckAllResult() != 1) {
					result = 0;
					break;
				}
			}
			break;
		case 5:
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getFirstCheckAllResult() != 1) {
					result = 0;
					break;
				}
			}
			break;
		default:
			System.out.println("超出程序范围");
			result = 0;
		}
		return result;
	}

	
	@Override
	public Integer isChangeSucceed(String programId, String lineseat) {
		int result = 1;
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId).andLineseatEqualTo(lineseat);
		List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(example);
		if (programItemVisits.size() > 0) {
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getChangeResult() == 0) {
					result = 0;
					break;
				}
			}
		}
		return result;
	}

	
	@Override
	public List<String> selectWorkingOrder(String line) {
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			ProgramExample programExample = new ProgramExample();
			programExample.setDistinct(true);
			programExample.createCriteria().andLineEqualTo(lineId).andStateEqualTo(1);
			List<Program> programs = programMapper.selectByExample(programExample);
			List<String> workingOrderList = new ArrayList<>();
			for (Program program : programs) {
				workingOrderList.add(program.getWorkOrder());
			}
			return workingOrderList;
		}
		return null;
	}

	
	@Override
	public List<Integer> selectWorkingBoardType(String line, String workOrder) {
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			ProgramExample programExample = new ProgramExample();
			programExample.createCriteria().andLineEqualTo(lineId).andWorkOrderEqualTo(workOrder).andStateEqualTo(1);
			programExample.setOrderByClause("board_type asc");
			programExample.setDistinct(true);
			List<Program> programs = programMapper.selectByExample(programExample);
			List<Integer> workingBoardTypeList = new ArrayList<>();
			for (Program program : programs) {
				workingBoardTypeList.add(program.getBoardType());
			}
			return workingBoardTypeList;
		}
		return null;
	}

	
	@Override
	public List<ProgramItemVisit> selectItemVisitByProgram(String line, String workOrder, int boardType) {
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			Program program = new Program();
			program.setLine(lineId);
			program.setWorkOrder(workOrder);
			program.setBoardType(boardType);
			return programItemVisitMapper.selectItemVisitByProgram(program);
		}
		return null;
	}

	
	@Override
	public String selectLastOperatorByProgram(String line, String workOrder, Integer boardType) {
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			Program program = new Program();
			program.setLine(lineId);
			program.setWorkOrder(workOrder);
			program.setBoardType(boardType);
			return programMapper.selectLastOperatorByProgram(program);
		}
		return null;
	}

	
	@Override
	public String getProgramId(String line, String workOrder, Integer boardType) {
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId != null) {
			ProgramExample programExample = new ProgramExample();
			programExample.createCriteria().andLineEqualTo(lineId).andWorkOrderEqualTo(workOrder).andBoardTypeEqualTo(boardType).andStateEqualTo(1);
			List<Program> programs = programMapper.selectByExample(programExample);
			if (programs.isEmpty()) {
				return null;
			}
			return programs.get(0).getId();
		}
		return null;
	}

	
	@Override
	public String isCheckAllTimeOut(String line, String workOrder, Integer boardType) {
		// TODO Auto-generated method stub
		Integer isCheckAllTimeOutExist = 0;
		List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			if (programItemVisit.getCheckAllResult() == 3) {
				isCheckAllTimeOutExist = 1;
				break;
			}
		}
		return "{\"programId\":\"" + programItemVisits.get(0).getProgramId() + "\",\"isCheckAllTimeOutExist\":" + isCheckAllTimeOutExist + "}";
	}


	@Override
	public Date getCurrentTime() {
		return new Date();
	}


	private void clearVisits(String programId) {
		ProgramItemVisitExample programItemVisitExample = new ProgramItemVisitExample();
		programItemVisitExample.createCriteria().andProgramIdEqualTo(programId);
		programItemVisitMapper.deleteByExample(programItemVisitExample);
	}

	
	private String formatLineseat(String in) {
		try {
			String[] array = in.split("-");
			array[0] = Integer.valueOf(array[0]) <= 9 ? "0" + array[0] : array[0];
			array[1] = Integer.valueOf(array[1]) <= 9 ? "0" + array[1] : array[1];
			return array[0] + "-" + array[1];
		} catch (NumberFormatException | PatternSyntaxException e) {
			return in;
		}
	}
				
	
	/**@author HCJ
	 * 获取从Excel表格读取的计划生产数量
	 * @method getPlanProduct
	 * @param planProduct 从Excel表格读取的字符串
	 * @return int
	 * @date 2018年9月19日 下午8:58:52
	 */
	private int getPlanProduct(String planProduct) {
		if (planProduct.contains("k") || planProduct.contains("K")) {
			String result = planProduct.toLowerCase().substring(0, planProduct.indexOf("k"));
			return Integer.parseInt(result) * 1000;
		} else {
			return Integer.parseInt(planProduct);
		}
	}
	
	
	/**@author HCJ
	 * 根据ExcelSpringHelper获取的文件内容，判断其是否符合要求
	 * @date 2018年11月27日 下午4:28:55
	 */
	private boolean isProgramFileContentAvailable(ExcelSpringHelper helper) {
		// 因添加流水号而导致的表格列号偏移量
		int offset = 1;
		boolean isProgramFileHeadExist = false;
		boolean isProgramFileBodyExist = false;
		boolean isAuditorAvailable = false;
		boolean isSerialNoAvailable = false;
		// 判断各个格子的内容是否存在
		boolean isClientExist = isContentExist(helper.getString(2, 1 + offset));
		boolean isMachineNameExist = isContentExist(helper.getString(2, 4 + offset));
		boolean isVersionExist = isContentExist(helper.getString(2, 6 + offset));
		boolean isMachineConfigExist = isContentExist(helper.getString(3, 1 + offset));
		boolean isProgramNoExist = isContentExist(helper.getString(3, 4 + offset));
		boolean isLineExist = isContentExist(helper.getString(3, 6 + offset));
		boolean isEffectiveDateExist = isContentExist(helper.getString(4, 1 + offset));
		boolean isPcbNoExist = isContentExist(helper.getString(4, 4 + offset));
		boolean isWorkOrderExist = isContentExist(helper.getString(4, 6 + offset));
		boolean isBomExist = isContentExist(helper.getString(5, 1 + offset));
		boolean isProgramNameExist = isContentExist(helper.getString(6, 1 + offset));
		boolean isPlanProductExist = isContentExist(helper.getString(6, 5 + offset));
		boolean isStructureExist = isContentExist(helper.getString(6, 8 + offset));
		if (isContentExist(helper.getString(7, 4 + offset))) {
			if (helper.getString(7, 4 + offset).length() > 3) {
				isAuditorAvailable = true;
			}
		}
		if (isClientExist && isMachineNameExist && isVersionExist && isMachineConfigExist && isProgramNoExist && isLineExist && isEffectiveDateExist && isPcbNoExist && isWorkOrderExist && isBomExist && isProgramNameExist && isPlanProductExist && isStructureExist && isAuditorAvailable) {
			isProgramFileHeadExist = true;
		}
		int sum = helper.getBook().getNumberOfSheets();
		int realBodyRowNum = 0;
		int planBodyRowNum = 0;
		for (int i = 0; i < sum; i++) {
			helper.switchSheet(i);
			// 站位表body中含有序列号的行的数量
			for (int j = 9; j < helper.getBook().getSheetAt(i).getLastRowNum() - 3; j++) {
				if (isContentExist(helper.getString(j, -1 + offset)) && isNumber(helper.getString(j, -1 + offset))) {
					planBodyRowNum++;
				}
				isSerialNoAvailable = isContentExist(helper.getString(j, -1 + offset)) && isNumber(helper.getString(j, -1 + offset));
				boolean isLineseatExist = isContentExist(helper.getString(j, 0 + offset));
				boolean isMaterialNoExist = isContentExist(helper.getString(j, 1 + offset));
				boolean isSpecitificationExist = isContentExist(helper.getString(j, 3 + offset));
				boolean isPositionExist = isContentExist(helper.getString(j, 4 + offset));
				// 填写有效的站位表body中含有序列号的行的数量
				if (isLineseatExist && isMaterialNoExist && isSpecitificationExist && isPositionExist && isSerialNoAvailable) {
					realBodyRowNum++;
				}
			}
		}
		if (planBodyRowNum == realBodyRowNum) {
			isProgramFileBodyExist = true;
		}
		if (isProgramFileHeadExist && isProgramFileBodyExist) {
			return true;
		}
		return false;
	}
	
	
	/**@author HCJ
	 * 判断字符串是否为空或者空字符串
	 * @date 2018年11月28日 上午8:38:43
	 */
	private boolean isContentExist(String content) {
		if (content == null || "".equals(content) || content.length() == 0 || content.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	
	/**@author HCJ
	 * 判断字符串是否为数字
	 * @date 2018年11月28日 上午8:40:02
	 */
	private boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
