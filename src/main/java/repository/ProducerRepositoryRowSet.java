package repository;

import Dominio.Producer;
import jdbc.conn.ConnectionFactory;
import listener.CustomRowSetListener;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProducerRepositoryRowSet {

    public static List<Producer> findByNameJdbcRowSet(String name) {
        String sql = "SELECT * "
                + "FROM producer "
                + "WHERE name LIKE ?";
        List<Producer> producers = new ArrayList<>();
        try (JdbcRowSet jrs = ConnectionFactory.getJdbcRowSet()){
            jrs.setCommand(sql);
            jrs.setString(1, "%" + name + "%");
            jrs.execute();
            while (jrs.next()){
                Producer producer = Producer.builder().id(jrs.getInt("id")).name(jrs.getString("name")).build();
                producers.add(producer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producers;
    }

    public static void updateJdbcRowSet(Producer producer) {
        try (JdbcRowSet jrs = ConnectionFactory.getJdbcRowSet()){
            jrs.addRowSetListener(new CustomRowSetListener());
            jrs.setCommand("SELECT * "
                    + "FROM producer "
                    + "WHERE id = ?");
            jrs.setInt(1, producer.getId());
            jrs.execute();
            if (!jrs.next()) return;
            jrs.updateString("name", producer.getName());
            jrs.updateRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCachedRowSet(Producer producer) {
        try (CachedRowSet crs = ConnectionFactory.getCachedRowSet();
             Connection connection = ConnectionFactory.getConnection()){
            connection.setAutoCommit(false);
            crs.setCommand("SELECT * "
                    + "FROM producer "
                    + "WHERE id = ?");
            crs.setInt(1, producer.getId());
            crs.execute(connection);
            if (!crs.next()) return;
            crs.updateString("name", producer.getName());
            crs.updateRow();
            crs.acceptChanges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
