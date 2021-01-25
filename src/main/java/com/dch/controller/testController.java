package com.dch.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dch.common.ExcelListtiner;
import com.dch.common.ExcelWriteUtil;
import com.dch.common.LocalDateTimeConverter;
import com.dch.entity.ExportTest;
import com.dch.entity.ImportTest;
import com.dch.entity.Test;
import com.dch.service.TestService;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class testController {
    @Autowired
    TestService testService;

    //列表展示
    @GetMapping("/findlist")
    public List<Test> findList() {
        return testService.list();
    }

    //列表模糊查询
    @PostMapping("/findlike")
    public List<Test> findLike(@RequestParam String name,@RequestParam Integer nid) throws InterruptedException {
        return testService.findLike(name,nid);
    }
    /**
     * Excel导出
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportList")
    public void exportList(HttpServletResponse response) throws IOException {
        //列表list
        List<Test> list = testService.list();
        //实体类输出
        List<ExportTest> collect = list.stream().map(v -> {
            //new一个导出实体类list
            ExportTest exportTest = new ExportTest();
            BeanUtils.copyProperties(v, exportTest);
            return exportTest;
        }).collect(Collectors.toList());
        //输出工具类
        ExcelWriteUtil.excelWrite(response,collect,ExportTest.class,"列表导出","列表");
    }

    /**
     * Excel导入
     * @param file Excel文件
     * @throws IOException
     */
    @GetMapping("/importList")
    public void importList(@RequestBody MultipartFile file) throws IOException {
    //文件读取的监听器创建
        ExcelListtiner<ImportTest> objectExcelListtiner = new ExcelListtiner<>();
        try {
            //使用easyexcel的文件读取方法.......registerConverter日期转化器（将文件读取出来放入到objectExcelListtiner中）
            EasyExcel.read(file.getInputStream(),ImportTest.class,objectExcelListtiner).registerConverter(new LocalDateTimeConverter()).sheet(0).doRead();
        }catch (Exception e){
            throw new RuntimeException("导入读取错误");
        }
    //根据行规则取数据，将数据放到列表中
        List<ImportTest> datas = objectExcelListtiner.getDatas();
        testService.insert(datas);

    }


}
