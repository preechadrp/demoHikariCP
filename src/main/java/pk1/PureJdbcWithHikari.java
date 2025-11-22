package pk1;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PureJdbcWithHikari {

	static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PureJdbcWithHikari.class);
	private static HikariDataSource dataSource;

	public static void initializeDataSource() {
		// 1. สร้าง HikariConfig
		HikariConfig config = new HikariConfig();

		// 2. ตั้งค่า Connection (สำหรับ Pure JDBC)
		config.setJdbcUrl("jdbc:firebird://localhost:3050/D:/javaDemo1/TESTHIBERNET.FDB?encoding=UTF8");
		config.setUsername("SYSDBA");
		config.setPassword("masterkey");

		// 3. ตั้งค่า Pool (ค่าแนะนำ)
		config.setMaximumPoolSize(10); // จำนวน Connection สูงสุด
		config.setPoolName("MyHikariPool");

		// 4. สร้าง HikariDataSource
		dataSource = new HikariDataSource(config);
	}

	// เมธอดสำหรับดึง Connection 
	public static Connection getConnection() throws SQLException {
		// HikariCP จะจัดการการให้และคืน Connection ให้โดยอัตโนมัติ
		if (dataSource == null) {
			throw new IllegalStateException("DataSource Not Initialized");
		}
		return dataSource.getConnection();
	}

	// เมธอดสำหรับทำ Database Operation (Pure JDBC)
	public static void fetchData() {
		try (Connection conn = getConnection();
				// ใช้วิธี Pure JDBC ในการสร้าง Statement
				var stmt = conn.prepareStatement("SELECT * FROM USER_TABLE");
				var rs = stmt.executeQuery()) {

			while (rs.next()) {
				log.info("USER_ID : {} ,Username : {}", rs.getInt("USER_ID"), rs.getString("USER_NAME"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ... เมื่อแอปพลิเคชันปิด ให้ปิด Pool ด้วย
	public static void shutdown() {
		if (dataSource != null) {
			dataSource.close();
		}
	}

	public static void main(String[] args) {
		try {
			initializeDataSource();
			fetchData();
			shutdown();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}