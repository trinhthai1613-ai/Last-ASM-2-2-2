package model;

import java.util.ArrayList;

/**
 * Simple DTO that represents the leave status for an employee in a date range.
 */
public class AgendaRow {
    private Employee employee;
    private ArrayList<Integer> statuses = new ArrayList<>();

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public ArrayList<Integer> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Integer> statuses) {
        this.statuses = statuses;
    }
}
