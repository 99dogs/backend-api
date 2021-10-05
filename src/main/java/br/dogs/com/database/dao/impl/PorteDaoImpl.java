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
import br.dogs.com.database.dao.PorteDao;
import br.dogs.com.model.entities.Porte;

@Repository
public class PorteDaoImpl implements PorteDao {
	
	Logger logger = LoggerFactory.getLogger(PorteDaoImpl.class);
	
	@Override
	public List<Porte> buscarTodos() {
		
		List<Porte> portes = new ArrayList<Porte>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from porte ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Porte porte = new Porte();
				
				porte.setId(resultSet.getLong("id"));
				porte.setCriado(resultSet.getTimestamp("criado"));
				porte.setModificado(resultSet.getTimestamp("modificado"));
				porte.setNome(resultSet.getString("nome"));
				
				portes.add(porte);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return portes;
		
	}
	
	@Override
	public Porte buscarPorId(Long id) {
		
		Porte porte = new Porte();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from porte where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				porte.setId(resultSet.getLong("id"));
				porte.setCriado(resultSet.getTimestamp("criado"));
				porte.setModificado(resultSet.getTimestamp("modificado"));
				porte.setNome(resultSet.getString("nome"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return porte;
		
	}

	@Override
	public Porte cadastrar(Porte entity) {
		
		Porte porte = new Porte();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into porte (criado, modificado, nome) values (?,?,?) ";

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
				porte.setId(resultSet.getLong(1));
			}
			
			porte = buscarPorId(porte.getId());
			
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

		return porte;
		
	}

	@Override
	public boolean alterar(Porte entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update porte set modificado = ?, nome = ? where id = ? ";

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
	public boolean deletarPorId(Long id) {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = "DELETE FROM porte WHERE id = ?;";

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
