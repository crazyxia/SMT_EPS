package com.jimi.smt.eps_server.entity.filler;

import java.text.SimpleDateFormat;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.OperationDetials;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class OperationToClientReportFiller extends EntityFieldFiller<OperationDetials, ClientReport> {
	
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
	public ClientReport fill(OperationDetials operation) {	

		ClientReport clientReport = new ClientReport();
		//拷贝相同属性
		BeanUtils.copyProperties(operation, clientReport);
		//填写工单
		clientReport.setWorkOrderNo(operation.getWorkOrder());
		if(operation.getLine() != null) {
			clientReport.setLine(operation.getLine());
		}
		
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(operation.getTime());
		clientReport.setTime(time);
		
		//匹配程序表子项目和操作日志
//		String key = operation.getProgramId()+operation.getLineseat()+operation.getMaterialNo();
//		ProgramItem programItem = programItemMaps.get(key);
		
		if (operation.getProgramId() != null) {
			//解析料描述和料规格
			String specitification = operation.getSpecitification();
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
