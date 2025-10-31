package com.homework;

import java.util.*;

public class task2 {
    public static List<Integer> calcChildWidths(int parentWidth, List<Integer> childSpecs) {
        int sum = 0, negSpecCount = 0;
        for(int spec : childSpecs){
            if(spec > 0)
                sum += spec;
            else{
                negSpecCount += spec * -1;
            }
        }
        if(sum > parentWidth){
            System.out.println("Некорректные данные!");
            return null;
        }
        for(int i = 0; i < childSpecs.size(); i++){
            if(childSpecs.get(i) <= 0){
                childSpecs.set(i, (parentWidth - sum) / negSpecCount * childSpecs.get(i) * -1);
            }
        }
        return childSpecs;
    }
}
