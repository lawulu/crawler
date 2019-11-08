package com.lawulu.crawler.hupu;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.lawulu.crawler.hupu.model.Result;
import com.lawulu.crawler.model.Const;
import com.lawulu.crawler.util.JsonUtil;
import net.minidev.json.JSONUtil;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HupuData {


    static OkHttpClient client = new OkHttpClient.Builder().     // addInterceptor(new GzipRequestInterceptor()).
            build();
    static Map<String, String> headers = Maps.newHashMap();

    static String fileDir = "/Users/inza9hi/tmp/hupu/";
    static {
        headers.put("authority","soccer.hupu.com");
        headers.put("accept","*/*");
        headers.put("origin","https://soccer.hupu.com");
        headers.put("x-requested-with","XMLHttpRequest");
        headers.put("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
        headers.put("content-type","application/x-www-form-urlencoded");
        headers.put("sec-fetch-site","same-origin");
        headers.put("sec-fetch-mode","cors");
        headers.put("referer","https://soccer.hupu.com/schedule/Italy.html");
//        headers.put("accept-encoding","gzip, deflate, br");
        //https://www.jianshu.com/p/a9d861732445
        headers.put("accept-language","en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        headers.put("cookie","_cnzz_CV30020080=buzi_cookie%7Cfb8781a2.8b23.b831.4412.62f4c7e6c010%7C-1; _dacevid3=fb8781a2.8b23.b831.4412.62f4c7e6c010; __gads=ID=d8ef64152c2bbd9f:T=1532661407:S=ALNI_Mb12nSRS2UucawKoiplaEda_tc-fg; _HUPUSSOID=ab7beeb9-1e2e-46f8-acfe-e469529c76cc; ADHOC_MEMBERSHIP_CLIENT_ID1.0=ce817959-d0ff-191f-955a-c01df8c2e44b; AUM=dgR-CbdMnRYJ957Fiz0xiS6XjN5nt-yqlWtWpTno5j7lw; __dacevid3=0x7f9c90ff1e361cd8; _ga=GA1.2.1229286429.1550592816; Hm_lvt_3d37bd93521c56eba4cc11e2353632e2=1565099264,1566590100; _CLT=b0c2a05996d8b48b354e1fa4ddfc1fef; u=41229758|anV2ZWdvbA==|b535|d8a98b78aa31b82fdc9b66e5eea48a61|aa31b82fdc9b66e5|aHVwdV85OGI3NmJkNzUyZTlmMWNh; us=c24f8252a0b6448f3445a185c9771dac0d3b9b5a8ae0ce6fcc74167603efcc776164d8b64b66541c71dc2d13009350f5789c241403e8f460a524d5ec9ca1e817; new_soccer=1; Hm_lvt_0d446728dc701d4fb7e09b03d0d813f3=1570297814,1570956959,1571632460,1572486939; ua=26662572; shihuo_target_common_go_go=1; __dacemvst=59ee2e4e.1933646c; csrfToken=eXvvmI6BjtcO7Ll7Niv6yqae; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221663ad994efd1-0ae3d8f766ee66-346a7809-1296000-1663ad994f0108%22%2C%22%24device_id%22%3A%221663ad994efd1-0ae3d8f766ee66-346a7809-1296000-1663ad994f0108%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; __dacevst=03a6f71b.6646c7de|1573097794889");

    }


    public static void main(String[] args) throws IOException, InterruptedException {


        DateTime dateTime = new DateTime(2018,8,1,0,0,0,0);
        DateTime today = new DateTime();
        while (dateTime.isBefore(today)){
            crawlerAndSave(dateTime);
            dateTime = dateTime.plusMonths(1);
            Thread.sleep(10*1000);

        }
//        crawlerAndSave(new DateTime());

//        final Result build = Result.builder().homeScore(10).build();
//
//        System.out.println(JsonUtil.writeAsString(build));

//        data = {
//                'd': '20180901',
//                'type': 'm',
//                'league_id': '4'
//}


//        System.out.println(response.body().string());
//        final GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
//    ByteSource byteSource = new ByteSource() {
//    public InputStream openStream() throws IOException {
//    return gzipInputStream;
//    }
//    };
//
//    String text = byteSource.asCharSource(Charsets.UTF_8).read();
//        System.out.println(text);

//        String href = "https://g.hupu.com/soccer/report_48362637.html";
//        final String id = href.replace("https://g.hupu.com/soccer/report_", "").replace(".html", "");
//
//        System.out.println(id);

    }


    public static void crawlerAndSave(DateTime dateTime) throws IOException {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

        final String dateStr = fmt.print(dateTime);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("d", dateStr)
                .addFormDataPart("type", "m")
                .addFormDataPart("league_id", "4")
                .build();

        Request request = new Request.Builder()
                .url("https://soccer.hupu.com/schedule/schedule.server.php")
                .headers(Headers.of(headers))
                .post(requestBody)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();

        Document doc = Jsoup.parse(response.body().string());
        final List<List<Result>> parse = parse(dateTime,doc);
        final List<String> list = parse.stream().flatMap(List::stream).map(x->JsonUtil.writeAsString(x)).collect(Collectors.toList());


        final File newFile = new File(fileDir+dateStr+".json");
        final CharSink charSink = Files.asCharSink(newFile, Charsets.UTF_8);

        charSink.writeLines(list);




    }

    public static List<List<Result>> parse(DateTime dateTime,Document document){
        Elements tables = document.select("table"); //select the first table.
        List<List<Result>> resultList = Lists.newArrayList();

        for (Element table : tables) {
            final List<Result> results = parseTable(dateTime,table);
            resultList.add(results);
        }
        return resultList;

    }

    public static List<Result> parseTable(DateTime dateTime,Element table){
        final Elements trs = table.select("tr");
        List<Result> results = Lists.newArrayList();
        for (Element element : trs) {
            if(element.attr("style").equals("display:none;")){
                continue;
            }
            final Elements tds = element.select("td");
            int index =0;
            final Result.ResultBuilder builder = Result.builder();
            builder.year(""+dateTime.getYear());
            for (Element td : tds) {

                if(td.className().equals("bifen")){
                    final String href = td.select("a").first().attr("href");
                    final String id = href.replace("https://g.hupu.com/soccer/report_", "").replace("https://g.hupu.com/soccer/preview_", "").replace(".html", "");
                    builder.href(href);
                    builder.matchId(id);

                }
                if(td.attr("align").equals("center")){
                    continue;
                }
//                System.out.println(td.text());
                if(index==0){
                    builder.dateTime(td.text());
                }else if(index==1){
                    builder.weekday(td.text());
                }else if(index==2){
                    builder.round(td.text());
                }else if(index==3){
                    builder.homeTeam(td.text());
                }else if(index==4){
                    final String score = td.text().replace(" ", "");
                    builder.score(score);
                    final String[] split = score.split("-");
                    if(split.length==2){
                        builder.homeScore(Integer.parseInt(split[0]));
                        builder.awayScore(Integer.parseInt(split[1]));
                    }

                }else if(index==5){
                    builder.awayTeam(td.text());
                }
                index++;

            }

            results.add(builder.build());
        }

        return results;

    }
}
