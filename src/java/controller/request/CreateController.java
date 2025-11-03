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
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

/**
 *
 * @author sonnt
 */
@WebServlet(urlPatterns = "/request/create")
public class CreateController extends BaseRequiredAuthorizationController {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        ArrayList<String> errors = new ArrayList<>();
        RequestForLeave rfl = new RequestForLeave();
        Employee creator = user.getEmployee();
        rfl.setCreated_by(creator);

        String fromRaw = req.getParameter("from");
        String toRaw = req.getParameter("to");
        String reason = req.getParameter("reason");
        if (reason != null) {
            reason = reason.trim();
        }

        java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        LocalDate today = LocalDate.now();

        if (fromRaw == null || fromRaw.isBlank()) {
            errors.add("Please select the start date.");
        } else {
            try {
                fromDate = Date.valueOf(fromRaw);
            } catch (IllegalArgumentException ex) {
                errors.add("Invalid start date.");
            }
        }

        if (toRaw == null || toRaw.isBlank()) {
            errors.add("Please select the end date.");
        } else {
            try {
                toDate = Date.valueOf(toRaw);
            } catch (IllegalArgumentException ex) {
                errors.add("Invalid end date.");
            }
        }

        if (fromDate != null && toDate != null && toDate.before(fromDate)) {
            errors.add("End date must be after start date.");
        }

        if (fromDate != null && fromDate.toLocalDate().isBefore(today)) {
            errors.add("Start date must be today or later.");
        }

        if (toDate != null && toDate.toLocalDate().isBefore(today)) {
            errors.add("End date must be today or later.");
        }

        if (reason == null || reason.isBlank()) {
            errors.add("Please provide a reason for the leave request.");
        }

        rfl.setFrom(fromDate);
        rfl.setTo(toDate);
        rfl.setReason(reason);
        rfl.setStatus(RequestForLeave.STATUS_IN_PROGRESS);

        if (errors.isEmpty() && fromDate != null && toDate != null) {
            boolean hasOverlap = new RequestForLeaveDBContext()
                    .hasOverlappingRequest(creator.getId(), fromDate, toDate);
            if (hasOverlap) {
                errors.add("You already have another active leave request that overlaps this time range.");
            }
        }

        if (errors.isEmpty()) {
            RequestForLeaveDBContext db = new RequestForLeaveDBContext();
            db.insert(rfl);
            resp.sendRedirect("list?message=created");
        } else {
            req.setAttribute("errors", errors);
            req.setAttribute("rfl", rfl);
            req.getRequestDispatcher("/view/request/create.jsp").forward(req, resp);
        }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        RequestForLeave rfl = new RequestForLeave();
        rfl.setCreated_by(user.getEmployee());
        LocalDate today = LocalDate.now();
        rfl.setFrom(Date.valueOf(today));
        rfl.setTo(Date.valueOf(today));
        req.setAttribute("rfl", rfl);
        req.getRequestDispatcher("/view/request/create.jsp").forward(req, resp);
    }

}
