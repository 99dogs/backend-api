package br.dogs.com.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.RacaDao;
import br.dogs.com.model.entities.Raca;

@Repository
public class RacaDaoImpl implements RacaDao {
	
	Logger logger = LoggerFactory.getLogger(RacaDaoImpl.class);
	
	@Override
	public List<Raca> findAll() {
		
		List<Raca> racas = new ArrayList<Raca>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from raca ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Raca raca = new Raca();
				
				raca.setId(resultSet.getLong("id"));
				raca.setCriado(resultSet.getTimestamp("criado"));
				raca.setModificado(resultSet.getTimestamp("modificado"));
				raca.setNome(resultSet.getString("nome"));
				
				racas.add(raca);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return racas;
		
	}

	@Override
	public Raca findById(Long id) {
		
		Raca raca = new Raca();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from raca where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
			
				raca.setId(resultSet.getLong("id"));
				raca.setCriado(resultSet.getTimestamp("criado"));
				raca.setModificado(resultSet.getTimestamp("modificado"));
				raca.setNome(resultSet.getString("nome"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return raca;
		
	}

	@Override
	public Raca create(Raca entity) {
		
		Raca raca = new Raca();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into raca (criado, modificado, nome) values (?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getNome());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				raca.setId(resultSet.getLong(1));
			}
			
			raca = findById(raca.getId());
			
		} catch (Exception e) {

			logger.error(e.getMessage());

			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}

		} finally {
			ConnectionFactory.close(ps, connection);
		}

		return raca;
		
	}

	@Override
	public boolean update(Raca entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update raca set modificado = ?, nome = ? where id = ? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, entity.getNome());
			ps.setLong(3, entity.getId());

			ps.execute();

			connection.commit();

			return true;
			
		} catch (Exception e) {

			logger.error(e.getMessage());

			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}

		} finally {
			ConnectionFactory.close(ps, connection);
		}

		return false;
		
	}

	@Override
	public boolean deleteById(Long id) {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = "DELETE FROM raca WHERE id = ?;";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			preparedStatement.execute();
			connection.commit();
			
			return true;

		} catch (Exception e) {
			
			logger.error(e.getMessage());
			
			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
			
		} finally {
			ConnectionFactory.close(preparedStatement, connection);
		}
		
		return false;
		
	}

}
