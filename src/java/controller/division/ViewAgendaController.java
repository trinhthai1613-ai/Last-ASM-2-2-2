/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.division;

import controller.iam.BaseRequiredAuthorizationController;
import dal.EmployeeDBContext;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.AgendaRow;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

/**
 *
 * @author sonnt
 */
@WebServlet(urlPatterns = "/division/agenda")
public class ViewAgendaController extends BaseRequiredAuthorizationController {

    private static final int DEFAULT_TIMELINE_DAYS = 7;
    private static final int MAX_TIMELINE_DAYS = 31;

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        processGet(req, resp, user);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        LocalDate start = parseDate(req.getParameter("from"), LocalDate.now());
        int days = parseDays(req.getParameter("days"), DEFAULT_TIMELINE_DAYS);
        LocalDate end = start.plusDays(Math.max(days, 1) - 1);

        EmployeeDBContext employeeDb = new EmployeeDBContext();
        Integer divisionId = employeeDb.getDivisionIdByEmployee(user.getEmployee().getId());
        if (divisionId == null) {
            req.setAttribute("error", "Unable to detect your division. Please contact the administrator.");
            req.getRequestDispatcher("/view/division/agenda.jsp").forward(req, resp);
            return;
        }

        ArrayList<Employee> employees = new EmployeeDBContext().getByDivision(divisionId);
        ArrayList<RequestForLeave> requests = new RequestForLeaveDBContext()
                .getByDivisionAndDateRange(divisionId, start, end);

        ArrayList<Date> timeline = buildTimeline(start, end);
        List<AgendaRow> rows = buildAgendaRows(employees, requests, start, timeline.size());

        req.setAttribute("timeline", timeline);
        req.setAttribute("rows", rows);
        req.setAttribute("fromDate", Date.valueOf(start));
        req.setAttribute("toDate", Date.valueOf(end));
        req.setAttribute("days", days);
        req.getRequestDispatcher("/view/division/agenda.jsp").forward(req, resp);
    }

    private LocalDate parseDate(String raw, LocalDate defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(raw);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    private int parseDays(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw);
            if (value <= 0) {
                return defaultValue;
            }
            return Math.min(value, MAX_TIMELINE_DAYS);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private ArrayList<Date> buildTimeline(LocalDate start, LocalDate end) {
        ArrayList<Date> timeline = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            timeline.add(Date.valueOf(cursor));
            cursor = cursor.plusDays(1);
        }
        return timeline;
    }

    private List<AgendaRow> buildAgendaRows(ArrayList<Employee> employees,
            ArrayList<RequestForLeave> requests,
            LocalDate start,
            int rangeLength) {
        Map<Integer, AgendaRow> rows = new LinkedHashMap<>();
        for (Employee employee : employees) {
            AgendaRow row = new AgendaRow();
            row.setEmployee(employee);
            ArrayList<Integer> statuses = new ArrayList<>(Collections.nCopies(rangeLength, null));
            row.setStatuses(statuses);
            rows.put(employee.getId(), row);
        }

        LocalDate timelineEnd = start.plusDays(rangeLength - 1);
        for (RequestForLeave request : requests) {
            AgendaRow row = rows.get(request.getCreated_by().getId());
            if (row == null) {
                continue;
            }
            LocalDate requestStart = request.getFrom().toLocalDate();
            LocalDate requestEnd = request.getTo().toLocalDate();
            LocalDate effectiveStart = requestStart.isBefore(start) ? start : requestStart;
            LocalDate effectiveEnd = requestEnd.isAfter(timelineEnd) ? timelineEnd : requestEnd;
            if (effectiveEnd.isBefore(effectiveStart)) {
                continue;
            }
            for (LocalDate cursor = effectiveStart; !cursor.isAfter(effectiveEnd); cursor = cursor.plusDays(1)) {
                int index = (int) java.time.temporal.ChronoUnit.DAYS.between(start, cursor);
                if (index >= 0 && index < row.getStatuses().size()) {
                    Integer currentStatus = row.getStatuses().get(index);
                    if (currentStatus == null || currentStatus == RequestForLeave.STATUS_IN_PROGRESS) {
                        row.getStatuses().set(index, request.getStatus());
                    }
                }
            }
        }
        return new ArrayList<>(rows.values());
    }
}
