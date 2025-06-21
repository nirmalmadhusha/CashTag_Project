package com.s23010388.cashtag.utility;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textFinder {
    public static String findDate(List<String> rawList){
        String date = "";
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        for (int i=0; i < rawList.size();i++){
            String word = rawList.get(i).toLowerCase();
            if (word.contains("date")){
                String currentWord = rawList.get(i);
                Matcher matcher1 = pattern.matcher(currentWord);
                if (matcher1.find()){
                    date = matcher1.group();
                    break;
                }
                if (i + 1 < rawList.size()) {
                    String nextWord = rawList.get(i + 1);
                    Matcher matcher2 = pattern.matcher(nextWord);
                    if (matcher2.find()) {
                        date = matcher2.group();  // This will extract only the "yyyy-MM-dd" part
                        break;
                    }
                }
            }
        }
        return date;
    }

    public static String findTotal(List<String> rawList){
        String total = "";
        for (int i=0; i < rawList.size();i++){
            String word = rawList.get(i).toLowerCase();
            if (word.contains("total")){
                total = rawList.get(i+1);
                break;

            }
        }
        return total;
    }
}
