package com.lawulu.crawler.hupu;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.lawulu.crawler.hupu.model.Event;
import com.lawulu.crawler.hupu.model.Result;
import com.lawulu.crawler.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class HupuEvent {

    static OkHttpClient client = new OkHttpClient.Builder().     // addInterceptor(new GzipRequestInterceptor()).
            build();
    static Map<String, String> headers = Maps.newHashMap();

    static String fileDir = "/Users/inza9hi/tmp/event/";

    static {
        headers.put("authority","m.hupu.com");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
        headers.put("sec-fetch-user","?1");
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.put("sec-fetch-site","none");
        headers.put("sec-fetch-mode","navigate");
//        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        headers.put("cookie","_dacevid3=fb8781a2.8b23.b831.4412.62f4c7e6c010; __gads=ID=d8ef64152c2bbd9f:T=1532661407:S=ALNI_Mb12nSRS2UucawKoiplaEda_tc-fg; _HUPUSSOID=ab7beeb9-1e2e-46f8-acfe-e469529c76cc; AUM=dgR-CbdMnRYJ957Fiz0xiS6XjN5nt-yqlWtWpTno5j7lw; __dacevid3=0x7f9c90ff1e361cd8; _ga=GA1.2.1229286429.1550592816; Hm_lvt_3d37bd93521c56eba4cc11e2353632e2=1565099264,1566590100; _CLT=b0c2a05996d8b48b354e1fa4ddfc1fef; u=41229758|anV2ZWdvbA==|b535|d8a98b78aa31b82fdc9b66e5eea48a61|aa31b82fdc9b66e5|aHVwdV85OGI3NmJkNzUyZTlmMWNh; us=c24f8252a0b6448f3445a185c9771dac0d3b9b5a8ae0ce6fcc74167603efcc776164d8b64b66541c71dc2d13009350f5789c241403e8f460a524d5ec9ca1e817; new_soccer=1; shihuo_target_common_go_go=1; m_id=qJuXqZSazpnX1cRim2%2BZlJprZ5GVwpOTmWRmaw%3D%3D; app_cookie=1573094380; __dacemvst=59ee2e4e.1933646c; Hm_lvt_abb0c7fb3fb595c51dc99849b10830bd=1570675152,1573094381; shihuo_target_common_hupu_m=5; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221663ad994efd1-0ae3d8f766ee66-346a7809-1296000-1663ad994f0108%22%2C%22%24device_id%22%3A%221663ad994efd1-0ae3d8f766ee66-346a7809-1296000-1663ad994f0108%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; ua=26662945; __dacemvst=59ee2e4e.1933646c|1573115570895; Hm_lpvt_abb0c7fb3fb595c51dc99849b10830bd=1573113771");

    }

    public static void main(String[] args) throws Exception {
//        final Result build = Result.builder().homeTeam("ss").awayTeam("bb").build();
//        test(build);

        doit();

    }

    public static void doit() throws Exception{
//        String begin = "48362911";
//        boolean flag = true;

        File dist = new File((fileDir));
        Set<String> downloaded = Sets.newHashSet();
        for (String s : dist.list()) {
            final String matchId = s.replace(".json", "");
            downloaded.add(matchId);

        }

        Map<String,Result> all = Maps.newHashMap();
        File file = new File(HupuData.fileDir);
        Queue<String> toBeDone = new LinkedList<String>();
        final File[] list = file.listFiles();
        for (File json : list) {
            final CharSource charSource = Files.asCharSource(json, Charsets.UTF_8);
            final ImmutableList<String> strings = charSource.readLines();
            for (String string : strings) {
                final Result result = JsonUtil.readAs(string, Result.class);
                if(result.getHomeScore()==null){
                    continue;
                }
                if(downloaded.contains(result.getMatchId())){
                    continue;
                }else{
                    if(all.containsKey(result.getMatchId())){
                        log.warn("has added "+json.getName());
                    }else{
                        all.put(result.getMatchId(),result);
                        toBeDone.add(result.getMatchId());
                    }

                }

//                if(!flag) {
//                    if (begin.equals(result.getMatchId())) {
//                        flag = true;
//                    } else {
//                        log.info("ignore to handle " + result.getMatchId() + " " + result.getHomeTeam() + ": " + result.getAwayTeam() + " " + result.getScore());
//
//                    }
//                }

//                if(flag){
//                    log.info("begin to handle "+result.getMatchId() + " "+ result.getHomeTeam() + ": "+result.getAwayTeam() +" " + result.getScore());
//                    try{
//                        crawlerAndSave(result);
//
//                    }catch (Exception e){
//                        log.error("s1",e);
//                        try{
//                            TimeUnit.SECONDS.sleep(30);
//
//                            crawlerAndSave(result);
//
//                        }catch (Exception e2){
//                            log.error("s2",e2);
//                            try{
//                                TimeUnit.SECONDS.sleep(30);
//
//                                crawlerAndSave(result);
//
//                            }catch (Exception e3){
//                                log.error("s3",e3);
//                            }
//                        }
//                    }
//                    TimeUnit.SECONDS.sleep(30);
//                }



            }
        }

        log.info("toBeDone"+toBeDone.size());
        log.info("all"+all.size());
        log.info("downloaded"+downloaded.size());

        while(true){
            String id = toBeDone.poll();
            if(id!=null){
                final Result result = all.get(id);

                try{
                    log.info("begin to handle "+result.getMatchId() + " "+ result.getHomeTeam() + ": "+result.getAwayTeam() +" " + result.getScore());

                    crawlerAndSave(result);

                }catch (Exception e3){
                    toBeDone.add(result.getMatchId());
                    log.error("ss"+result.getMatchId(),e3);
                }
            }else {
                break;
            }
            TimeUnit.SECONDS.sleep(10);

        }
    }

    public static void crawlerAndSave(Result result) throws IOException {

        List<Event> list = Lists.newArrayList();


        Request request = new Request.Builder()
                .url("https://m.hupu.com/soccer/games/event/"+result.getMatchId())
                .headers(Headers.of(headers))
                .get()
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();

        Document doc = Jsoup.parse(response.body().string());


        final Element events = doc.select("section.match-soccer-event>ul").first();
        final Elements lis = events.select("li");

        for (Element li : lis) {
//            System.out.println(li.html());
//            System.out.println(li.text());

            final Event.EventBuilder builder = parse(result, li);

            if(builder!=null){
                list.add(builder.build());

            }



        }

        final File newFile = new File(fileDir+result.getMatchId()+".json");
        final CharSink charSink = Files.asCharSink(newFile, Charsets.UTF_8);
        final List<String> stringList = list.stream().map(x -> JsonUtil.writeAsString(x)).collect(Collectors.toList());

        charSink.writeLines(stringList);


    }



    public static void test(Result result) throws Exception{
        File input = new File("/Users/inza9hi/work/repo/crawler/src/main/resources/hupuevent.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        final Element events = doc.select("section.match-soccer-event>ul").first();
        final Elements lis = events.select("li");
        for (Element li : lis) {
//            System.out.println(li.html());
//            System.out.println(li.text());

            final Event.EventBuilder builder = parse(result, li);

            if(builder!=null){
                System.out.println(builder.build());

            }



        }


    }




    public static Event.EventBuilder parse(Result result, Element li){
        if(li.select("img.icon-whistle").size()!=0){
            return null;
        }
        if(li.select("span.overtime").size()!=0){
            return null;
        }

        final Event.EventBuilder builder = Event.builder();
        final Element left = li.select("span.col-left").first();
        handleOneSide(builder,left, result.getHomeTeam());
        final Element right = li.select("span.col-right").first();
        handleOneSide(builder,right, result.getAwayTeam());
        final Element time = li.select("span.surplus-time").first();
        builder.time(time.text().trim());



        return builder;


    }

    public static void handleOneSide(Event.EventBuilder builder,Element side, String team){

        final Elements icons = side.select("i");
        if(icons.isEmpty()){
            return;
        }
        final Element icon = icons.first();
        final String className = icon.className();
        String type = getTypeFromClassName(className);
        String type2 = null;
        if(type.equals("goal")){
            final String attr = icon.select("img").first().attr("src");
            if(attr.contains("icon-oolong")){
                type2="oolong";
            }
            if(className.contains("icon-thepenalty")){
                type2="penalty";
            }
        }


        final Elements teamNames = side.select("span.team-name");




        if(teamNames.size()!=0){
            final Element teamName = teamNames.first();

            String player1 = teamName.text().trim();

            if(type.equals("substitution")){

                final String player2 = teamName.select("span.gray").first().text();
                player1 = player1.replace(player2,"").trim();
                builder.player2(player2);

            }
            builder.player1(player1);
            builder.team(team);


            builder.type1(type);
            if(type2==null){
                type2=type;
            }
            builder.type2(type2);

        }
    }

    public static String getTypeFromClassName(String name) {
        if(name.contains("icon-yellow-card")){
            return "yellow-card";
        }else if(name.contains("icon-ball")){
            return "goal";
        } else if(name.contains("icon-substitution")){
            return "substitution";
        }else if(name.contains("icon-red-card")){
            return "red-card";
        }
//        &&name.contains("icon-thepenalty")
//        else if(name.contains("icon-ball")){
//            return "goal";
//        }else if(name.contains("icon-ball")){
//            return "goal";
//        }else if(name.contains("icon-ball")){
//            return "goal";
//        }
        return null;
    }
}
