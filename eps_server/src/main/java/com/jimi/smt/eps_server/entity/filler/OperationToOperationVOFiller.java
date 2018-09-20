package com.jimi.smt.eps_server.entity.filler;

import org.springframework.beans.BeanUtils;

import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.vo.OperationVO;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

public class OperationToOperationVOFiller extends EntityFieldFiller<Operation, OperationVO>{

	@Override
	public OperationVO fill(Operation Operation) {
		OperationVO operationVO = new OperationVO();
		BeanUtils.copyProperties(Operation, operationVO);		
		return operationVO;
	}
}