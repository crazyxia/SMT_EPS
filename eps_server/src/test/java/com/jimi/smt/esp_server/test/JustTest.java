package com.jimi.smt.esp_server.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jimi.smt.esp_server.base.BaseJunit4Test;
import com.jimi.smt.esp_server.controller.TestController;

public class JustTest extends BaseJunit4Test {

	@Autowired
	private TestController controller;
	
	@Test
	public void test() {
		System.out.println(controller.i);
	}
	
}
