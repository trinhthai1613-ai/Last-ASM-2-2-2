/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Employee;
import model.RequestForLeave;

/**
 *
 * @author sonnt
 */
public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    public ArrayList<RequestForLeave> getByEmployeeAndSubodiaries(int eid) {
        ArrayList<RequestForLeave> rfls = new ArrayList<>();
        try {
            String sql = """
                             WITH Org AS (
                             \tSELECT e.eid, e.ename, 0 AS lvl FROM Employee e WHERE e.eid = ?
                             \tUNION ALL
                             \tSELECT c.eid, c.ename, o.lvl + 1 FROM Employee c JOIN Org o ON c.supervisorid = o.eid
                             )
                             SELECT
                             \t r.rid,
                             \t r.created_by,
                             \t org.ename AS created_name,
                             \t org.lvl,
                             \t r.created_time,
                             \t r.[from],
                             \t r.[to],
                             \t r.reason,
                             \t r.status,
                             \t r.processed_by,
                             \t p.ename AS processed_name
                             FROM Org org INNER JOIN RequestForLeave r ON org.eid = r.created_by
                             LEFT JOIN Employee p ON p.eid = r.processed_by
                             ORDER BY r.created_time DESC""";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, eid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                RequestForLeave rfl = buildRequest(rs);
                rfl.setReviewerLevel(rs.getInt("lvl"));
                rfls.add(rfl);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return rfls;
    }

    public RequestForLeave getByIdForEmployeeAndSubordinates(int rid, int eid) {
        try {
            String sql = """
                             WITH Org AS (
                             \tSELECT e.eid, e.ename, 0 AS lvl FROM Employee e WHERE e.eid = ?
                             \tUNION ALL
                             \tSELECT c.eid, c.ename, o.lvl + 1 FROM Employee c JOIN Org o ON c.supervisorid = o.eid
                             )
                             SELECT
                             \t r.rid,
                             \t r.created_by,
                             \t org.ename AS created_name,
                             \t org.lvl,
                             \t r.created_time,
                             \t r.[from],
                             \t r.[to],
                             \t r.reason,
                             \t r.status,
                             \t r.processed_by,
                             \t p.ename AS processed_name
                             FROM Org org INNER JOIN RequestForLeave r ON org.eid = r.created_by
                             LEFT JOIN Employee p ON p.eid = r.processed_by
                             WHERE r.rid = ?""";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, eid);
            stm.setInt(2, rid);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                RequestForLeave rfl = buildRequest(rs);
                rfl.setReviewerLevel(rs.getInt("lvl"));
                return rfl;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return null;
    }

    public ArrayList<RequestForLeave> getByDivisionAndDateRange(int divisionId, LocalDate from, LocalDate to) {
        ArrayList<RequestForLeave> requests = new ArrayList<>();
        try {
            String sql = """
                             SELECT
                             \t r.rid,
                             \t r.created_by,
                             \t e.ename AS created_name,
                             \t r.created_time,
                             \t r.[from],
                             \t r.[to],
                             \t r.reason,
                             \t r.status,
                             \t r.processed_by,
                             \t p.ename AS processed_name
                             FROM RequestForLeave r
                             INNER JOIN Employee e ON e.eid = r.created_by
                             LEFT JOIN Employee p ON p.eid = r.processed_by
                             WHERE e.did = ? AND r.[to] >= ? AND r.[from] <= ?
                             ORDER BY e.ename, r.[from], r.[to]""";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, divisionId);
            stm.setDate(2, java.sql.Date.valueOf(from));
            stm.setDate(3, java.sql.Date.valueOf(to));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                requests.add(buildRequest(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return requests;
    }

    public boolean hasOverlappingRequest(int employeeId, java.sql.Date fromDate, java.sql.Date toDate) {
        if (fromDate == null || toDate == null) {
            return false;
        }
        try {
            String sql = "SELECT TOP 1 1 FROM RequestForLeave "
                    + "WHERE created_by = ? AND status <> ? AND [to] >= ? AND [from] <= ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, employeeId);
            stm.setInt(2, RequestForLeave.STATUS_REJECTED);
            stm.setDate(3, fromDate);
            stm.setDate(4, toDate);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return false;
    }

    @Override
    public ArrayList<RequestForLeave> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RequestForLeave get(int id) {
        try {
            String sql = """
                             SELECT
                             \t r.rid,
                             \t r.created_by,
                             \t e.ename AS created_name,
                             \t r.created_time,
                             \t r.[from],
                             \t r.[to],
                             \t r.reason,
                             \t r.status,
                             \t r.processed_by,
                             \t p.ename AS processed_name
                             FROM RequestForLeave r
                             INNER JOIN Employee e ON e.eid = r.created_by
                             LEFT JOIN Employee p ON p.eid = r.processed_by
                             WHERE r.rid = ?""";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return buildRequest(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return null;
    }

    @Override
    public void insert(RequestForLeave model) {
        try {
            String sql = "INSERT INTO RequestForLeave(created_by, created_time, [from], [to], reason, status, processed_by) VALUES (?, GETDATE(), ?, ?, ?, ?, NULL)";
            PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setInt(1, model.getCreated_by().getId());
            stm.setDate(2, model.getFrom());
            stm.setDate(3, model.getTo());
            stm.setString(4, model.getReason());
            stm.setInt(5, model.getStatus());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                model.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void update(RequestForLeave model) {
        try {
            String sql = "UPDATE RequestForLeave SET status = ?, processed_by = ? WHERE rid = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, model.getStatus());
            if (model.getProcessed_by() != null) {
                stm.setInt(2, model.getProcessed_by().getId());
            } else {
                stm.setNull(2, Types.INTEGER);
            }
            stm.setInt(3, model.getId());
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void delete(RequestForLeave model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private RequestForLeave buildRequest(ResultSet rs) throws SQLException {
        RequestForLeave rfl = new RequestForLeave();
        rfl.setId(rs.getInt("rid"));
        rfl.setCreated_time(rs.getTimestamp("created_time"));
        rfl.setFrom(rs.getDate("from"));
        rfl.setTo(rs.getDate("to"));
        rfl.setReason(rs.getString("reason"));
        rfl.setStatus(rs.getInt("status"));

        Employee creator = new Employee();
        creator.setId(rs.getInt("created_by"));
        creator.setName(rs.getString("created_name"));
        rfl.setCreated_by(creator);

        int processedId = rs.getInt("processed_by");
        if (!rs.wasNull()) {
            Employee processor = new Employee();
            processor.setId(processedId);
            processor.setName(rs.getString("processed_name"));
            rfl.setProcessed_by(processor);
        }
        return rfl;
    }
}
