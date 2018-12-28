package com.jimi.smt.eps_server.entity.filler;

import java.text.SimpleDateFormat;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.OperationDetials;
import com.jimi.smt.eps_server.entity.vo.OperationReport;
import com.jimi.smt.eps_server.util.EntityFieldFiller;


@Component
public class OperationToOperationReportFiller extends EntityFieldFiller<OperationDetials, OperationReport> {

	// private List<ProgramItem> programItems;
	//
	// private Map<String, ProgramItem> programItemMaps;
	//
	//
	// public void init() {
	// programItemMaps = new HashMap<>();
	// programItems = programItemMapper.selectByExample(null);
	// for (ProgramItem programItem : programItems) {
	// programItemMaps.put(programItem.getProgramId() + programItem.getLineseat() +
	// programItem.getMaterialNo(), programItem);
	// }
	// }
	//
	//
	// public void destroy() {
	// programItems = null;
	// programItemMaps = null;
	// }

	@Override
	public OperationReport fill(OperationDetials operation) {
		OperationReport operationReport = new OperationReport();
		// 拷贝相同属性
		BeanUtils.copyProperties(operation, operationReport);
		// 填写工单
		operationReport.setWorkOrderNo(operation.getWorkOrder());

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(operation.getTime());
		operationReport.setTime(time);

		if (operation.getLine() != null) {
			operationReport.setLine(operation.getLine());
		}
		// 匹配程序表子项目和操作日志
		// String key =
		// operation.getProgramId()+operation.getLineseat()+operation.getMaterialNo();
		/*
		 * List<ProgramItem> programItems; ProgramItemExample programItemExample = new
		 * ProgramItemExample(); programItemExample.createCriteria()
		 * .andProgramIdEqualTo(operation.getProgramId())
		 * .andLineseatEqualTo(operation.getLineseat())
		 * .andMaterialNoEqualTo(operation.getMaterialNo()); programItems =
		 * programItemMapper.selectByExample(programItemExample);
		 */
		// ProgramItem programItem = programItemMaps.get(key);

		if (operation.getProgramId() != null) {
			// 解析料描述和料规格
			String specitification = operation.getSpecitification();
			try {
				String materialDescription = specitification.substring(0, specitification.indexOf(","));
				String temp = specitification.substring(specitification.indexOf(";") + 5, specitification.lastIndexOf(";") - 4);
				if (!temp.equals(materialDescription)) {
					operationReport.setMaterialDescription("-");
					operationReport.setMaterialSpecitification(specitification);
				} else {
					String materialSpecitification = specitification.substring(specitification.indexOf(",") + 1, specitification.indexOf(";"));
					operationReport.setMaterialDescription(materialDescription);
					operationReport.setMaterialSpecitification(materialSpecitification);
				}
			} catch (StringIndexOutOfBoundsException e) {
				operationReport.setMaterialDescription("-");
				operationReport.setMaterialSpecitification(specitification);
			}
		}
		// 解析操作类型
		switch (operation.getType()) {
		case 0:
			operationReport.setOperationType("上料");
			break;
		case 1:
			operationReport.setOperationType("换料");
			break;
		case 2:
			operationReport.setOperationType("核料");
			break;
		case 3:
			operationReport.setOperationType("全检");
			break;
		case 4:
			operationReport.setOperationType("发料");
			break;
		case 5:
			operationReport.setOperationType("首检");
			break;
		default:
			break;
		}

		return operationReport;
	}

}
