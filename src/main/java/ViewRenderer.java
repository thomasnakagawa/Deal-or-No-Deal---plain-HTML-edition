import org.apache.velocity.app.*;
import org.eclipse.jetty.http.*;
import spark.*;
import spark.template.velocity.*;

import java.text.Format;
import java.util.*;

public class ViewRenderer {

    // Renders a template given a model and a request
    // The request is needed to check the user session for language settings
    // and to see if the user is logged in
    /*public static String render(Request request, Map<String, Object> model, String templatePath) {
        model.put("msg", new MessageBundle(getSessionLocale(request)));
        model.put("currentUser", getSessionCurrentUser(request));
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }*/

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

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}