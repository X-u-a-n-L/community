package com.community.community.schedule;

import com.community.community.Mapper.QuestionMapper;
import com.community.community.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagsTasks {
    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private HotTagCache hotTagCache;

     /*
       每隔一段时间进行一次各个问题权重的重新计算
       遍历每个问题，把tags得到，用split得到每个小tag
       然后放入priority的map中， 每个tag的出现priority+5，再加上这个问题的回复数，就是最终的priority（算法可变）
      */
     /*
     Map放的是tag和它的priority
      */
    @Scheduled(fixedRate = 5000)
    //@Scheduled(cron = "0 0 1 * * *") //表示每天凌晨1点
    public void hotTagSchedule() {
        int offset = 0;
        int limit = 5;

        List<Question> list = new ArrayList<>();
        Map<String, Integer> priorities = new HashMap<>();
        while (offset == 0 || list.size() == limit) {
            list = questionMapper.list(offset, limit);
            for (Question question : list) {
                String[] tags = StringUtils.split(question.getTag(), ",");

                for (String tag : tags) {
                    Integer priority = priorities.get(tag);
                    if (priority != null) {
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    }
                    else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
        hotTagCache.setTags(priorities);
//        hotTagCache.getTags().forEach(
//                (k, v) -> {
//                    System.out.print(k);
//                    System.out.print(":");
//                    System.out.print(v);
//                    System.out.println();
//                }
//        );
        hotTagCache.updateTags(priorities);
        log.info("The time is now {}", new Date());

    }
}