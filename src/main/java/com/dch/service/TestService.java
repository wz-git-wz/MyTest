package com.dch.service;

import com.dch.entity.ImportTest;
import com.dch.entity.Test;

import java.util.List;

public interface TestService {
     List<Test> list();

    void insert(List<ImportTest> datas);

    List<Test> findLike(String name,Integer nid) throws InterruptedException;
}
