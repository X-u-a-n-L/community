package com.community.community.schedule;

import com.community.community.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class HotTagCache {
    private Map<String, Integer> tags = new HashMap<>();

    private List<String> hots = new ArrayList<>();

    /*
    运用小顶堆来实现topN算法
    构建按照priority的小顶堆，然后维持里边的元素只有3个，最后一个个poll出来加在list的头部，就是从大到小了。这样找出了topN
     */
    public void updateTags(Map<String, Integer> tags) {
        int max = 3;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max, (a, b) -> (a.getPriority() - b.getPriority()));

        tags.forEach((name, priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size() < max) {
                priorityQueue.add(hotTagDTO);
            }
            else {
                HotTagDTO minHot = priorityQueue.peek();
                if (minHot.getPriority() < priority) {
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });
        List<String> sortedTags = new ArrayList<>();//为防止schedule运行时的累加，新建一个list

        HotTagDTO poll = priorityQueue.poll();
        while (poll != null) {
            sortedTags.add(0, poll.getName()); //从小到大从priorityQueue中拿，从左边插入到list，就是从大到小顺序
            poll = priorityQueue.poll();
        }
        hots = sortedTags;
        System.out.println(hots);
    }
}
