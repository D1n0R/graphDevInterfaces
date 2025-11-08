package com.homework;

import java.util.ArrayList;
import java.util.List;

public class task1 {
    private static boolean checkSearch(String query, String contact){
        int i = 0;
        for(int j = 0; j < contact.length(); j++){
            if(contact.charAt(j) == query.charAt(i)){
                i++;
                if(i + 1== query.length()){
                    return true;
                }
            }
        }
        return i == query.length();
    }

    public static List<String> searchInContacts(String query, ArrayList<String> contacts){
        List<String> result = new ArrayList<>();
        query = query.toLowerCase();
        for(String contact : contacts){
            String cntct = contact.toLowerCase();
            if(checkSearch(query.replace(" ", ""), cntct.replace(" ", "")))
                result.add(contact);
        }
        return result;
    }
}
