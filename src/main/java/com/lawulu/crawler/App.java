package com.lawulu.crawler;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.lawulu.crawler.model.Const;
import com.lawulu.crawler.processer.GoalProcessor;
import com.lawulu.crawler.webmagic.GoalJsonFilePipeline;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        goalTest();
//        goal();
//        error();
    }

    static void error() throws Exception{
        URL url = Resources.getResource("nohup.out.error.error");
        List<String> strings = Resources.readLines(url, Charsets.UTF_8);

        System.out.println(strings.size());

        Pattern compile = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");
//        System.out.println( DigestUtils.sha1Hex("http://www.goal.com/en-us/results/2018-02-17"));
        List<Request> requests = Lists.newArrayList();
        for (String log : strings) {
            Matcher matcher = compile.matcher(log);
            if(matcher.find()){
                Request request = new Request(matcher.group());
                requests.add(request);

            }
        }
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config,"localhost");
        RedisScheduler redisScheduler = new RedisScheduler(pool);//How to select database?

        Spider.create(new GoalProcessor())
                .setScheduler(redisScheduler)
                .addRequest(requests.toArray(new Request[]{}))
                .addPipeline(new GoalJsonFilePipeline("/root/data/webmagic"))
                .thread(5).run();
    }

    static void goal(){
        List<Request> requests = Lists.newArrayList();

        DateTime dateTime = new DateTime(2010,8,1,0,0,0,0);
        DateTime today = new DateTime();
        while (dateTime.isBefore(today)){
            Request request = new Request("http://www.goal.com/en-us/results/"+dateTime.toString("yyyy-MM-dd"));
            request.putExtra(Const.PAGE_TYPE,Const.LIST);
            requests.add(request);
            dateTime=dateTime.plusDays(1);

        }

        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config,"localhost");
        RedisScheduler redisScheduler = new RedisScheduler(pool);//How to select database?

        Spider.create(new GoalProcessor())
                .setScheduler(redisScheduler)
                .addRequest(requests.toArray(new Request[]{}))
                .addPipeline(new GoalJsonFilePipeline("/root/data/webmagic"))
                .thread(5)
                .run();
    }

    static void goalTest(){


//        Request request = new Request("http://www.goal.com/en-us/match/fulham-v-chelsea/70nfpjlbvc8jsyvukj20q3qvp");
//        Request request = new Request("http://www.goal.com/en-us/match/chievo-v-juventus/commentary-result/2a5cko82ghpj0p5odxxxvj4ve");
//        Request request = new Request("http://www.goal.com/en-us/match/slovakia-v-croatia/commentary-result/eh448vb0lfgb50je4i57fqx1x");
        Request request = new Request("http://www.goal.com/en-us/match/real-madrid-v-basel/commentary-result/4hix990ya0eqtyahdxwep9rkl");
        request.putExtra(Const.PAGE_TYPE,Const.DETAIL);
        request.putExtra(Const.COMPETITION,"SERIE A");
        request.putExtra(Const.DATE,"2018-09-05");
        request.putExtra(Const.MATCH,"chievo-v-juventus");

        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config,"localhost");
        RedisScheduler redisScheduler = new RedisScheduler(pool);//How to select database?

        Spider.create(new GoalProcessor())
//                .setScheduler(redisScheduler)

                .addRequest(request)
                .addPipeline(new GoalJsonFilePipeline("/Users/inza9hi/tmp/webmagic/"))
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }

    }
