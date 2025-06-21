package com.s23010388.cashtag.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class textCleaner {
    public static List<String> cleanTokenList(List<String> rawWords1) {
        List<String> rawWords = new ArrayList<>();
        for (int i = 0; i < rawWords1.size(); i++) {
            String word = rawWords1.get(i).toLowerCase();

            if (word.equals("cashier")) {
                i++; // skip next word (the name)
                continue;
            }

            rawWords.add(rawWords1.get(i));
        }
        List<String> cleaned = new ArrayList<>();

        //  unwanted words
        // for remove unwanted exact words
        Set<String> skipWords = new HashSet<>(Arrays.asList(
                "time", "cashier", "till", "amount", "aty.","qty.","qty", "price", "item",
                "discount", "bill", "total", "payment", "balance", "iten", "itern", "items",
                "pavable", "customer" , "gross","net" , "ems", "description","invoice","onapproval","voiceno","unitprice"
        ));
        // for remove words like this
        Set<String> matchingWords = new HashSet<>(Arrays.asList(
                "till","amount","item","discount","balance","payment","total","time","*","name", "code",":"
        ));
        Pattern[] skipPatterns = new Pattern[]{
                Pattern.compile("(?i)^x\\d+(\\.\\d+)?$"),// x2,x2 etc.
                Pattern.compile("\\b[kK]\\b|:"),// k , :
                Pattern.compile("\\d{1,2}"),
                Pattern.compile("\\d{4}-\\d{2}-\\d{1,2} \\d{1,2}:\\d{2}"), //Date/time
                Pattern.compile("\\d{4}-\\d{2}-\\d{1,2} \\d{1,2} \\d{1,2}:\\d{2}"),// Date /time
                Pattern.compile("(?i)^date / time$"),
                Pattern.compile("(?i)^till-\\d{1,2}"),
                //Pattern.compile("^\\d{1,6}(\\.\\d{1,2})?$")
                Pattern.compile("^0([.,]00)?$") // remove "0" or "0.00"
        };
        for (String word : rawWords) {
            String trimmed = word.replaceAll("\\s+","");
            // Skip empty strings or broken characters
            if (trimmed.isEmpty() || trimmed.equals("(") || trimmed.equals(")")) continue;
            // Lowercase version for comparison
            String lower = trimmed.toLowerCase();
            boolean wordSkip = false;
            boolean patternSkip = false;
            boolean matchingSkip = false;
            for (String skipWord : skipWords) {
                if (lower.equals(skipWord)) {
                    wordSkip = true;
                    break;
                }
            }
            for (String skipWord : matchingWords) {
                if (lower.contains(skipWord)) {
                    matchingSkip = true;
                    break;
                }
            }
            for (Pattern pattern : skipPatterns) {
                if (pattern.matcher(trimmed).matches()) {
                    patternSkip = true;
                    break;
                }
            }

            if ((!wordSkip) && (!patternSkip) && (!matchingSkip)){
                cleaned.add(trimmed);
            }

        }
        return cleaned;

    }
}
