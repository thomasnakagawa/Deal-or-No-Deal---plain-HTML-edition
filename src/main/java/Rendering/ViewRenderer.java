package Rendering;

import GameState.GameState;
import org.apache.velocity.app.*;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import spark.*;
import spark.template.velocity.*;

import java.util.*;

public class ViewRenderer {

    public static String renderNextGameView(String gameID, GameState gameState) {
        Map<String, Object> model = new HashMap<>();
        model.put("gameID", gameID);
        model.put("gameState", gameState);
        model.put("Render", RenderingConstants.class);
        model.put("Formatter", Formatter.class);

        switch (gameState.getGamePhase()) {
            case choosingInitialCase:
            case openingCases:
                return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/caseView.vm"));
            case pendingDeal:
                return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/bankerView.vm"));
            case pendingSwap:
                return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/swapView.vm"));
            case finished:
                return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/ending.vm"));
            default:
                throw new IllegalArgumentException("Cannot render game for this game phase");
        }
    }

    public static String renderLeaderboard(List<String> scores) {
        Map<String, Object> model = new HashMap<>();
        model.put("scores", scores);
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/leaderboard.vm"));
    }

    public static String renderHomeScreen() {
        Map<String, Object> model = new HashMap<>();
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/index.vm"));
    }

    public static String renderAboutPage() {
        Map<String, Object> model = new HashMap<>();
        return strictVelocityEngine().render(new ModelAndView(model, "/Velocity/about.vm"));
    }

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        configuredEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        return new VelocityTemplateEngine(configuredEngine);
    }
}