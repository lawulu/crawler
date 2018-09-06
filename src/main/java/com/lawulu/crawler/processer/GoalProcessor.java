package com.lawulu.crawler.processer;

import com.lawulu.crawler.model.Const;
import com.lawulu.crawler.model.Event;
import com.lawulu.crawler.model.Result;
import com.lawulu.crawler.model.Stats;
import com.lawulu.crawler.webmagic.GoalJsonFilePipeline;
import org.apache.commons.lang3.tuple.Triple;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;

//http://www.goal.com/en/results-standings/69/italy-serie-a
//TODO 积分？？教练？？
//https://www.google.com/search?ei=BLWKW7_ZMYeu0PEP-Ler8Ao&q=serie-a+table+site%3Agoal.com
public class GoalProcessor implements PageProcessor {

    final static Logger logger = LoggerFactory.getLogger(GoalProcessor.class);


    private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(5).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");


    @Override
    public void process(Page page) {

        String extra = page.getRequest().getExtra(Const.PAGE_TYPE).toString();
        if(Const.DETAIL.equals(extra)){
            Triple<Result, List<Event>, Stats> resultListStatsTriple = handleDetail(page);
            page.putField(Const.RESULT,resultListStatsTriple.getLeft());
            page.putField(Const.EVENTS,resultListStatsTriple.getMiddle());
            page.putField(Const.STATS,resultListStatsTriple.getRight());
            page.putField(Const.COMPETITION,page.getRequest().getExtra(Const.COMPETITION));
            page.putField(Const.DATE,page.getRequest().getExtra(Const.DATE));
            page.putField(Const.MATCH,page.getRequest().getExtra(Const.MATCH));

        }else if(Const.LIST.equals(extra)){
            handleList(page);
            page.setSkip(true) ;

        }
  }

    @Override
    public Site getSite() {
        return site;
    }

    public static void handleList(Page page){
//        Selectable results = page.getHtml().css("div.widget-fixtures-and-results");

        logger.info("handle:" + page.getUrl());
        Document document = page.getHtml().getDocument();
        String currentUrl = document.location();
        String date =currentUrl.substring(currentUrl.lastIndexOf("/")+1);

        Elements competitions = document.select("div.competition-matches");
        for (Element competition : competitions) {
            String competitionName = competition.select("div.competition-name").text();
            logger.debug(competition.html());
            Elements matches = competition.select("div.match-row");

            if(matches.isEmpty()){
                continue;//没有比赛
            }
            for (Element match : matches) {
                Elements mainData = match.select("a.match-main-data-link");
                String href = mainData.attr("abs:href");
                String[] split = href.split("/");
                int lastIndex = href.lastIndexOf("/");
                String newUrl = href.substring(0,lastIndex)+"/commentary-result"+href.substring(lastIndex);
                Request request = new Request(newUrl);
                request.putExtra(Const.COMPETITION,competitionName);
                request.putExtra(Const.DATE,date);
                request.putExtra(Const.PAGE_TYPE,Const.DETAIL);
                request.putExtra(Const.MATCH,split[split.length-2]);
                page.addTargetRequest(request);
      }

        }
    }

