package com.dch.mapper;

import com.dch.entity.ImportTest;
import com.dch.entity.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Mapperlist {
     List<Test> list();

     void insert(ImportTest datas);

     @Select("select * from Test where name Like concat('%',#{name},'%')")
     List<Test> listList(String name);
}
