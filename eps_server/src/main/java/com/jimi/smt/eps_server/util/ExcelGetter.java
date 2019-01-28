package com.jimi.smt.eps_server.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelGetter extends ExcelHelper {

	private List<String> errorInfos = new ArrayList<>();
	/**
	 * NUM_TWO : 数字2
	 */
	private static final int NUM_TWO = 2;
	/**
	 * NUM_THREE : 数字3
	 */
	private static final int NUM_THREE = 3;
	/**
	 * NUM_FOUR : 数字4
	 */
	private static final int NUM_FOUR = 4;
	/**
	 * NUM_SIX : 数字6
	 */
	private static final int NUM_SIX = 6;
	/**
	 * NUM_NINE : 数字9
	 */
	private static final int NUM_NINE = 9;


	/**
	 * 传入一个excel表格，构造Helper
	 */
	public static ExcelGetter from(MultipartFile file) throws IOException {
		return new ExcelGetter(file);
	}


	/**
	 * 获取一个值
	 */
	public String getString(int rowNum, int colNum) {
		return (String) get(rowNum, colNum, RequireType.STRING);
	}


	/**
	 * 获取一个值
	 */
	public Integer getInt(int rowNum, int colNum) {
		return (Integer) get(rowNum, colNum, RequireType.INT);
	}


	/**
	 * 获取一个值
	 */
	public Date getDate(int rowNum, int colNum) {
		return (Date) get(rowNum, colNum, RequireType.DATE);
	}


	/**
	 * 获取一个值
	 */
	public Double getDouble(int rowNum, int colNum) {
		return (Double) get(rowNum, colNum, RequireType.DOUBLE);
	}


	/**
	 * 获取一个值
	 */
	public Boolean getBoolean(int rowNum, int colNum) {
		return (Boolean) get(rowNum, colNum, RequireType.BOOLEAN);
	}


	public List<String> getErrorInfos() {
		return errorInfos;
	}


	public void addErrorInfo(String errorInfo) {
		errorInfos.add(errorInfo);
	}


	/**
	 * 根据行数、列数和类型获取excel表格单元的值
	 */
	protected Object get(int rowNum, int colNum, RequireType requireType) {
		String sheetName = workbook.getSheetAt(currentSheetNum).getSheetName();
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
					logger.error("无法把坐标为(" + currentSheetNum + "," + (rowNum + 1) + "," + (colNum + 1) + ")的布尔值转成日期");
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
					if (rowNum == NUM_SIX && colNum == NUM_SIX) {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 5 列的值，该单元格可能为空");
					} else if ((rowNum == NUM_SIX && colNum == NUM_NINE)) {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 7 列的值，该单元格可能为空");
					} else if (rowNum == NUM_TWO || rowNum == NUM_THREE || rowNum == NUM_FOUR) {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + colNum + " 列的值，该单元格可能为空");
					} else {
						errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的值，该单元格可能为空");
					}
					return "";
				case INT:
					return 0;
				case DATE:
					logger.error("无法把坐标为(" + currentSheetNum + "," + (rowNum + 1) + "," + (colNum + 1) + ")的布尔值转成日期");
					errorInfos.add("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 输入正确的日期格式，如 2019/1/13");
					return null;
				case DOUBLE:
					return 0.0d;
				default:
					return null;
				}
			}
		} catch (NullPointerException e) {
			logger.error("无法获取表格" + sheetName + "第 " + (rowNum + 1) + "行,第" + (colNum + 1) + "列的值，该单元格可能为空");
			if (rowNum != 0 && colNum != 0) {
				errorInfos.add("无法获取表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的值，该单元格可能为空");
			}
			return null;
		} catch (NumberFormatException e) {
			logger.error("无法把表格 " + sheetName + "第 " + (rowNum + 1) + "行,第" + (colNum + 1) + "列的数值转成字符串");
			errorInfos.add("无法把表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 列的数值转成文本形式，请输入数字");
			return null;
		} catch (ParseException e) {
			logger.error("无法把表格 " + sheetName + "第 " + (rowNum + 1) + "行,第" + (colNum + 1) + "列的字符串转成日期");
			errorInfos.add("请在表格 " + sheetName + " 第 " + (rowNum + 1) + " 行,第 " + (colNum + 1) + " 输入正确的日期格式，如 2019/1/13");
			return null;
		}
	}


	/**
	 * 根据文件格式创建workbook
	 */
	private ExcelGetter(MultipartFile file) throws IOException {
		// 判断格式
		if (file.getOriginalFilename().endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else {
			workbook = new HSSFWorkbook(file.getInputStream());
		}
		init();
	}
}
