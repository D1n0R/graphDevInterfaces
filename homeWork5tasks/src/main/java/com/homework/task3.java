package com.homework;

public class task3 {
    private static String funcStrings(String strPref, String strSuf){
        if(strSuf.isEmpty())
            return strPref;

        String newStrPref;
        if(strSuf.charAt(0) == '#'){
            if(!strPref.isEmpty())
                newStrPref = strPref.substring(0, strPref.length()-1);
            else
                newStrPref = "";
        }
        else{
            newStrPref = strPref + strSuf.charAt(0);
        }
        return funcStrings(newStrPref, strSuf.substring(1));
    }

    public static boolean compareStrings(String string1, String string2){
        return funcStrings("",string1).equals(funcStrings("", string2));
    }
}
