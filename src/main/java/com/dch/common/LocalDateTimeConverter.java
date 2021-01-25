package com.dch.common;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * 关于日期的处理
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime> {


    @Override
    public Class<LocalDateTime> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                           GlobalConfiguration globalConfiguration) {
        if (CellDataTypeEnum.NUMBER.equals(cellData.getType())) {
            return LocalDateTime.of(1900, 1, 1, 0, 0, 0).plusDays(cellData.getNumberValue().intValue() - 2);
        } else if (CellDataTypeEnum.STRING.equals(cellData.getType())) {
            String stringValue = cellData.getStringValue();
            stringValue = stringValue.replaceAll("-","/"); // 替换可能存在的 年-月-日、 日-月-年的情况
            String pattern = "^[0-9]{4}/(0?[1-9]|1[0-2])/((0?[1-9])|((1|2)[0-9])|30|31)$";//文本日期 年/月/日
            String pattern1 = "^((0?[1-9])|((1|2)[0-9])|30|31)/(0?[1-9]|1[0-2])/[0-9]{4}$";//文本日期 日/月/年
            if (Pattern.matches(pattern, stringValue)) {
                String[] split = stringValue.split("/");
                String M = String.join("", Collections.nCopies(split[1].length(), "M"));
                String d = String.join("", Collections.nCopies(split[2].length(), "d"));
                String formatStr = String.format("yyyy/%s/%s HH:mm:ss", M, d);
                return LocalDateTime.parse(stringValue + " 00:00:00", DateTimeFormatter.ofPattern(formatStr));
            } else if (Pattern.matches(pattern1, stringValue)) {
                String[] split = stringValue.split("/");
                String d = String.join("", Collections.nCopies(split[0].length(), "d"));
                String M = String.join("", Collections.nCopies(split[1].length(), "M"));
                String formatStr = String.format("%s/%s/yyyy HH:mm:ss", d, M);
                return LocalDateTime.parse(stringValue + " 00:00:00", DateTimeFormatter.ofPattern(formatStr));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CellData<String> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new CellData<>(value.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    }

}
