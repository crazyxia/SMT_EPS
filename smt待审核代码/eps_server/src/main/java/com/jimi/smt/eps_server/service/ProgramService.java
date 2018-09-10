package com.jimi.smt.eps_server.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.bo.EditProgramItemBO;
import com.jimi.smt.eps_server.entity.vo.ProgramItemVO;
import com.jimi.smt.eps_server.entity.vo.ProgramVO;

/**
 * 排位表业务接口
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public interface ProgramService {
	
	/**
	 * 根据条件列出线上的排位表
	 * @return
	 */
	List<ProgramVO> list(String programName, String fileName, String line, String workOrder, Integer state, String ordBy);
	
	
	/**
	 * 上传并解析文件，如果“未开始”的工单列表中存在板面类型、工单号、线号同时一致的工单项目，将被新文件内容覆盖
	 * @param programFile 文件
	 * @return 
	 * @throws IOException IO异常
	 * @throws XLSException 解析异常
	 */
	List<Map<String, Object>> upload(MultipartFile programFile, Integer boardType) throws IOException, RuntimeException;


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
	
	/**
	 * 返回当前产线正在进行的工单列表
	 * @param line
	 * @return
	 */
	List<Program> selectWorkingProgram(String line);
	
	/**
	 * 获取当前工单的料号信息
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	List<ProgramItem> selectProgramItem(String programId);
	
	/**
	 * 更新program_item_visit 表日志
	 * @param programItemVisit
	 * @return
	 */
	int updateItemVisit(ProgramItemVisit programItemVisit);
	
	/**
	 * 首检完成后重置全检时间和全检结果
	 * @param programId
	 * @return
	 */
	int resetCheckAll(String programId);
			
	/**
	 * 判断工单是否重置了
	 * @param programId
	 * @param type
	 * @return
	 */
	int checkIsReset(String programId, int type);
	
	/**
	 * 返回某个工单是否全部完成某项操作的结果
	 * @param programId
	 * @param type
	 * @return
	 */
	int isAllDone(String programId, int type);
	
	/**
	 * 核料时判断某个站位是否换料成功
	 * @param programId
	 * @param lineseat
	 * @return
	 */
	int isChangeSucceed(String programId, String lineseat);
	
	/**
	 * 返回工单的集合
	 * @param line
	 * @return
	 */
	List<String> selectWorkingOrder(String line);
	
	/**
	 * 返回版面类型的集合
	 * @param line
	 * @param workOrder
	 * @return
	 */
	List<String> selectWorkingBoardType(String line, String workOrder);
	
	/**
	 * 返回ProgramItemVisit集合
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	List<ProgramItemVisit> selectItemVisitByProgram(String line, String workOrder, int boardType);
	
	/**
	 * 返回最新操作的操作员
	 * @param line
	 * @param workOrder
	 * @param boardType
	 * @return
	 */
	String selectLastOperatorByProgram(String line, String workOrder, Integer boardType);
}
