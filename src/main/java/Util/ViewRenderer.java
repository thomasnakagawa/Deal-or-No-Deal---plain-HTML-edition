package Util;

import GameState.GameState;
import org.apache.velocity.app.*;
import spark.*;
import spark.template.velocity.*;

import java.util.*;

public class ViewRenderer {

    public static String renderCaseView(String gameID, GameState gameState) {
        Map<String, Object> model = new HashMap<>();
        model.put("gameID", gameID);
        model.put("gameState", gameState);
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/caseView.vm"));
    }

    public static String renderBankerView(String gameID, GameState gameState, String bankerOffer) {
        Map<String, Object> model = new HashMap<>();
        model.put("gameID", gameID);
        model.put("gameState", gameState);
        model.put("bankerOffer", bankerOffer);
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/bankerView.vm"));
    }

    public static String renderSwapView(String gameID, GameState gameState) {
        Map<String, Object> model = new HashMap<>();
        model.put("gameID", gameID);
        model.put("gameState", gameState);
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/swapView.vm"));
    }

    public static String renderEnding(String gameID, GameState gameState, boolean swapped, boolean deal) {
        Map<String, Object> model = new HashMap<>();
        model.put("gameID", gameID);
        model.put("gameState", gameState);
        model.put("swapped", swapped);
        model.put("deal", deal);
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/ending.vm"));
    }

    public static String renderHomeScreen() {
        Map<String, Object> model = new HashMap<>();
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/index.vm"));
    }

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}