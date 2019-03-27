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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.LineExample;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.ProgramItemExample;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.ProgramItemVisitExample;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.bo.OperationResult;
import com.jimi.smt.eps_server.entity.filler.ProgramItemToProgramItemVOFiller;
import com.jimi.smt.eps_server.entity.filler.ProgramItemToProgramItemVisitFiller;
import com.jimi.smt.eps_server.entity.filler.ProgramToProgramVOFiller;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;
import com.jimi.smt.eps_server.mapper.DisplayMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.mapper.ProgramMapper;
import com.jimi.smt.eps_server.service.LineService;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.timer.TimeoutTimer;
import com.jimi.smt.eps_server.util.ExcelPopularGetter;
import com.jimi.smt.eps_server.util.OsHelper;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.ResultUtil2;
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
	private LineMapper lineMapper;
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

	/**
	 * FIELD_LENGTH : 字段最大长度为32
	 */
	private static final Integer FIELD_LENGTH = 32;
	/**
	 * PCBNO_LENGTH : PCBNO最大长度为64
	 */
	private static final Integer PCBNO_LENGTH = 64;
	/**
	 * MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH : 料号、文件名、程序名和工单最大长度为128
	 */
	private static final Integer MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH = 128;
	/**
	 * BOM_LENGTH : Bom最大长度为256
	 */
	private static final Integer BOM_LENGTH = 256;
	/**
	 * POSITION_SPECITIFICATION_LENGTH : 单板位置和BOM料号/规格最大长度为1024
	 */
	private static final Integer POSITION_SPECITIFICATION_LENGTH = 1024;


	@Override
	public Map<String, Object> upload(MultipartFile programFile, Integer boardType) throws IOException, InvalidFormatException {
		// 读文件
		ExcelPopularGetter getter = ExcelPopularGetter.from(programFile);

		// 初始化结果
		Map<String, Object> result = new HashMap<String, Object>();

		// 因添加流水号而导致的表格列号偏移量
		int offset = 1;

		// 因添加流水号而进行的站位表校验
		if (getter.getString(0, 0) != null && !"".equals(getter.getString(0, 0))) {
			throw new RuntimeException("站位表版本错误：请使用带有序列号的站位表");
		}

		// 校验头部
		final String header = "SMT FEEDER LIST";
		if (!header.equals(getter.getString(1, 0 + offset))) {
			throw new RuntimeException("站位表版本错误：请使用带有 SMT FEEDER LIST 标题栏的站位表");
		}

		// 分割解析工单和线号
		String workOrder = getter.getString(4, 6 + offset);
		String lineName = getter.getString(3, 6 + offset);

		// 判断线号是否正确
		Integer lineId = lineService.getLineIdByName(lineName);
		if (lineId == null) {
			throw new RuntimeException("线号错误：请填写正确的线号");
		}

		// 获取首个表格表头的数据
		Program program = new Program();
		program.setClient(getter.getString(2, 1 + offset));
		program.setMachineName(getter.getString(2, 4 + offset));
		program.setVersion(getter.getString(2, 6 + offset));
		program.setMachineConfig(getter.getString(3, 1 + offset));
		program.setProgramNo(getter.getString(3, 4 + offset));
		program.setLine(lineId);
		program.setEffectiveDate(getEffectiveDate(getter.getDate(4, 1 + offset)));
		program.setPcbNo(getter.getString(4, 4 + offset));
		program.setBom(getter.getString(5, 1 + offset));
		program.setProgramName(getter.getString(6, 1 + offset));
		program.setPlanProduct(getPlanProduct(getter.getString(6, 5 + offset), getter));
		program.setStructure(getStructure(getter.getString(6, 8 + offset), getter));
		program.setAuditor(getAuditor(getter.getString(7, 4 + offset), getter));
		program.setFileName(programFile.getOriginalFilename());
		program.setId(UuidUtil.get32UUID());
		program.setCreateTime(getCurrentTime());
		program.setBoardType(boardType);
		program.setWorkOrder(workOrder);
		// 打印到控制台
		if (!OsHelper.isProductionEnvironment()) {
			FieldUtil.print(program);
		}

		// 为第一个表格的表头添加长度错误信息
		addErrorInfo(program, null, getter, "headerLength", lineName);

		// 获取表格数量
		int sheetNum = getter.getBook().getNumberOfSheets();
		result.put("realParseNum", sheetNum);
		result.put("actionName", "上传");

		// 所有表格表头的文本与第一个表格的文本进行比较
		if (sheetNum > 1) {
			for (int i = 1; i < sheetNum; i++) {
				getter.switchSheet(i);
				if ((getter.getString(0, 0) != null && !"".equals(getter.getString(0, 0))) || !header.equals(getter.getString(1, 0 + offset))) {
					throw new RuntimeException("请检查站位表中是否存在空表");
				}
				addErrorInfo(program, null, getter, "headerContent", lineName);
			}
		}

		List<ProgramItem> programItems = new ArrayList<>();
		// 填充表项
		for (int i = 0; i < sheetNum; i++) {
			getter.switchSheet(i);
			// Excel表格有效的最后一行所在的位置
			int effectiveLastRowNum;
			String content = getter.getString(getter.getBook().getSheetAt(i).getLastRowNum() - 4, 2);
			if (content != null && !"".equals(content) && content.contains("变更记录")) {
				effectiveLastRowNum = getter.getBook().getSheetAt(i).getLastRowNum() - 4;
			} else if (content != null && !"".equals(content) && content.contains("执行日期")) {
				effectiveLastRowNum = getter.getBook().getSheetAt(i).getLastRowNum() - 5;
			} else {
				throw new RuntimeException("站位表版本错误，请使用标准站位表");
			}
			for (int j = 9; j < effectiveLastRowNum; j++) {
				ProgramItem programItem = new ProgramItem();
				String lineseat = getter.getString(j, 0 + offset);
				// 空表判断
				if (lineseat.equals("")) {
					int temp = (int) result.get("realParseNum");
					result.put("realParseNum", --temp);
					break;
				}
				// 排除手盖
				if ("手盖".equals(lineseat)) {
					continue;
				}
				if (lineseat.contains("-")) {
					programItem.setLineseat(formatLineseat(lineseat));
				} else {
					getter.getErrorInfos().add("请在第 " + (j + 1) + " 行，第 2 列输入正确的站位格式，如 1-20");
				}
				programItem.setMaterialNo(getter.getString(j, 1 + offset));
				// 填写内容为0或者空时为主料，其他情况时为替料
				programItem.setAlternative(getter.getBoolean(j, 2 + offset));
				programItem.setSpecitification(getter.getString(j, 3 + offset));
				programItem.setPosition(getter.getString(j, 4 + offset));
				programItem.setQuantity(getter.getInt(j, 5 + offset));
				programItem.setSerialNo(getter.getInt(j, -1 + offset));
				// 设置programId
				programItem.setProgramId(program.getId());

				// 输出到控制台
				if (!OsHelper.isProductionEnvironment()) {
					FieldUtil.print(programItem);
				}
				programItems.add(programItem);

				// 根据表格主体的内容添加错误信息
				addErrorInfo(null, programItem, getter, "bodyLength", null);
			}
		}

		ProgramExample programExample = new ProgramExample();
		if (getter.getErrorInfos().size() < 1) {
			result.put("errorInfos", null);
			// 覆盖：如果“未开始”的工单列表中存在板面类型、工单号、线号同时一致的工单项目，将被新文件内容覆盖
			programExample.createCriteria().andWorkOrderEqualTo(program.getWorkOrder()).andBoardTypeEqualTo(program.getBoardType()).andLineEqualTo(program.getLine()).andStateEqualTo(0);
			try {
				List<Program> programs2 = programMapper.selectByExample(programExample);
				if (!programs2.isEmpty()) {
					programMapper.updateByExampleSelective(program, programExample);
					ProgramItemExample programItemExample = new ProgramItemExample();
					programItemExample.createCriteria().andProgramIdEqualTo(programs2.get(0).getId());
					programItemMapper.deleteByExample(programItemExample);
					result.put("actionName", "覆盖");
				} else {
					programMapper.insertSelective(program);
				}
				programItemMapper.insertProgramItemList(programItems);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("更新数据库出错");
			}
		} else {
			result.put("errorInfos", getter.getErrorInfos());
		}
		return result;
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
		
		if (page != null) {
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
					if (bo.getTargetLineseat().equals(programItem.getLineseat()) && bo.getTargetMaterialNo().equals(programItem.getMaterialNo())) {
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
						if (newVisit.getLineseat().equals(oldVisit.getLineseat()) && newVisit.getMaterialNo().equals(oldVisit.getMaterialNo())) {
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
	public String switchWorkOrder(String line, String workOrder, Integer boardType, Boolean selected) {
		if (line == null || line.equals("")) {
			return "succeed";
		}
		Integer lineId = lineService.getLineIdByName(line);
		if (lineId == null) {
			return "failed_not_exist";
		}
		// 获取Display
		DisplayExample displayExample = new DisplayExample();
		displayExample.createCriteria().andLineEqualTo(lineId);
		List<Display> displays = displayMapper.selectByExample(displayExample);
		if (displays == null || displays.isEmpty()) {
			return "failed_not_exist";
		}
		Display display = displays.get(0);
		int flag = 0;
		// 判断是否是停止监控
		if (workOrder == null && boardType == null) {
			display.setWorkOrder(null);
			display.setBoardType(null);
			display.setSelected(selected);
		} else {

			List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
			if (programItemVisits == null || programItemVisits.isEmpty()) {
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
			display.setSelected(selected);
		}
		displayMapper.updateByPrimaryKey(display);
		return "succeed";
	}

	
	@Override
	public String operate(String line, String workOrder, Integer boardType, Integer type, String lineseat, String scanLineseat, String scanMaterialNo, Integer operationResult) {
		if (lineService.getLineIdByName(line) == null) {
			return "failed_not_exist";
		}
		List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
		if (programItemVisits == null || programItemVisits.isEmpty()) {
			return "不存在此进行中的工单";
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
		if (programItemVisits == null || programItemVisits.isEmpty()) {
			return "不存在此进行中的工单";
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
		if (lineId == null) {
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
		ProgramExample programExample = new ProgramExample();
		LineExample lineExample = new LineExample();
		programExample.createCriteria().andIdEqualTo(programItemVisit.getProgramId());
		ProgramItemVisitExample itemVisitExample = new ProgramItemVisitExample();
		itemVisitExample.createCriteria().andProgramIdEqualTo(programItemVisit.getProgramId());
		if (programItemVisitMapper.selectByExample(itemVisitExample).isEmpty()) {
			return -1;
		}
		lineExample.createCriteria().andIdEqualTo(programMapper.selectByExample(programExample).get(0).getLine());
		String line = lineMapper.selectByExample(lineExample).get(0).getLine();
		synchronized (timeoutTimer.getLock(line)) {
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
				//遍历首检结果，如果全部为成功，则调用resetCheckAll()方法，APP不再在首检完成后调用resetCheckAll()方法。本方法要加Timeout类的线锁
				boolean hasFirstCheckAll = true;
				ProgramItemVisitExample programItemVisitExample = new ProgramItemVisitExample();
				programItemVisitExample.createCriteria().andProgramIdEqualTo(programItemVisit.getProgramId());
				List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(programItemVisitExample);
				for (ProgramItemVisit itemVisit : programItemVisits) {
					if (itemVisit.getFirstCheckAllResult() != 1) {
						hasFirstCheckAll = false;
						break;
					}
				}
				if (hasFirstCheckAll == true) {
					resetCheckAll(programItemVisit.getProgramId());
				}
				break;
			default:
				break;
			}
			return result;
		}
	}


	@Override
	public Integer resetCheckAll(String programId) {
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId);
		if (programItemVisitMapper.selectByExample(example).isEmpty()) {
			return -1;
		}
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
		if (programItemVisits.isEmpty()) {
			return -1;
		}
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
	public ResultUtil2 isAllDone(String programId, String type) {
		ResultUtil2 result = new ResultUtil2();
		OperationResult operationResult = new OperationResult("", "", "", "", "", "");
		if (programId != null && !"".equals(programId) && type != null && !"".equals(type)) {
			ProgramItemVisitExample example = new ProgramItemVisitExample();
			example.createCriteria().andProgramIdEqualTo(programId);
			List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(example);
			if(programItemVisits.isEmpty()) {
				result.setCode(-1);
				result.setMsg("查询失败，不存在此进行中的工单");
				result.setData(null);
				return result;
			}
			String[] typeStrings = type.split("&");
			for (String typeString : typeStrings) {
				operationResult = getResultByTypeAndItemVisit(programItemVisits, typeString, operationResult);
			}
			result.setCode(1);
			result.setMsg("查询成功");
			result.setData(operationResult);
		} else {
			result.setCode(0);
			result.setMsg("查询失败，参数不存在");
			result.setData(null);
		}
		return result;
	}


	@Override
	public Integer isChangeSucceed(String programId, String lineseat) {
		int result = 1;
		ProgramItemVisitExample example = new ProgramItemVisitExample();
		example.createCriteria().andProgramIdEqualTo(programId).andLineseatEqualTo(lineseat);
		List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(example);
		if (programItemVisits.isEmpty()) {
			return -1;
		}
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
	public ResultUtil2 getProgramId(String line, String workOrder, Integer boardType) {
		Integer lineId = lineService.getLineIdByName(line);
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (lineId != null) {
			ProgramExample programExample = new ProgramExample();
			programExample.createCriteria().andLineEqualTo(lineId).andWorkOrderEqualTo(workOrder).andBoardTypeEqualTo(boardType).andStateEqualTo(1);
			List<Program> programs = programMapper.selectByExample(programExample);
			if (programs.isEmpty()) {
				resultUtil2.setCode(-1);
				resultUtil2.setMsg("不存在此进行中的工单");
				return resultUtil2;
			}
			resultUtil2.setCode(1);
			resultUtil2.setMsg(programs.get(0).getId());
			resultUtil2.setData(null);
			return resultUtil2;
		}
		resultUtil2.setCode(0);
		resultUtil2.setMsg("不存在此线号");
		return resultUtil2;
	}


	@Override
	public String isCheckAllTimeOut(String line, String workOrder, Integer boardType) {
		Integer isCheckAllTimeOutExist = 1;
		List<ProgramItemVisit> programItemVisits = getVisits(line, workOrder, boardType);
		if (programItemVisits == null || programItemVisits.isEmpty()) {
			ResultUtil2 resultUtil2 = new ResultUtil2(-1, "不存在此进行中的工单");
			return JSONObject.toJSONString(resultUtil2);
		}
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			if (!programItemVisit.getCheckAllResult().equals(3)) {
				isCheckAllTimeOutExist = 0;
				break;
			}
		}
		ResultUtil2 resultUtil2 = new ResultUtil2(isCheckAllTimeOutExist, programItemVisits.get(0).getProgramId());
		return JSONObject.toJSONString(resultUtil2);
	}


	@Override
	public Date getCurrentTime() {
		return new Date();
	}


	@Override
	public String isLineMonitored(Integer lineId) {
		if (lineId == null) {
			return "failed_not_exist";
		}
		String lineName = lineService.getLineNameById(lineId);
		if (lineName == null || "".equals(lineName)) {
			return "failed_not_exist";
		}
		DisplayExample displayExample = new DisplayExample();
		displayExample.createCriteria().andLineEqualTo(lineId);
		List<Display> displays = displayMapper.selectByExample(displayExample);
		if (displays != null && displays.size() > 0 && displays.get(0).getSelected()) {
			return "succeed";
		}
		return "failed_not_exist";
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
	private int getPlanProduct(String planProduct, ExcelPopularGetter getter) {
		if (planProduct.contains("k") || planProduct.contains("K")) {
			String lowerPlanProduct = planProduct.toLowerCase();
			String result = planProduct.toLowerCase().substring(0, lowerPlanProduct.indexOf("k"));
			return Integer.parseInt(result) * 1000;
		} else {
			if (!"".equals(planProduct) && planProduct.length() > 0 && isNumber(planProduct)) {
				return Integer.parseInt(planProduct);
			}
			getter.getErrorInfos().add("计划生产总数必须为数字");
			return 0;
		}
	}


	/**@author HCJ
	 * 获取从Excel表格读取的联板数
	 * @date 2019年1月27日 上午10:13:32
	 */
	private int getStructure(String structure, ExcelPopularGetter getter) {
		if (!"".equals(structure) && structure.length() > 0 && isNumber(structure)) {
			return Integer.parseInt(structure);
		}
		getter.getErrorInfos().add("连板数必须为数字");
		return 0;
	}


	/**@author HCJ
	 * 获取从Excel表格读取的时间
	 * @date 2019年1月27日 上午10:49:53
	 */
	private String getEffectiveDate(Date effectiveDate) {
		if (effectiveDate != null) {
			return effectiveDate.toString();
		}
		return getCurrentTime().toString();
	}


	/**@author HCJ
	 * 获取从Excel表格读取的审核人
	 * @date 2019年2月18日 下午3:00:45
	 */
	private String getAuditor(String content, ExcelPopularGetter getter) {
		if (content.length() > 3) {
			String auditor = content.substring(3);
			if (!auditor.trim().isEmpty()) {
				return auditor;
			}
		}
		getter.getErrorInfos().add("审核人不能为空");
		return "";
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


	/**@author HCJ
	 * 添加错误信息
	 * @param errorInfoType 相关的错误信息类型
	 * @param lineName 产线名称
	 * @date 2019年3月27日 上午10:36:55
	 */
	private void addErrorInfo(Program program, ProgramItem programItem, ExcelPopularGetter getter, String errorInfoType, String lineName) {
		int offset = 1;
		if ("headerLength".equals(errorInfoType)) {
			addLengthErrorInfo(program.getClient(), getter, FIELD_LENGTH, "产品客户");
			addLengthErrorInfo(program.getAuditor(), getter, FIELD_LENGTH, "审核人");
			addLengthErrorInfo(program.getMachineName(), getter, FIELD_LENGTH, "机型名称");
			addLengthErrorInfo(program.getFileName(), getter, MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH, "文件名");
			addLengthErrorInfo(program.getVersion(), getter, FIELD_LENGTH, "版本");
			addLengthErrorInfo(program.getMachineConfig(), getter, FIELD_LENGTH, "机器配置");
			addLengthErrorInfo(program.getProgramNo(), getter, FIELD_LENGTH, "程序表编号");
			addLengthErrorInfo(program.getPcbNo(), getter, PCBNO_LENGTH, "PCB NO");
			addLengthErrorInfo(program.getBom(), getter, BOM_LENGTH, "BOM文件的信息");
			addLengthErrorInfo(program.getProgramName(), getter, MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH, "程序名");
			addLengthErrorInfo(program.getWorkOrder(), getter, MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH, "工单");
			addSizeErrorInfo(program.getPlanProduct(), getter, "计划生产总数");
			addSizeErrorInfo(program.getStructure(), getter, "连板");
		} else if ("headerContent".equals(errorInfoType)) {
			addContentMatchErrorInfo(getter.getString(2, 1 + offset), program.getClient(), getter, "产品客户");
			addContentMatchErrorInfo(getter.getString(2, 4 + offset), program.getMachineName(), getter, "机型名称");
			addContentMatchErrorInfo(getter.getString(2, 6 + offset), program.getVersion(), getter, "版本");
			addContentMatchErrorInfo(getter.getString(3, 1 + offset), program.getMachineConfig(), getter, "机器配置");
			addContentMatchErrorInfo(getter.getString(3, 4 + offset), program.getProgramNo(), getter, "程序表编号");
			addContentMatchErrorInfo(getter.getString(3, 6 + offset), lineName, getter, "线别");
			addContentMatchErrorInfo(getter.getString(4, 4 + offset), program.getPcbNo(), getter, "PCB NO");
			addContentMatchErrorInfo(getter.getString(4, 6 + offset), program.getWorkOrder(), getter, "工单");
			addContentMatchErrorInfo(String.valueOf(getPlanProduct(getter.getString(6, 5 + offset), getter)), program.getPlanProduct().toString(), getter, "计划生产总数");
			addContentMatchErrorInfo(String.valueOf(getStructure(getter.getString(6, 8 + offset), getter)), program.getStructure().toString(), getter, "连板");
		} else if ("bodyLength".equals(errorInfoType)) {
			addLengthErrorInfo(programItem.getLineseat(), getter, FIELD_LENGTH, "站位");
			addLengthErrorInfo(programItem.getMaterialNo(), getter, MATERIALNO_FILENAME_PROGRAMNAME_WORKORDER_LENGTH, "程序料号");
			addLengthErrorInfo(programItem.getPosition(), getter, POSITION_SPECITIFICATION_LENGTH, "单板位置");
			addLengthErrorInfo(programItem.getSpecitification(), getter, POSITION_SPECITIFICATION_LENGTH, "BOM料号/规格");
			addSizeErrorInfo(programItem.getQuantity(), getter, "数量");
			addSizeErrorInfo(programItem.getSerialNo(), getter, "序列号");
		}
	}


	/**@author HCJ
	 * 根据文本长度是否符合规范添加错误信息
	 * @param content 文本内容
	 * @param length 最大长度
	 * @date 2019年3月27日 上午10:34:58
	 */
	private void addLengthErrorInfo(String content, ExcelPopularGetter getter, Integer length, String field) {
		if (content!=null&&content.length() > length) {
			getter.addErrorInfo("表格 "+getter.getSheetName()+" 的 " + field + " 长度不能大于 " + length + " ，请及时修改\n");
		}
	}


	/**@author HCJ
	 * 根据数值大小添加错误信息
	 * @param number 需要进行判断的数值
	 * @date 2019年3月27日 上午10:33:40
	 */
	private void addSizeErrorInfo(Integer number, ExcelPopularGetter getter, String field) {
		if (number != null && number.intValue() >= Integer.MAX_VALUE) {
			getter.addErrorInfo("表格 " + getter.getSheetName() + " 的 " + field + " 大小不能大于或者等于 " + Integer.MAX_VALUE + " ，请及时修改\n");
		}
	}


	/**@author HCJ
	 * 根据文本是否匹配添加错误信息
	 * @param content 要比较的文本内容
	 * @param standardContent 标准文本内容
	 * @date 2019年3月27日 上午10:31:38
	 */
	private void addContentMatchErrorInfo(String content, String standardContent, ExcelPopularGetter getter, String field) {
		if (content != null && !standardContent.equals(content)) {
			getter.addErrorInfo("表格 " + getter.getSheetName() + " 的 " + field + " 必须与第一个表格的 " + field + " 填写一致，请及时修改\n");
		}
	}


	private void clearVisits(String programId) {
		ProgramItemVisitExample programItemVisitExample = new ProgramItemVisitExample();
		programItemVisitExample.createCriteria().andProgramIdEqualTo(programId);
		programItemVisitMapper.deleteByExample(programItemVisitExample);
	}


	/**@author HCJ
	 * 根据ItemVisit和操作类型设置操作结果的值
	 * @date 2019年1月27日 上午8:34:11
	 */
	private OperationResult getResultByTypeAndItemVisit(List<ProgramItemVisit> programItemVisits, String typeString, OperationResult operationResult) {
		switch (typeString) {
		case "0":
			operationResult.setFeed("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getFeedResult() != 1) {
					operationResult.setFeed("0");
					break;
				}
			}
			break;
		case "1":
			operationResult.setChange("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getChangeResult() != 1) {
					operationResult.setChange("0");
					break;
				}
			}
			break;
		case "2":
			operationResult.setCheck("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getCheckResult() != 1) {
					operationResult.setCheck("0");
					break;
				}
			}
			break;
		case "3":
			operationResult.setCheckAll("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getCheckAllResult() != 1) {
					operationResult.setCheckAll("0");
					break;
				}
			}
			break;
		case "4":
			operationResult.setStore("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getStoreIssueResult() != 1) {
					operationResult.setStore("0");
					break;
				}
			}
			break;
		case "5":
			operationResult.setFirstCheckAll("1");
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getFirstCheckAllResult() != 1) {
					operationResult.setFirstCheckAll("0");
					break;
				}
			}
			break;
		default:
			break;
		}
		return operationResult;
	}

}
