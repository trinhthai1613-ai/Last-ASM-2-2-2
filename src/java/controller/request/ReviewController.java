/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

/**
 *
 * @author sonnt
 */
@WebServlet(urlPatterns = "/request/review")
public class ReviewController extends BaseRequiredAuthorizationController {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        ArrayList<String> errors = new ArrayList<>();
        String ridRaw = req.getParameter("rid");
        String decision = req.getParameter("decision");
        int rid = parseId(ridRaw);

        if (rid == -1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave rfl = db.getByIdForEmployeeAndSubordinates(rid, user.getEmployee().getId());
        if (rfl == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!rfl.getCanProcess()) {
            errors.add("You are not allowed to process this request anymore.");
        }

        if (!"approve".equals(decision) && !"reject".equals(decision)) {
            errors.add("Unsupported decision.");
        }

        if (errors.isEmpty()) {
            RequestForLeave updateModel = new RequestForLeave();
            updateModel.setId(rfl.getId());
            updateModel.setStatus("approve".equals(decision)
                    ? RequestForLeave.STATUS_APPROVED
                    : RequestForLeave.STATUS_REJECTED);
            Employee processor = user.getEmployee();
            updateModel.setProcessed_by(processor);
            RequestForLeaveDBContext updateDb = new RequestForLeaveDBContext();
            updateDb.update(updateModel);
            resp.sendRedirect("list?message=updated");
        } else {
            req.setAttribute("errors", errors);
            req.setAttribute("rfl", rfl);
            req.getRequestDispatcher("/view/request/review.jsp").forward(req, resp);
        }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        int rid = parseId(req.getParameter("rid"));
        if (rid == -1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave rfl = db.getByIdForEmployeeAndSubordinates(rid, user.getEmployee().getId());
        if (rfl == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("rfl", rfl);
        req.getRequestDispatcher("/view/request/review.jsp").forward(req, resp);
    }

    private int parseId(String raw) {
        if (raw == null) {
            return -1;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
