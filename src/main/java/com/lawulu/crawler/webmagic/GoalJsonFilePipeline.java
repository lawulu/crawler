package com.lawulu.crawler.webmagic;

import com.alibaba.fastjson.JSON;
import com.lawulu.crawler.model.Const;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GoalJsonFilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public GoalJsonFilePipeline(String path) {
        setPath(path);
    }


    @Override
    public void process(ResultItems resultItems, Task task) {
        String date = resultItems.get(Const.DATE);
        String competition = resultItems.get(Const.COMPETITION);
        String match = resultItems.get(Const.MATCH);
        String fileName = (date == null || competition ==null || match == null)?
                DigestUtils.md5Hex(resultItems.getRequest().getUrl()):
                date + PATH_SEPERATOR + competition + PATH_SEPERATOR +match ;

        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR +fileName
                ;
        logger.info("fileName for "+resultItems.getRequest().getUrl() +" is "+fileName);
        try {
//            String fileName = resultItems.get(Const.DATE) + "_" + resultItems.get(Const.COMPETITION);
//            if(fileName.contains("null")){
//                fileName =
//            }
//            String url = resultItems.getRequest().getUrl().
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path  + ".json")));
            printWriter.write(JSON.toJSONString(resultItems.getAll()));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}