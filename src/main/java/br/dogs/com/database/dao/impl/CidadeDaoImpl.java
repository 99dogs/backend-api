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
import br.dogs.com.database.dao.CidadeDao;
import br.dogs.com.model.entities.Cidade;

@Repository
public class CidadeDaoImpl implements CidadeDao {
	
	Logger logger = LoggerFactory.getLogger(CidadeDaoImpl.class);
	
	@Override
	public List<Cidade> buscarPorEstado(Long estadoId) {
		
		List<Cidade> cidades = new ArrayList<Cidade>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from cidade where id_estado = ? and ativo = true ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, estadoId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Cidade cidade = new Cidade();
				
				cidade.setId(resultSet.getLong("id"));
				cidade.setNome(resultSet.getString("nome"));
				cidade.setAtivo(resultSet.getBoolean("ativo"));
				
				cidades.add(cidade);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return cidades;
		
	}

	@Override
	public Cidade buscarPorId(Long id) {
		
		Cidade cidade = new Cidade();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from cidade where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				cidade.setId(resultSet.getLong("id"));
				cidade.setNome(resultSet.getString("nome"));
				cidade.setAtivo(resultSet.getBoolean("ativo"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return cidade;
		
	}

}
