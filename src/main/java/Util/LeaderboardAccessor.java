package Util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardAccessor {
    private static String dreamloKey;

    public static List<String> getLeaderboardScores() throws IOException {
        try {
            String request = ("http://dreamlo.com/lb/$key/pipe")
                    .replace("$key", getDreamLoKey());

            HttpResponse<String> response = Unirest.get(request).asString();
            return formatScores(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> postScoreAndGetLeaderboardScores(String name, int score) throws IOException {
        try {
            String request = ("http://dreamlo.com/lb/$key/add-pipe/$name/$score")
                    .replace("$key", getDreamLoKey())
                    .replace("$name", name)
                    .replace("$score", Integer.toString(score));

            HttpResponse<String> response = Unirest.get(request).asString();
            return formatScores(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> formatScores(String body) {
        List<String> scores = new ArrayList<>();
        int number = 1;
        for (String line : body.split("\n")) {
            String[] segments = line.split("\\|");
            scores.add(Integer.toString(number) + ": " + segments[0] + " | " + segments[1]);
            number += 1;
        }
        return scores;
    }

    private static String getDreamLoKey() throws IOException {
        if (dreamloKey == null) {
            BufferedReader br = new BufferedReader(new FileReader("./private/dreamloKey.txt"));
            try {
                dreamloKey = br.readLine();
            }finally {
                br.close();
            }
        }
        return dreamloKey;
    }
}