    public static Triple<Result, List<Event>, Stats> handleDetail(Page page){


        Document document = page.getHtml().getDocument();
        String url = document.location();
        Result result = new Result();


        // header区域
        Elements matchHeader = document.select("div.widget-match-header");
        //metadata
        Elements metas = matchHeader.select("meta");
        for (Element meta : metas) {
            if(meta.attr("itemprop").equals("startDate")){
                String startDate = meta.attr("content");
                result.setStartDate(startDate);
            }else if(meta.attr("itemprop").equals("location")){
                String location = meta.attr("content");
                result.setLocation(location);
            }
//            else if(meta.attr("itemprop").equals("eventStatus")){
//
//            }
        }

        //主队
        Elements homeTeam = matchHeader.select("a.widget-match-header__team--home");
        String homeTeamName = homeTeam.select("span.widget-match-header__name--full").text();
        result.setHomeTeamName(homeTeamName);

        //客队
        Elements awayTeam = matchHeader.select("a.widget-match-header__team--away");
        String awayTeamName = awayTeam.select("span.widget-match-header__name--full").text();
        result.setAwayTeamName(awayTeamName);

        String score = matchHeader.select("div.widget-match-header__score > span").text();
        result.setScore(score);
        //进球从从Event中提取 所以注释掉
//        matchHeader.select("div.widget-match-header__scorers-names--home").text();

        logger.info(" "+ url +" result "+ result);
        // key events
        Elements keyEvents = document.select("div.widget-match-key-events");

        Elements events = keyEvents.select("div.event");

        List<Event> eventList = Lists.newArrayList();

        for (Element elementEvent : events) {
            Event event = new Event();


            Elements eventIcon = elementEvent.select("div.match-event-icon");
            String eventType = eventIcon.attr("class").split(" ")[1];
            if(eventType.equals("type-kick_off")){
                continue;
            }

            Triple<String, String, String> stringStringStringTriple = null;

            Elements homeEvent = elementEvent.select("div.team-home");
            if(!homeEvent.isEmpty()){
                stringStringStringTriple = handleEvent(homeEvent);

            }
            Elements awayEvent = elementEvent.select("div.team-away");
            if(!awayEvent.isEmpty()){
                stringStringStringTriple = handleEvent(awayEvent);
            }
            event.setEventType(eventType);
            if(stringStringStringTriple!=null){
                event.setEventTime(stringStringStringTriple.getLeft());
                event.setMain(stringStringStringTriple.getMiddle());
                event.setAdditional(stringStringStringTriple.getRight());
            }


            eventList.add(event);

        }

        //stats
        Elements elementStats = document.select("div.widget-match-stats");

        Stats stats = new Stats();

        String statsClass = elementStats.attr("class");

        if(!statsClass.contains("hidden")){
            Elements possession = elementStats.select("div.stats-possession");

            String attr = possession.attr("data-stat");
            if(!attr.isEmpty()){
                Double homePossession = Double.parseDouble(attr);//possession.select("span.lbl-home").text();
                stats.setHomePossession(homePossession);

                Double awayPossession = 100-homePossession ;//possession.select("span.lbl-away").text();
                stats.setAwayPossession(awayPossession);
            }


            Elements shots = elementStats.select("div.stats-shots");
            List<String> homeShotsAll = shots.select("span.value-home").eachText();
            List<String> awayShotsAll = shots.select("span.value-away").eachText();
            try {
                String homeShots = homeShotsAll.get(0);
                String awayShots = awayShotsAll.get(0);
                stats.setHomeShots(homeShots);
                stats.setAwayShots(awayShots);
                //        Elements gates = stats.select("div.gates");

                String homeGates = homeShotsAll.get(1);
                String awayGates = awayShotsAll.get(1);
                stats.setHomeGates(homeGates);
                stats.setAwayGates(awayGates);
            }catch (Exception e){
                logger.warn("not found",e);
            }

            Elements passes = elementStats.select("div.stats-passes");

            String homePasses = passes.select("span.value-home").text();
            String awayPasses = passes.select("span.value-away").text();
            stats.setHomePasses(homePasses);
            stats.setAwayPasses(awayPasses);
        }

        return Triple.of(result,eventList,stats);


    }

    private static Triple<String,String,String> handleEvent(Elements homeEvent) {
        Elements eventWrapper = homeEvent.select("div.text-wrapper");
//        Elements eventIcon = homeEvent.select("div.event-icon");
        String eventTime = eventWrapper.select("div.event-time").text();
        String main = eventWrapper.select("div.event-text-main").text();
        String additional = eventWrapper.select("div.event-text-additional").text();
        return Triple.of(eventTime,main,additional);

    }


}
