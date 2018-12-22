package com.jimi.smt.eps_server.entity.filler;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.ProgramItemExample;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemMapper;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class OperationToClientReportFiller extends EntityFieldFiller<Operation, ClientReport> {

	@Autowired
	private ProgramItemMapper programItemMapper;
	@Autowired
	private LineMapper lineMapper;
	
//	private List<ProgramItem> programItems;
	
//	private Map<String, ProgramItem> programItemMaps;
	
	
//	public void init() {
//		programItemMaps = new HashMap<>();
//		programItems = programItemMapper.selectByExample(null);
//		for (ProgramItem programItem : programItems) {
//			programItemMaps.put(programItem.getProgramId() + programItem.getLineseat() + programItem.getMaterialNo(), programItem);	
//		}
//	}
//	
//	
//	public void destroy() {
//		programItems = null;
//		programItemMaps = null;
//	}
	
	
	@Override
	public ClientReport fill(Operation operation) {	

		ClientReport clientReport = new ClientReport();
		//拷贝相同属性
		BeanUtils.copyProperties(operation, clientReport);
		//填写工单
		clientReport.setWorkOrderNo(operation.getWorkOrder());
		Line line = lineMapper.selectByPrimaryKey(operation.getLine());
		if(line != null) {
			clientReport.setLine(line.getLine());
		}
		
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(operation.getTime());
		clientReport.setTime(time);
		
		//匹配程序表子项目和操作日志
//		String key = operation.getProgramId()+operation.getLineseat()+operation.getMaterialNo();
		List<ProgramItem> programItems;
		ProgramItemExample programItemExample = new ProgramItemExample();
		programItemExample.createCriteria()
			.andProgramIdEqualTo(operation.getProgramId())
			.andLineseatEqualTo(operation.getLineseat())
			.andMaterialNoEqualTo(operation.getMaterialNo());
		programItems = programItemMapper.selectByExample(programItemExample);
//		ProgramItem programItem = programItemMaps.get(key);
		
		if (programItems != null && !programItems.isEmpty()) {
			//解析料描述和料规格
			ProgramItem programItem = programItems.get(0);
			String specitification = programItem.getSpecitification();
			try {
				String materialDescription = specitification.substring(0, specitification.indexOf(","));
				String temp = specitification.substring(specitification.indexOf(";") + 5, specitification.lastIndexOf(";") - 4);
				if(!temp.equals(materialDescription)) {
					clientReport.setMaterialDescription("-");
					clientReport.setMaterialSpecitification(specitification);
				}else {
					String materialSpecitification = specitification.substring(specitification.indexOf(",") + 1, specitification.indexOf(";"));
					clientReport.setMaterialDescription(materialDescription);
					clientReport.setMaterialSpecitification(materialSpecitification);
				}
			}catch (StringIndexOutOfBoundsException e) {
				clientReport.setMaterialDescription("-");
				clientReport.setMaterialSpecitification(specitification);
			}
		}
		
		//解析操作类型
		switch (operation.getType()) {
		case 0:
			clientReport.setOperationType("上料");
			break;
		case 1:
			clientReport.setOperationType("换料");
			break;
		case 2:
			clientReport.setOperationType("核料");
			break;
		case 3:
			clientReport.setOperationType("全检");
			break;
		case 4:
			clientReport.setOperationType("发料");
			break;
		case 5:
			clientReport.setOperationType("首检");
			break;
		default:
			break;
		}
		
		return clientReport;
	}

}
