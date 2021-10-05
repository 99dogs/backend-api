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
import br.dogs.com.database.dao.ReclamacaoSugestaoDao;
import br.dogs.com.model.entities.ReclamacaoSugestao;
import io.swagger.annotations.ApiOperation;

@Repository
public class ReclamacaoSugestaoDaoImpl implements ReclamacaoSugestaoDao {
	
	Logger logger = LoggerFactory.getLogger(ReclamacaoSugestaoDaoImpl.class);
	
	@ApiOperation("Retorna a lista de reclamação/sugestão na qual o usuário cadastrou na plataforma.")
	@Override
	public List<ReclamacaoSugestao> bsucarTodos(Long usuarioId) {
		
		List<ReclamacaoSugestao> reclamacaoSugestaoList = new ArrayList<ReclamacaoSugestao>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from reclamacao_sugestao where usuario_id = ?";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, usuarioId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				ReclamacaoSugestao reclamacaoSugestao = new ReclamacaoSugestao();
				
				reclamacaoSugestao.setId(resultSet.getLong("id"));
				reclamacaoSugestao.setCriado(resultSet.getTimestamp("criado"));
				reclamacaoSugestao.setModificado(resultSet.getTimestamp("modificado"));
				reclamacaoSugestao.setAssunto(resultSet.getString("assunto"));
				reclamacaoSugestao.setMensagem(resultSet.getString("mensagem"));
				reclamacaoSugestao.setUsuarioId(resultSet.getLong("usuario_id"));
				
				reclamacaoSugestaoList.add(reclamacaoSugestao);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return reclamacaoSugestaoList;
		
	}

	@Override
	public ReclamacaoSugestao cadastrar(ReclamacaoSugestao entity) {
		
		ReclamacaoSugestao reclamacaoSugestao = new ReclamacaoSugestao();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into reclamacao_sugestao (criado, modificado, assunto, mensagem, usuario_id) values (?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getAssunto());
			ps.setString(4, entity.getMensagem());
			ps.setLong(5, entity.getUsuarioId());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				reclamacaoSugestao.setId(resultSet.getLong(1));
			}
			
			reclamacaoSugestao = buscarPorId(reclamacaoSugestao.getId());
			
		} catch (Exception e) {

			logger.error(e.getMessage());

			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}

		} finally {
			ConnectionFactory.close(resultSet, ps, connection);
		}

		return reclamacaoSugestao;
		
	}

	@Override
	public ReclamacaoSugestao buscarPorId(Long id) {
		
		ReclamacaoSugestao reclamacaoSugestao = new ReclamacaoSugestao();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from reclamacao_sugestao where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				reclamacaoSugestao.setId(resultSet.getLong("id"));
				reclamacaoSugestao.setCriado(resultSet.getTimestamp("criado"));
				reclamacaoSugestao.setModificado(resultSet.getTimestamp("modificado"));
				reclamacaoSugestao.setAssunto(resultSet.getString("assunto"));
				reclamacaoSugestao.setMensagem(resultSet.getString("mensagem"));
				reclamacaoSugestao.setUsuarioId(resultSet.getLong("usuario_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return reclamacaoSugestao;
		
	}

	@Override
	public boolean deletarPorId(Long id) {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = "DELETE FROM reclamacao_sugestao WHERE id = ?;";

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
