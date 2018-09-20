package com.jimi.smt.eps_server.entity.filler;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.entity.vo.OperationVO;
import com.jimi.smt.eps_server.mapper.ProgramItemMapper;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class OperationVOToClientReportFiller extends EntityFieldFiller<OperationVO, ClientReport> {

	@Autowired
	private ProgramItemMapper programItemMapper;
	
	private List<ProgramItem> programItems;
	
	private Map<String, ProgramItem> programItemMaps = new HashMap<>();
	
	synchronized public void init() {
		programItems = programItemMapper.selectByExample(null);
		
		for (ProgramItem programItem : programItems) {
			programItemMaps.put(programItem.getProgramId() + programItem.getLineseat() + programItem.getMaterialNo(),
					programItem);
		}
	}
	
	@Override
	public ClientReport fill(OperationVO operationVO) {	

		ClientReport clientReport = new ClientReport();
		//拷贝相同属性
		BeanUtils.copyProperties(operationVO, clientReport);
		//填写工单
		clientReport.setWorkOrderNo(operationVO.getWorkOrder());
		clientReport.setLine(operationVO.getLineName());
		
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(operationVO.getTime());
		clientReport.setTime(time);
		
		//匹配程序表子项目和操作日志
		String key = operationVO.getProgramId()+operationVO.getLineseat()+operationVO.getMaterialNo();
		ProgramItem programItem = programItemMaps.get(key);
		
		if (programItem != null) {
			//解析料描述和料规格
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
		switch (operationVO.getType()) {
		case 0:
			clientReport.setOperationType("上料");
			break;
		case 1:
			clientReport.setOperationType("换料");
			break;
		case 2:
			clientReport.setOperationType("检料");
			break;
		case 3:
			clientReport.setOperationType("核对全料");
			break;
		case 4:
			clientReport.setOperationType("仓库发料");
			break;
		default:
			break;
		}
		
		return clientReport;
	}

}
