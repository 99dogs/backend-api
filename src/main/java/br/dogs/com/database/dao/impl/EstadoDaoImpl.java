package br.dogs.com.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.EstadoDao;
import br.dogs.com.model.entities.Estado;

@Repository
public class EstadoDaoImpl implements EstadoDao {
	
	Logger logger = LoggerFactory.getLogger(EstadoDaoImpl.class);
	
	@Override
	public List<Estado> buscarTodos() {
		
		List<Estado> estados = new ArrayList<Estado>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from estado where ativo = true ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Estado estado = new Estado();
				
				estado.setId(resultSet.getLong("id"));
				estado.setNome(resultSet.getString("nome"));
				estado.setSigla(resultSet.getString("sigla"));
				estado.setAtivo(resultSet.getBoolean("ativo"));
				
				estados.add(estado);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return estados;
		
	}

	@Override
	public Estado buscarPorId(Long id) {
		
		Estado estado = new Estado();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from estado where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				estado.setId(resultSet.getLong("id"));
				estado.setNome(resultSet.getString("nome"));
				estado.setSigla(resultSet.getString("sigla"));
				estado.setAtivo(resultSet.getBoolean("ativo"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return estado;
		
	}

}
