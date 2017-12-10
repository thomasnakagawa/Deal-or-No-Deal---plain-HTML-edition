package Rendering;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RenderingConstants {
    private static final Map<String, String> constantsMap = Stream.of(
            new AbstractMap.SimpleEntry<>("BLANK_CASE_COST", "______"),
            new AbstractMap.SimpleEntry<>("CASE_SPACER", "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp")
    ).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

    public static String constant(String key) {
        return constantsMap.get(key);
    }
}
