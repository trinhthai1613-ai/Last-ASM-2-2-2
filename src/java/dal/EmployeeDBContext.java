/*
 * Employee data access helper used for agenda view and request creation logic.
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Department;
import model.Employee;

public class EmployeeDBContext extends DBContext<Employee> {

    public Integer getDivisionIdByEmployee(int employeeId) {
        try {
            String sql = "SELECT did FROM Employee WHERE eid = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, employeeId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                int divisionId = rs.getInt("did");
                if (rs.wasNull()) {
                    return null;
                }
                return divisionId;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return null;
    }

    public ArrayList<Employee> getByDivision(int divisionId) {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            String sql = """
                             SELECT e.eid, e.ename, e.supervisorid, d.did, d.dname
                             FROM Employee e INNER JOIN Division d ON e.did = d.did
                             WHERE e.did = ?
                             ORDER BY e.ename
                             """;
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, divisionId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("eid"));
                e.setName(rs.getString("ename"));

                Department dept = new Department();
                dept.setId(rs.getInt("did"));
                dept.setName(rs.getString("dname"));
                e.setDept(dept);

                int supervisorId = rs.getInt("supervisorid");
                if (!rs.wasNull()) {
                    Employee supervisor = new Employee();
                    supervisor.setId(supervisorId);
                    e.setSupervisor(supervisor);
                }

                employees.add(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return employees;
    }

    @Override
    public ArrayList<Employee> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Employee get(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(Employee model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Employee model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Employee model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
