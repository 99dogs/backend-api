package br.dogs.com.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConnectionFactory {

	private static Connection connection = null;	
	
	private static String host;
	private static String databaseName;
	private static String user;
	private static String password;
	
	@Value("${database_host}")
	public void setHost(String data) {
		ConnectionFactory.host = data;
	}
	
	@Value("${database_name_database}")
	public void setNameDatabase(String data) {
		ConnectionFactory.databaseName = data;
	}
	
	@Value("${database_user}")
	public void setUser(String data) {
		ConnectionFactory.user = data;
	}
	
	@Value("${database_password}")
	public void setPassword(String data) {
		ConnectionFactory.password = data;
	}
	
	public static Connection getConnection() {

		try {

			connection = DriverManager.getConnection(host+"/"+databaseName, user, password);
			
		} catch (SQLException e) {
			System.out.println("Ocorreu um erro ao obter a instancia do banco de dados: " + e.getMessage());
		}

		return connection;
	}

	private static void closeResultSet(ResultSet resultSet) {

		try {
			resultSet.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private static void closePreparedStatement(PreparedStatement preparedStatement) {
		try {
			preparedStatement.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private static void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
		closeResultSet(resultSet);
		closePreparedStatement(preparedStatement);
		closeConnection(connection);

	}

	public static void close(PreparedStatement preparedStatement, Connection connection) {

		closePreparedStatement(preparedStatement);
		closeConnection(connection);

	}
	
}
