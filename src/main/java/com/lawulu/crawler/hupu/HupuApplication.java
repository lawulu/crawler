package com.lawulu.crawler.hupu;


import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.lawulu.crawler.hupu.mapper.EventMapper;
import com.lawulu.crawler.hupu.mapper.ResultMapper;
import com.lawulu.crawler.hupu.model.Event;
import com.lawulu.crawler.hupu.model.Result;
import com.lawulu.crawler.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


@Slf4j
@SpringBootApplication
@MapperScan("com.lawulu.crawler.hupu.mapper")
public class HupuApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HupuApplication.class, args);
    }

    @Autowired
    private EventMapper eventMapper;


    @Autowired
    private ResultMapper resultMapper;

    @Override
//    @SuppressWarnings("squid:S106")
    public void run(String... args) throws Exception {


        handleResult();


    }

     void handleResult() throws Exception{
         File file = new File(HupuData.fileDir);
         final File[] list = file.listFiles();
         for (File json : list) {
             final CharSource charSource = Files.asCharSource(json, Charsets.UTF_8);
             final ImmutableList<String> strings = charSource.readLines();
             for (String string : strings) {
                 final Result result = JsonUtil.readAs(string, Result.class);
                 if(result.getLeague()==null){
                     result.setLeague("SA");
                 }
                 if(result.getSeason()==null){
                     result.setSeason(result.getYear());
                     if(result.getDateTime().compareTo("08-01 02:45")<0){
                         result.setSeason(""+(Integer.parseInt(result.getYear())-1));
                     }
                 }

                 if(result.getRoundNum()==null && StringUtils.isNotEmpty(result.getRound())){
                     String r = result.getRound().replace("第","").replace("轮","");
                     result.setRoundNum(Integer.parseInt(r));
                 }
                 if(resultMapper.selectById(result.getMatchId())==null){
                     resultMapper.insert(result);

                 }else{
                     resultMapper.updateById(result);
                     log.info("duplicated:" + result);
                 }
             }
         }
     }

     void handleEvent() throws Exception{
         File file = new File(HupuEvent.fileDir);
         final File[] list = file.listFiles();
         for (File json : list) {
             final CharSource charSource = Files.asCharSource(json, Charsets.UTF_8);
             final ImmutableList<String> strings = charSource.readLines();
             int index=10;
             for (String string : strings) {
                 final Event event = JsonUtil.readAs(string, Event.class);
                 event.setMatchId(json.getName().replace(".json",""));
                 event.setEventId(Long.parseLong(event.getMatchId())*100+index);
                 index= index +1;
                 System.out.println(event);
                 if(eventMapper.selectById(event.getEventId())==null){
                     eventMapper.insert(event);

                 }else{
                     log.info("duplicated:" + event);
                 }
             }
         }
     }

}