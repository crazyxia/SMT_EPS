package com.jimi.smt.eps_server.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;
import com.jimi.smt.eps_server.util.ResultUtil2;

/**
 * 排位表业务接口
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public interface ProgramService {
	
	/**
	 * 根据条件列出线上的排位表
	 * @return
	 */
	List<ProgramVO> list(String programName, String fileName, Integer line, String workOrder, Integer state, String ordBy, Page page);
	
	
	/**
	 * 上传并解析文件，如果“未开始”的工单列表中存在板面类型、工单号、线号同时一致的工单项目，将被新文件内容覆盖
	 * @param programFile 文件
	 * @return 
	 * @throws IOException IO异常
	 * @throws InvalidFormatException 无效的格式异常
	 * @throws XLSException 解析异常
	 */
	List<Map<String, Object>> upload(MultipartFile programFile, Integer boardType) throws IOException, RuntimeException, InvalidFormatException;


	/**
	 * 取消指定工单
	 * @param workOrder
	 * @return
	 */
	boolean cancel(String id);


	/**
	 * 完成指定工单
	 * @param workOrder
	 * @return
	 */
	boolean finish(String id);


	/**
	 * 开始指定工单
	 * @param workOrder
	 * @return
	 */
	String start(String id);


	/**
	 * 列出指定id排位表的所有子项目
	 * @param id
	 * @return
	 */
	List<ProgramItemVO> listItem(String id);
	
	
	/**
	 * 更新一项工单的内容
	 * @param programItemVOs
	 * @return
	 */
	String updateItem(List<EditProgramItemBO> BOs);
	
	
	/**
	 * 适用于实时监控程序切换当前显示工单
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	String switchWorkOrder(String line, String workOrder, Integer boardType);
	
	
	/**
	 * 适用于APP进行对物料的操作
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @param type 操作类型：2：核料；3：全检；
	 * @param lineseat
	 * @param materialNo
	 * @param scanMaterialNo 
	 * @param scanLineseat 
	 * @return
	 */
	String operate(String line, String workOrder, Integer boardType, Integer type, String lineseat, String scanLineseat, String scanMaterialNo, Integer operationResult);
	
	
	/**
	 * 适用于APP/实时监控程序对指定工单进行重置
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	String reset(String line, String workOrder, Integer boardType);
	
	
	/**
	 * 获取Visit数据
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	List<ProgramItemVisit> getVisits(String line, String workOrder, Integer boardType);
	
	
	/**
	 * 更新一个Visit
	 * @param visit
	 */
	void updateVisit(ProgramItemVisit visit);
	
	
	/**
	 * 列出所有实时监控程序选择的对应工单
	 * @return
	 */
	List<Display> listDisplays();
	
	
	/**@author HCJ
	 * 返回当前产线正在进行的工单列表
	 * @method selectWorkingProgram
	 * @param line
	 * @return
	 * @return List<ProgramVO>
	 * @date 2018年9月25日 下午4:31:54
	 */
	List<ProgramVO> selectWorkingProgram(String line);	
	
	
	/**@author HCJ
	 * 更新program_item_visit 表日志
	 * @method updateItemVisit
	 * @param programItemVisit
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:31:41
	 */
	Integer updateItemVisit(ProgramItemVisit programItemVisit);
	
	
	/**@author HCJ
	 * 首检完成后重置全检时间和全检结果
	 * @method resetCheckAll
	 * @param programId
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:31:25
	 */
	Integer resetCheckAll(String programId);
		
	
	/**@author HCJ
	 * 判断工单是否重置了
	 * @method checkIsReset
	 * @param programId
	 * @param type
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:31:13
	 */
	Integer checkIsReset(String programId, int type);
	
	
	/**@author HCJ
	 * 返回工单是否全部完成某项操作的结果
	 * @method isAllDone
	 * @param programId
	 * @param type
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:30:53
	 */
	ResultUtil2 isAllDone(String programId, String type);
	
	
	/**@author HCJ
	 * 核料时判断某个站位是否换料成功
	 * @method isChangeSucceed
	 * @param programId
	 * @param lineseat
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:30:42
	 */
	Integer isChangeSucceed(String programId, String lineseat);
	
	
	/**@author HCJ
	 * 返回工单集合
	 * @method selectWorkingOrder
	 * @param line
	 * @return
	 * @return List<String>
	 * @date 2018年9月25日 下午4:30:29
	 */
	List<String> selectWorkingOrder(String line);
	
	
	/**@author HCJ
	 * 返回版面类型集合
	 * @method selectWorkingBoardType
	 * @param line
	 * @param workOrder
	 * @return
	 * @return List<Integer>
	 * @date 2018年9月25日 下午4:30:11
	 */
	List<Integer> selectWorkingBoardType(String line, String workOrder);
	
	
	/**@author HCJ
	 * 返回ProgramItemVisit集合
	 * @method selectItemVisitByProgram
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 * @return List<ProgramItemVisit>
	 * @date 2018年9月25日 下午4:30:00
	 */
	List<ProgramItemVisit> selectItemVisitByProgram(String line, String workOrder, int boardType);
	
	
	/**@author HCJ
	 * 返回最新操作的操作员
	 * @method selectLastOperatorByProgram
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 * @return String
	 * @date 2018年9月25日 下午4:29:48
	 */
	String selectLastOperatorByProgram(String line, String workOrder, Integer boardType);
	
	
	/**@author HCJ
	 * 返回工单id
	 * @method getProgramId
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 * @return String
	 * @date 2018年9月25日 下午4:29:28
	 */
	String getProgramId(String line, String workOrder, Integer boardType);
	
	
	/**@author HCJ
	 * 判断当前工单全检是否超时
	 * @date 2018年12月21日 上午9:58:10
	 */
	String isCheckAllTimeOut(String line, String workOrder, Integer boardType);
	
	
	/**@author HCJ
	 * 获取服务器当前时间
	 * @date 2018年12月21日 下午6:59:10
	 */
	Date getCurrentTime();
	
}
