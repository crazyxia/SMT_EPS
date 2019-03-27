package com.jimi.smt.eps_server.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;


/**获取Excel表格单元格信息
 * @author   HCJ
 * @date     2019年1月29日 上午11:54:21
 */
public class ExcelPopularGetter extends ExcelHelper {

	private List<String> errorInfos = new ArrayList<>();

	/**
	 * CLIENT_AND_MACHINENAME_AND_VERSION_ROW : 客户、机器名和版本所在的行
	 */
	private static final int CLIENT_AND_MACHINENAME_AND_VERSION_ROW = 2;
	/**
	 * MACHINECONFIG_AND_PROGRAMNO_AND_LINE_ROW : 机器配置、程序表编号和产线所在的行
	 */
	private static final int MACHINECONFIG_AND_PROGRAMNO_AND_LINE_ROW = 3;
	/**
	 * EFFECTIVEDATE_AND_PCBNO_AND_WORKDERORDER_ROW : 生效日期、pcbno和工单所在的行
	 */
	private static final int EFFECTIVEDATE_AND_PCBNO_AND_WORKDERORDER_ROW = 4;
	/**
	 * PLAN_PRODUCT_NUM : 计划生产数量和联板数所在的行列值
	 */
	private static final int PLANPRODUCT_AND_STRUCTURE_NUM = 6;
	/**
	 * STRUCTURE_COL_NUM : 联板数所在的列
	 */
	private static final int STRUCTURE_COL_NUM = 9;
	
	/**
	 * SERIALNO_NUM : 校验序列号时所在的行列值
	 */
	private static final int SERIALNO_NUM = 0;

	/**
	 * sheetName : 表格名称
	 */
	private String sheetName;


	/**
	 * 传入一个excel表格，构造Helper
	 * @throws InvalidFormatException 
	 */
	public static ExcelPopularGetter from(MultipartFile file) throws IOException, InvalidFormatException {
		return new ExcelPopularGetter(file);
	}


	/**@author HCJ
	 * 获取错误信息集合
	 * @date 2019年1月29日 上午11:01:46
	 */
	public List<String> getErrorInfos() {
		return errorInfos;
	}


	/**@author HCJ
	 * 添加错误信息
	 * @date 2019年1月29日 上午11:02:03
	 */
	public void addErrorInfo(String errorInfo) {
		errorInfos.add(errorInfo);
	}


	/**@author HCJ
	 * 获取表格名称
	 * @date 2019年3月27日 下午5:35:53
	 */
	public String getSheetName() {
		return sheetName;
	}


