package com.lawulu.crawler.hupu.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
@Data
@TableName("hupu_event")
public class Event {
    @TableId
    Long eventId;
    String matchId;
    String time;
    String player1;
    String player2;
    String type1;
    String type2;
    String team;


}
