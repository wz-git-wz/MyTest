package com.dch.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * excel导出工具类
 */
public class ExcelWriteUtil {
    /**
     * @param response  响应
     * @param list      出参数据集
     * @param clazz     出参对象类型
     * @param fileName  excel 文件名称
     * @param sheetName excel Sheet页名称
     */
    public static void excelWrite(HttpServletResponse response,
                                  List<?> list,
                                  Class<?> clazz,
                                  String fileName,
                                  String sheetName) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //ExcelWriterBuilder write = EasyExcel.write(response.getOutputStream(), clazz);
        ExcelWriterBuilder write = EasyExcel.write("d:/ele.xlsx", clazz);
        write.registerConverter(new LocalDateTimeConverter())
                //根据内容自适应宽度
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                //标题提示处理
                .registerWriteHandler(new ExcelTitleHandler(clazz))
                .sheet(sheetName + "1").doWrite(list);
    }
}
