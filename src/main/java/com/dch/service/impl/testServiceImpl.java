package com.dch.service.impl;

import com.dch.entity.ImportTest;
import com.dch.entity.Test;
import com.dch.mapper.Mapperlist;
import com.dch.service.TestService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class testServiceImpl implements TestService {

    @Resource
    Mapperlist mapperlist;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public List<Test> list() {
        return mapperlist.list();
    }

    @Override
    @Transactional
    public void insert(List<ImportTest> datas) {
        Map<String, List<ImportTest>> collect = datas.stream().collect(Collectors.groupingBy(ImportTest::getName));
        try {
            for(String test:collect.keySet()){
                ImportTest importTest = collect.get(test).get(0);
                mapperlist.insert(importTest);
            }
        }catch (Exception e){
            throw new RuntimeException("导入失败，请检查入参");
        }


    }

    @Override
    public List<Test> findLike(String name,Integer nid) {
        List<Test> tests=null;
        RLock  testLock = redissonClient.getLock("TestLock");
        try {
            boolean lock = testLock.tryLock(0, 5, TimeUnit.MINUTES);
            if(!lock){//判断
                throw  new RuntimeException("获取锁失败"+",名字是："+name);
            }
            tests = mapperlist.listList(name);
            //Thread.sleep(2000);
        }catch (Exception e){//抛出异常
             throw  new RuntimeException("查询失败"+",名字是："+name);
        }finally {
            //释放锁,isHeldByCurrentThread()是先查询当前线程是否保持此锁定
            if(testLock.isHeldByCurrentThread()){
                testLock.unlock();
            }
        }

        return  tests;
    }
}
