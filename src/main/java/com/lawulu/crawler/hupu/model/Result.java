package com.lawulu.crawler.hupu.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@TableName("hupu_result")
public class Result {
    String year;
    String dateTime;
    String weekday;
    String round;
    String homeTeam;
    String score;
    Integer homeScore;
    Integer awayScore;
    String awayTeam;
    String season;
    String league;

    @TableField(exist = false)
    String href;
    @TableId
    String matchId;

    Integer roundNum;


}
