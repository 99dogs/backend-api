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
import br.dogs.com.database.dao.FormaDePagamentoDao;
import br.dogs.com.model.entities.FormaDePagamento;

@Repository
public class FormaDePagamentoDaoImpl implements FormaDePagamentoDao {
	
	Logger logger = LoggerFactory.getLogger(FormaDePagamentoDaoImpl.class);
	
	@Override
	public List<FormaDePagamento> buscarTodos() {
		
		List<FormaDePagamento> formasPagamento = new ArrayList<FormaDePagamento>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from forma_de_pagamento ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				FormaDePagamento formaPagamento = new FormaDePagamento();
				
				formaPagamento.setId(resultSet.getLong("id"));
				formaPagamento.setCriado(resultSet.getTimestamp("criado"));
				formaPagamento.setModificado(resultSet.getTimestamp("modificado"));
				formaPagamento.setNome(resultSet.getString("nome"));
				formaPagamento.setTipo(resultSet.getString("tipo"));
				formaPagamento.setAtivo(resultSet.getBoolean("ativo"));
				
				formasPagamento.add(formaPagamento);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return formasPagamento;
		
	}

	@Override
	public FormaDePagamento buscarPorId(Long id) {
		
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from forma_de_pagamento where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				formaPagamento.setId(resultSet.getLong("id"));
				formaPagamento.setCriado(resultSet.getTimestamp("criado"));
				formaPagamento.setModificado(resultSet.getTimestamp("modificado"));
				formaPagamento.setNome(resultSet.getString("nome"));
				formaPagamento.setTipo(resultSet.getString("tipo"));
				formaPagamento.setAtivo(resultSet.getBoolean("ativo"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return formaPagamento;
		
	}

	@Override
	public FormaDePagamento cadastrar(FormaDePagamento entity) {
		
		FormaDePagamento formaPagamento = new FormaDePagamento();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into forma_de_pagamento (criado, modificado, nome, tipo, ativo) values (?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getNome());
			ps.setString(4, entity.getTipo());
			ps.setBoolean(5, entity.isAtivo());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				formaPagamento.setId(resultSet.getLong(1));
			}
			
			formaPagamento = buscarPorId(formaPagamento.getId());
			
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

		return formaPagamento;
		
	}

	@Override
	public boolean alterar(FormaDePagamento entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update forma_de_pagamento set modificado=?, nome=?, tipo=?, ativo=? where id = ? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, entity.getNome());
			ps.setString(3, entity.getTipo());
			ps.setBoolean(4, entity.isAtivo());
			ps.setLong(5, entity.getId());

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

		String sql = "DELETE FROM forma_de_pagamento WHERE id = ?;";

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
