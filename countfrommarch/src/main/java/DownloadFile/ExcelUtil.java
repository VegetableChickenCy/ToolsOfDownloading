package DownloadFile;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description: 导出excel工具类
 * @author: caoyang
 * @create: 2019\4\15 0015.
 */
public class ExcelUtil {

    private final static String excel2003L = ".xls";    //2003- 版本的excel
    private final static String excel2007U = ".xlsx";   //2007+ 版本的excel

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param wb        HSSFWorkbook对象
     * @return
     */
    protected static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, Map<String,List<String>> mapList, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        //设置自动换行
        style.setWrapText(true);
        //声明列对象
        HSSFCell cell = null;
        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            //设置样式
            cell.setCellStyle(style);
            //根据题目长度设置列宽
            sheet.setColumnWidth(i, 30 * 256);
        }

        Iterator<Map.Entry<String, List<String>>> iterator = mapList.entrySet().iterator();
        int i = 0;
        //创建内容
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();
            List<String> strings = next.getValue();
            row = sheet.createRow(i + 1);
            //将内容按顺序赋给对应的列对象
            HSSFCell cellContent = row.createCell(0);
            cellContent.setCellValue(strings.get(0));
            cellContent.setCellStyle(style);
            HSSFCell cellContent2 = row.createCell(1);
            cellContent2.setCellValue(strings.get(1));
            //设置样式
            cellContent2.setCellStyle(style);
            HSSFCell cellContent3 = row.createCell(2);
            if (strings.size() == 3){
                cellContent3.setCellValue(strings.get(2));
            } else {
                cellContent3.setCellValue("failed");
            }
            //设置样式
            cellContent3.setCellStyle(style);
            i++;
        }

/*        for (int i = 0; i < listList.size(); i++) {
          List<String> strings = listList.get(i);
          row = sheet.createRow(i + 1);
                //将内容按顺序赋给对应的列对象
                HSSFCell cellContent = row.createCell(0);
                cellContent.setCellValue(strings.get(0));
                cellContent.setCellStyle(style);
          HSSFCell cellContent2 = row.createCell(1);
          cellContent2.setCellValue(strings.get(1));
                //设置样式
                cellContent2.setCellStyle(style);
        }*/
        return wb;
    }

    /**
     * 设置头信息
     *
     * @param response
     * @param fileName
     */
    public static void setResponseHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        // 针对IE或者以IE为内核的浏览器：
        try {
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        //  客户端不缓存 
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");

    }

    /**
     * 主体方法
     *
     * @param fileName
     * @return HSSFWorkbook
     */
    public static HSSFWorkbook setContent(Map<String,List<String>> mapList, String fileName) {
        int i = 0;
      //初始化标题
        String[] title = new String[3];
        title[0] = "application_id";
        title[1] = "agent code";
        title[2] = "page number";
        //创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(fileName, title, mapList, null);
        return wb;
    }


}
