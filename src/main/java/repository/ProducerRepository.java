package repository;

import Dominio.Producer;
import jdbc.conn.ConnectionFactory;
import lombok.extern.log4j.Log4j2;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ProducerRepository {

    public static void save(Producer producer) {
        PreparedStatement st = null;
        try {
            Connection conn = ConnectionFactory.getConnection();

            st = conn.prepareStatement(
                    "INSERT INTO `anime_store`.`producer` (`name`) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, producer.getName());

            int rowsAffected = st.executeUpdate();
            log.info("Inserted producer '{}' in the database, rows affected '{}'", producer.getName(), rowsAffected);
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    producer.setId(id);
                }
                rs.close();
            }
        } catch (SQLException e) {
            log.error("Error while trying to insert producer '{}'", producer.getName(), e);
        } finally {
            ConnectionFactory.closeStatement(st);
        }
    }

    public static void  saveTransaction(List<Producer> producers) {
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            for (Producer p : producers) {
                st = conn.prepareStatement(
                        "INSERT INTO `anime_store`.`producer` (`name`) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS);
                log.info("Saving producer '{}'", p.getName());
                st.setString(1, p.getName());
                //if (p.getName().equals("White fox")) throw new SQLException("Can't save white fox");
                st.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException msg) {
                msg.printStackTrace();
            }
        } finally {
            ConnectionFactory.closeStatement(st);
        }
    }

    public static void delete(int id) {
        PreparedStatement st = null;

        try {
            Connection conn = ConnectionFactory.getConnection();

            st = conn.prepareStatement(
                    "DELETE FROM producer "
                            + "WHERE id = ?");
            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();
            log.info("Deleted producer '{}' from the database, rows affected '{}' ", id, rowsAffected);
        } catch (SQLException e) {
            log.error("Error while trying to delete producer '{}'", id, e);
        } finally {
            ConnectionFactory.closeStatement(st);
        }
    }

    public static void update(Producer producer) {
        PreparedStatement st = null;

        try {
            Connection conn = ConnectionFactory.getConnection();

            st = conn.prepareStatement(
                    "UPDATE producer "
                            + "SET Name = ? "
                            + "WHERE id = ?");
            st.setString(1, producer.getName());
            st.setInt(2, producer.getId());

            int rowsAffected = st.executeUpdate();
            log.info("Update producer '{}' from the database, rowsaffected '{}' ", producer.getId(), rowsAffected);
        } catch (SQLException e) {
            log.info("Error while trying to update producer '{}' ", producer.getId());
        } finally {
            ConnectionFactory.closeStatement(st);
        }
    }

    public static List<Producer> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            log.info("Finding all Producers");
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM anime_store.producer "
                            + "ORDER BY name");

            rs = st.executeQuery();
            List<Producer> producers = new ArrayList<>();

            while (rs.next()) {
                Producer producer = Producer
                        .builder()
                        .id(rs.getInt("Id"))
                        .name(rs.getString("Name"))
                        .build();
                producers.add(producer);
            }
            return producers;
        } catch (SQLException e) {
            log.info("Error while trying to find all producers ", e);
            return null;
        } finally {
            ConnectionFactory.closeStatement(st);
            ConnectionFactory.closeResultSet(rs);
        }
    }

    public static List<Producer> findByName(String name) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer "
                            + "WHERE name LIKE ?");

            st.setString(1, "%" + name + "%");
            rs = st.executeQuery();
            List<Producer> producers = new ArrayList<>();
            while (rs.next()) {
                Producer producer = Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
                producers.add(producer);
            }
            return producers;

        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
            return null;
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static List<Producer> findByNamePreparedStatement(String name) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer "
                            + "WHERE name LIKE ?");

            st.setString(1, "%" + name + "%");
            rs = st.executeQuery();
            List<Producer> producers = new ArrayList<>();
            while (rs.next()) {
                Producer producer = Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
                producers.add(producer);
            }
            return producers;

        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
            return null;
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static List<Producer> findByNamCallableStatement(String name) {
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            cs = conn.prepareCall("CALL `anime_store`.`sp_get_producer_by_name`(?);");
            cs.setString(1, "%" + name + "%");
            rs = cs.executeQuery();
            List<Producer> producers = new ArrayList<>();
            while (rs.next()) {
                Producer producer = Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
                producers.add(producer);
            }
            return producers;

        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
            return null;
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(cs);
        }
    }

    public static void showProducerMetaData() {
        PreparedStatement st = null;
        ResultSet rs = null;
        log.info("Showing Producer Metadata");

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer ");
            rs = st.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            log.info("Columns count '{}", columnCount);
            for (int i = 1; i <= columnCount; i++) {
                log.info("Table Name '{}'", rsMetaData.getTableName(i));
                log.info("Column Name '{}'", rsMetaData.getColumnName(i));
                log.info("Column size '{}'", rsMetaData.getColumnDisplaySize(i));
                log.info("Column type '{}'", rsMetaData.getColumnTypeName(i));
            }

        } catch (SQLException e) {
            log.info("Error while trying to find producers ", e);
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static void showDriverMetaData() {
        log.info("Showing Driver Metadata");

        try {
            Connection conn = ConnectionFactory.getConnection();
            DatabaseMetaData dbMetaData = conn.getMetaData();
            if (dbMetaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
                log.info("Supports TYPE_FORWARD_ONLY");
                if (dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                    log.info("And Supports CONCUR_UPDATABLE");
                }
            }
            if (dbMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
                log.info("Supports TYPE_SCROLL_INSESITIVE");
                if (dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    log.info("And Supports CONCUR_UPDATABLE");
                }
            }
            if (dbMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
                log.info("Supports TYPE_SCROLL_SENSITIVE");
                if (dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    log.info("And Supports CONCUR_UPDATABLE");
                }
            }
        } catch (SQLException e) {
            log.info("Error while trying to find producers ", e);
        }
    }

    public static void showTypeScrollWorking() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer ",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery();

            log.info("Last row? '{}' ", rs.last());
            log.info("Row number? '{}' ", rs.getRow());
            log.info(Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build());

            log.info("First row? '{}' ", rs.first());
            log.info("Row number? '{}' ", rs.getRow());
            log.info(Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build());

            log.info("Row Absolute? '{}' ", rs.absolute(2));
            log.info("Row number? '{}' ", rs.getRow());
            log.info(Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build());

            log.info("Row relative? '{}' ", rs.relative(-1));
            log.info("Row number? '{}' ", rs.getRow());
            log.info(Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build());

            log.info("Is last ? '{}'", rs.isLast());
            log.info("Row number '{}'", rs.getRow());

            log.info("Is last ? '{}'", rs.isFirst());
            log.info("Row number '{}'", rs.getRow());

            log.info("Last row? '{}' ", rs.last());
            log.info("-------------------");
            rs.next();
            log.info("After last row? '{}' ", rs.isAfterLast());
            while (rs.previous()) {
                log.info(Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build());
            }
        } catch (SQLException e) {
            log.info("Error while trying to find producers ", e);
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static List<Producer> findByNameAndUpdateToUpperCase(String name) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer "
                            + "WHERE name LIKE ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            st.setString(1, "%" + name + "%");
            rs = st.executeQuery();
            List<Producer> producers = new ArrayList<>();
            while (rs.next()) {
                rs.updateString("name", rs.getString("name").toUpperCase());
                // rs.cancelRowUpdates();
                rs.updateRow();
                Producer producer = Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
                producers.add(producer);
            }
            return producers;

        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
            return null;
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static List<Producer> findByNameAndInsertWhenNotFound(String name) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer "
                            + "WHERE name LIKE ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            st.setString(1, "%" + name + "%");
            rs = st.executeQuery();
            List<Producer> producers = new ArrayList<>();
            if (rs.next()) return producers;

            rs.moveToInsertRow();
            rs.updateString("name", name);
            rs.insertRow();
            rs.beforeFirst();
            rs.next();
            Producer producer = Producer.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
            producers.add(producer);

            return producers;
        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
            return null;
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }

    public static void findByNameAndDelete(String name) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            st = conn.prepareStatement(
                    "SELECT * "
                            + "FROM producer "
                            + "WHERE name LIKE ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            st.setString(1, "%" + name + "%");
            rs = st.executeQuery();
            while (rs.next()) {
                log.info("Deleteting '{}'", rs.getString("name"));
                rs.deleteRow();
            }
        } catch (SQLException e) {
            log.info("Error while trying to find %s producers ", name, e);
        } finally {
            ConnectionFactory.closeResultSet(rs);
            ConnectionFactory.closeStatement(st);
        }
    }
}