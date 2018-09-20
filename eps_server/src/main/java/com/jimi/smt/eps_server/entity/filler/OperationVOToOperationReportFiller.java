package com.jimi.smt.eps_server.entity.filler;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.ProgramItem;
import com.jimi.smt.eps_server.entity.vo.OperationReport;
import com.jimi.smt.eps_server.entity.vo.OperationVO;
import com.jimi.smt.eps_server.mapper.ProgramItemMapper;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class OperationVOToOperationReportFiller extends EntityFieldFiller<OperationVO, OperationReport> {

	@Autowired
	private ProgramItemMapper programItemMapper;
	
	private List<ProgramItem> programItems;
	private Map<String, ProgramItem> programItemMaps = new HashMap<>();
	
	synchronized public void init() {
		programItems = programItemMapper.selectByExample(null);
		
		for (ProgramItem programItem : programItems) {
			programItemMaps.put(programItem.getProgramId()+programItem.getLineseat()+programItem.getMaterialNo(), programItem);
		}
	}
		
	@Override
	public OperationReport fill(OperationVO operationVO) {
		OperationReport operationReport = new OperationReport();
		//拷贝相同属性
		BeanUtils.copyProperties(operationVO, operationReport);
		//填写工单
		operationReport.setWorkOrderNo(operationVO.getWorkOrder());
		
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(operationVO.getTime());
		operationReport.setTime(time);
		operationReport.setLine(operationVO.getLineName());
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
					operationReport.setMaterialDescription("-");
					operationReport.setMaterialSpecitification(specitification);
				}else {
					String materialSpecitification = specitification.substring(specitification.indexOf(",") + 1, specitification.indexOf(";"));
					operationReport.setMaterialDescription(materialDescription);
					operationReport.setMaterialSpecitification(materialSpecitification);
				}
			}catch (StringIndexOutOfBoundsException e) {
				operationReport.setMaterialDescription("-");
				operationReport.setMaterialSpecitification(specitification);
			}	
		}
		
		return operationReport;
	}

}
