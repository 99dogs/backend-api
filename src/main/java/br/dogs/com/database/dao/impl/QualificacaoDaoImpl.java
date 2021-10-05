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
import br.dogs.com.database.dao.QualificacaoDao;
import br.dogs.com.model.entities.Qualificacao;

@Repository
public class QualificacaoDaoImpl implements QualificacaoDao {
	
	Logger logger = LoggerFactory.getLogger(QualificacaoDaoImpl.class);
	
	@Override
	public List<Qualificacao> buscarTodos(Long usuarioId) {
		
		List<Qualificacao> qualificacoes = new ArrayList<Qualificacao>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from qualificacao where usuario_id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, usuarioId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Qualificacao qualificacao = new Qualificacao();
				
				qualificacao.setId(resultSet.getLong("id"));
				qualificacao.setCriado(resultSet.getTimestamp("criado"));
				qualificacao.setModificado(resultSet.getTimestamp("modificado"));
				qualificacao.setTitulo(resultSet.getString("titulo"));
				qualificacao.setModalidade(resultSet.getString("modalidade"));
				qualificacao.setDescricao(resultSet.getString("descricao"));
				qualificacao.setUsuarioId(resultSet.getLong("usuario_id"));
				
				qualificacoes.add(qualificacao);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return qualificacoes;
		
	}

	@Override
	public Qualificacao cadastrar(Qualificacao entity) {
		
		Qualificacao qualificacao = new Qualificacao();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into qualificacao (criado, modificado, titulo, modalidade, descricao, usuario_id) values (?,?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getTitulo());
			ps.setString(4, entity.getModalidade());
			ps.setString(5, entity.getDescricao());
			ps.setLong(6, entity.getUsuarioId());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				qualificacao.setId(resultSet.getLong(1));
			}
			
			qualificacao = buscarPorId(qualificacao.getId());
			
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

		return qualificacao;
		
	}

	@Override
	public Qualificacao buscarPorId(Long id) {
		
		Qualificacao qualificacao = new Qualificacao();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from qualificacao where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				qualificacao.setId(resultSet.getLong("id"));
				qualificacao.setCriado(resultSet.getTimestamp("criado"));
				qualificacao.setModificado(resultSet.getTimestamp("modificado"));
				qualificacao.setTitulo(resultSet.getString("titulo"));
				qualificacao.setModalidade(resultSet.getString("modalidade"));
				qualificacao.setDescricao(resultSet.getString("descricao"));
				qualificacao.setUsuarioId(resultSet.getLong("usuario_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return qualificacao;
		
	}
	
	@Override
	public boolean alterar(Qualificacao entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update qualificacao set modificado=?, titulo=?, modalidade=?, descricao=? where id=? and usuario_id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, entity.getTitulo());
			ps.setString(3, entity.getModalidade());
			ps.setString(4, entity.getDescricao());
			ps.setLong(5, entity.getId());
			ps.setLong(6, entity.getUsuarioId());

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

		String sql = "DELETE FROM qualificacao WHERE id = ?;";

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
