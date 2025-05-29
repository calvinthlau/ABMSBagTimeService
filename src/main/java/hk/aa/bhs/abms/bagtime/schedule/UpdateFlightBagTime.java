package hk.aa.bhs.abms.bagtime.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.PreparedStatement;

@Component
public class UpdateFlightBagTime {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateFlightBagTime.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    public void scheduleJob() {
        String startTime = LocalDateTime.now().format(formatter);
        logger.info("Schedule job started at: {}", startTime);
        
        try {
            // Test database connection
            if (!testDatabaseConnection()) {
                logger.error("Database connection test failed");
                return;
            }
            logger.info("Database connection test successful");
            
            // Update flight bag times
            updateFlightBagTime();
            updateLastBagTime();
            
        } catch (Exception e) {
            logger.error("Error occurred during schedule job execution", e);
        } finally {
            String endTime = LocalDateTime.now().format(formatter);
            logger.info("Schedule job ended at: {}", endTime);
        }
    }

    /**
     * Updates the FirstBagTime in Flight table based on ScanFirstBagTime and waiting time setting
     * @return number of rows updated
     */
    public int updateFlightBagTime() {
        String sql = "UPDATE Flight " +
                    "SET FirstBagTime = ScanFirstBagTime " +
                    "WHERE ChockOnTime IS NOT NULL " +
                    "  AND ScanFirstBagTime IS NOT NULL " +
                    "  AND FirstBagTime IS NULL " +
                    "  AND DATEADD(SECOND, " +
                    "              (SELECT CAST(SettingValue AS INT) " +
                    "               FROM SystemSetting " +
                    "               WHERE SettingKey = 'AABD_FIRST_BAG_WAITING_TIME_IN_SEC'), " +
                    "              ScanFirstBagTime) = GETDATE()";
        
        try {
            logger.info("Starting flight bag time update process");
            int updatedRows = jdbcTemplate.execute(sql, (PreparedStatement ps) -> {
                return ps.executeUpdate();
            });
            if (updatedRows > 0) {
                logger.info("Successfully updated {} flight record(s) with FirstBagTime", updatedRows);
            } else {
                logger.info("No flight records needed to be updated at this time");
            }
            return updatedRows;
        } catch (Exception e) {
            logger.error("Error updating flight bag time: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Updates the LastBagTime in Flight table based on ScanLastBagTime and waiting time setting
     * @return number of rows updated
     */
    public int updateLastBagTime() {
        String sql = "UPDATE Flight " +
                    "SET LastBagTime = ScanLastBagTime " +
                    "WHERE ChockOnTime IS NOT NULL " +
                    "  AND ScanLastBagTime IS NOT NULL " +
                    "  AND LastBagTime IS NULL " +
                    "  AND DATEADD(SECOND, " +
                    "              (SELECT CAST(SettingValue AS INT) " +
                    "               FROM SystemSetting " +
                    "               WHERE SettingKey = 'AABD_FIRST_BAG_WAITING_TIME_IN_SEC'), " +
                    "              ScanLastBagTime) = GETDATE()";
        
        try {
            logger.info("Starting flight last bag time update process");
            int updatedRows = jdbcTemplate.execute(sql, (PreparedStatement ps) -> {
                return ps.executeUpdate();
            });
            if (updatedRows > 0) {
                logger.info("Successfully updated {} flight record(s) with LastBagTime", updatedRows);
            } else {
                logger.info("No flight records needed to be updated with LastBagTime at this time");
            }
            return updatedRows;
        } catch (Exception e) {
            logger.error("Error updating flight last bag time: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Tests the MSSQL database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testDatabaseConnection() {
        try {
            // Simple query to test the connection
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            logger.info("Database connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }
}

