package com.jimi.smt.eps.ghost.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSON;
import com.jimi.smt.eps.ghost.entity.PartUsage;
import com.jimi.smt.eps.ghost.entity.PartUsageItem;
import com.jimi.smt.eps.ghost.entity.PartUsageReport;

import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.dautils.api.UuidUtil;


/**
 * 富士PartUsage报告解析器
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class FujiPartUsageReportParser {
	
	private List<PartUsageReport> reports = new ArrayList<PartUsageReport>();
	
	private Date globalStartTime;//整个页面的查询开始时间
	
	private Date globalEndTime;//整个页面的查询结束时间
	

	public FujiPartUsageReportParser(String source) throws Exception {
		//生成文档
		Document doc = Jsoup.parse(source);
		//设置整个页面的查询时间
		setGlobalTime(doc);
		//设置页面数据
		setData(doc);
	}


	private void setGlobalTime(Document doc) throws ParseException {
		Element baseInfoTbody = doc.getElementsByTag("tbody").get(0);//获取第一个tbody元素
		String globalStartTimeString = baseInfoTbody.getElementsByTag("tr").get(2).getElementsByTag("td").get(1).text();//获取第3行第2列的文本
		String globalEndTimeString = baseInfoTbody.getElementsByTag("tr").get(3).getElementsByTag("td").get(1).text();//获取第4行第2列的文本
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		globalStartTime = simpleDateFormat.parse(globalStartTimeString);
		globalEndTime = simpleDateFormat.parse(globalEndTimeString);
	}


	private void setData(Document doc) throws ParseException {
		PartUsageReport report = null;
		Element detailsTbody = doc.getElementsByTag("tbody").get(1);//获取第二个tbody元素
		//行解析
		for(Element trow : detailsTbody.children()) {
			PartUsageItem item = new PartUsageItem();
			//列解析
			for(int i = 0 ; i < trow.children().size() ; i++) {
				Element tdata = trow.children().get(i);
				//判断是否是“Machine：”字眼
				if(i == 0 && tdata.text().equals("Machine :")) {
					//创建PartUsage
					String machineName = tdata.nextElementSibling().text();
					PartUsage partUsage = new PartUsage();
					partUsage.setStart(globalStartTime);
					partUsage.setEnd(globalEndTime);
					partUsage.setMachine(machineName);
					partUsage.setId(FieldUtil.md5(partUsage));
					report = new PartUsageReport();
					report.setBaseInfo(partUsage);
					break;//直接跳到开始解析下一行（后面全是空格）
				}
				//判断是否是“Machine Total”字眼
				if(i == 0 && tdata.text().equals("Machine Total")) {
					//把PartUsageReport添加到最终List
					reports.add(report);
					break;//直接跳到开始解析下一行（后面全是统计数据）
				}
				//解析并填充表格内容
				switch (i) {
					case 2:
						item.setPartnumber(tdata.text());
						break;
					case 6:
						item.setPickupcount(Integer.parseInt(tdata.text().replaceAll(",", "")));
						break;
					case 7:
						item.setTotalpartsused(Integer.parseInt(tdata.text().replaceAll(",", "")));
						break;
					case 8:
						item.setRejectparts(!tdata.text().equals("") ? Integer.parseInt(tdata.text().replaceAll(",", "")) : 0);
						break;
					case 9:
						item.setNopickup(!tdata.text().equals("") ? Integer.parseInt(tdata.text().replaceAll(",", "")) : 0);
						break;
					case 10:
						item.setErrorparts(!tdata.text().equals("") ? Integer.parseInt(tdata.text().replaceAll(",", "")) : 0);
						break;
					case 11:
						item.setDislodgedparts(!tdata.text().equals("") ? Integer.parseInt(tdata.text().replaceAll(",", "")) : 0);
						break;
					case 12:
						item.setRescancount(!tdata.text().equals("") ? Integer.parseInt(tdata.text().replaceAll(",", "")) : 0);
						break;
					case 13:
						item.setPickuprate(formatToFloat(tdata.text()));
						break;
					case 14:
						item.setRejectrate(formatToFloat(tdata.text()));
						break;
					case 15:
						item.setErrorrate(formatToFloat(tdata.text()));
						break;
					case 16:
						item.setDislodgedrate(formatToFloat(tdata.text()));
						break;
					case 17:
						item.setSuccessrate(formatToFloat(tdata.text()));
						//把Item与baseInfo进行关联
						item.setId(UuidUtil.get32UUID());
						item.setBaseinfo(report.getBaseInfo().getId());
						//把Item放入report里
						report.getDetails().add(item);
						break;
					default:
						break;
				}
			}
		}
	}

	
	/**
	 * 百分比字符串转浮点型，支持类型类似“.00023%”、“102.54%”；
	 * @param in
	 * @return
	 */
	private Float formatToFloat(String in) {
		StringBuffer sb = new StringBuffer(in);
		if(sb.charAt(0) == '.') {
			sb.insert(0, '0');
		}
		sb.deleteCharAt(sb.length() - 1);
		float f = Float.parseFloat(sb.toString());
		f /= 100;
		return f;
	}
	

	/**
	 * 获取结果的json格式文本
	 * @return
	 */
	public String getJSON() {
		return JSON.toJSONString(reports);
	}
	
	
//	public static void main(String[] args) throws Exception {
//		String testSource = ClipBoardUtil.getClipboardString();//注意：此处将从系统粘贴板获取数据，内容来自文件：test-allLine-allMachie-allRecipe-report.html
//		FujiPartUsageReportParser parser = new FujiPartUsageReportParser(testSource);
//		System.out.println(parser.getJSON());
//	}
	
}