	/**
	 * 根据行数、列数和类型获取excel表格单元的值
	 */
	protected Object get(int rowNum, int colNum, RequireType requireType) {
		sheetName = workbook.getSheetAt(currentSheetNum).getSheetName();
		try {
			Cell cell = workbook.getSheetAt(currentSheetNum).getRow(rowNum).getCell(colNum);

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				switch (requireType) {
				case BOOLEAN:
					return cell.getBooleanCellValue();
				case STRING:
					return cell.getBooleanCellValue() ? "true" : "false";
				case INT:
					return cell.getBooleanCellValue() ? 1 : 0;
				case DATE:
					logger.error("无法把表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的数值转成日期，请选择正确的单元格格式并且输入正确的数值");
					errorInfos.add("无法把表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的数值转成日期，请选择正确的单元格格式并且输入正确的数值");
					return null;
				case DOUBLE:
					return cell.getBooleanCellValue() ? 1.0d : 0.0d;
				}
			case Cell.CELL_TYPE_NUMERIC:
				switch (requireType) {
				case BOOLEAN:
					return cell.getNumericCellValue() != 0 ? true : false;
				case STRING:
					return String.valueOf((int) cell.getNumericCellValue());
				case INT:
					return (int) cell.getNumericCellValue();
				case DATE:
					return cell.getDateCellValue();
				case DOUBLE:
					return cell.getNumericCellValue();
				}
			case Cell.CELL_TYPE_FORMULA:
			case Cell.CELL_TYPE_STRING:
				switch (requireType) {
				case BOOLEAN:
					return cell.getStringCellValue().equals("") ? false : true;
				case STRING:
					if (cell.getStringCellValue().trim().isEmpty()) {
						if (rowNum == PLANPRODUCT_AND_STRUCTURE_NUM && colNum == PLANPRODUCT_AND_STRUCTURE_NUM) {
							errorInfos.add("无法获取表格 " + sheetName + " 计划生产总数的值，该单元格可能为空");
						} else if ((rowNum == PLANPRODUCT_AND_STRUCTURE_NUM && colNum == STRUCTURE_COL_NUM)) {
							errorInfos.add("无法获取表格 " + sheetName + " 连板的值，该单元格可能为空");
						} else if (rowNum == CLIENT_AND_MACHINENAME_AND_VERSION_ROW || rowNum == MACHINECONFIG_AND_PROGRAMNO_AND_LINE_ROW || rowNum == EFFECTIVEDATE_AND_PCBNO_AND_WORKDERORDER_ROW) {
							errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + colNum + " 列的值，该单元格可能为空");
						} else {
							errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的值，该单元格可能为空");
						}
					}
					return cell.getStringCellValue();
				case INT:
					return Integer.parseInt(cell.getStringCellValue());
				case DATE:
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cell.getStringCellValue());
				case DOUBLE:
					return Double.parseDouble(cell.getStringCellValue());
				}
			default:
				switch (requireType) {
				case BOOLEAN:
					return false;
				case STRING:
					if (rowNum == PLANPRODUCT_AND_STRUCTURE_NUM && colNum == PLANPRODUCT_AND_STRUCTURE_NUM) {
						errorInfos.add("无法获取表格 " + sheetName + " 计划生产总数的值，该单元格可能为空");
					} else if ((rowNum == PLANPRODUCT_AND_STRUCTURE_NUM && colNum == STRUCTURE_COL_NUM)) {
						errorInfos.add("无法获取表格 " + sheetName + " 连板的值，该单元格可能为空");
					} else if (rowNum == CLIENT_AND_MACHINENAME_AND_VERSION_ROW || rowNum == MACHINECONFIG_AND_PROGRAMNO_AND_LINE_ROW || rowNum == EFFECTIVEDATE_AND_PCBNO_AND_WORKDERORDER_ROW) {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + colNum + " 列的值，该单元格可能为空");
					} else {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的值，该单元格可能为空");
					}
					return "";
				case INT:
					return 0;
				case DATE:
					logger.error("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列输入正确的日期格式，如 2019/1/13");
					errorInfos.add("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列输入正确的日期格式，如 2019/1/13");
					return null;
				case DOUBLE:
					return 0.0d;
				default:
					return null;
				}
			}
		} catch (NullPointerException e) {
			logger.error("无法获取表格" + sheetName + "第 " + (rowNum + 1) + "行,第" + (colNum + 1) + "列的值，该单元格可能为空");
			if (rowNum != SERIALNO_NUM && colNum != SERIALNO_NUM) {
				errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的值，该单元格可能为空");
			}
			return null;
		} catch (NumberFormatException e) {
			logger.error("无法把表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的数值转成文本形式，请输入数字");
			errorInfos.add("无法把表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的数值转成文本形式，请输入数字");
			return null;
		} catch (ParseException e) {
			logger.error("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列输入正确的日期格式，如 2019/1/13");
			errorInfos.add("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列输入正确的日期格式，如 2019/1/13");
			return null;
		}
	}


	/**
	 * 根据文件格式创建workbook
	 * @throws InvalidFormatException 
	 */
	private ExcelPopularGetter(MultipartFile file) throws IOException, InvalidFormatException {
		// 判断格式
		/*if (file.getOriginalFilename().endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else {
			workbook = new HSSFWorkbook(file.getInputStream());
		}*/
		workbook = WorkbookFactory.create(file.getInputStream());
		init();
	}
}
