/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.home;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RoleDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import model.iam.Feature;
import model.iam.Role;
import model.iam.User;

/**
 *
 * @author sonnt
 */
@WebServlet(urlPatterns = "/home")
public class HomeController extends BaseRequiredAuthenticationController {

    private static final Map<String, String> FEATURE_LABELS = new LinkedHashMap<>();

    static {
        FEATURE_LABELS.put("/request/create", "Create leave request");
        FEATURE_LABELS.put("/request/list", "View requests");
        FEATURE_LABELS.put("/request/review", "Review subordinate requests");
        FEATURE_LABELS.put("/division/agenda", "Division agenda");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        doGet(req, resp, user);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        ensureRolesLoaded(req, user);
        LinkedHashMap<String, String> features = new LinkedHashMap<>();
        for (Role role : user.getRoles()) {
            for (Feature feature : role.getFeatures()) {
                features.putIfAbsent(feature.getUrl(), FEATURE_LABELS.getOrDefault(feature.getUrl(), feature.getUrl()));
            }
        }
        req.setAttribute("features", features);
        req.getRequestDispatcher("/view/home/index.jsp").forward(req, resp);
    }

    private void ensureRolesLoaded(HttpServletRequest req, User user) {
        if (user.getRoles().isEmpty()) {
            RoleDBContext db = new RoleDBContext();
            user.setRoles(db.getByUserId(user.getId()));
            req.getSession().setAttribute("auth", user);
        }
    }
}
