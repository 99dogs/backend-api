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
import br.dogs.com.database.dao.ConfiguracaoHorarioDao;
import br.dogs.com.model.entities.ConfiguracaoHorario;

@Repository
public class ConfiguracaoHorarioDaoImpl implements ConfiguracaoHorarioDao {
	
	Logger logger = LoggerFactory.getLogger(ConfiguracaoHorarioDaoImpl.class);
	
	@Override
	public List<ConfiguracaoHorario> buscarTodos(Long usuarioId) {
		
		List<ConfiguracaoHorario> configuracoes = new ArrayList<ConfiguracaoHorario>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from configuracao_horario where usuario_id = ? order by dia_semana asc ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, usuarioId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				ConfiguracaoHorario configuracao = new ConfiguracaoHorario();
				
				configuracao.setId(resultSet.getLong("id"));
				configuracao.setCriado(resultSet.getTimestamp("criado"));
				configuracao.setModificado(resultSet.getTimestamp("modificado"));
				configuracao.setDiaSemana(resultSet.getInt("dia_semana"));
				configuracao.setHoraInicio(resultSet.getTime("hora_inicio"));
				configuracao.setHoraFinal(resultSet.getTime("hora_final"));
				configuracao.setUsuarioId(resultSet.getLong("usuario_id"));
				
				configuracoes.add(configuracao);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return configuracoes;
		
	}

	@Override
	public ConfiguracaoHorario buscarPorId(Long id) {
		
		ConfiguracaoHorario configuracao = new ConfiguracaoHorario();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from configuracao_horario where id=? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				configuracao.setId(resultSet.getLong("id"));
				configuracao.setCriado(resultSet.getTimestamp("criado"));
				configuracao.setModificado(resultSet.getTimestamp("modificado"));
				configuracao.setDiaSemana(resultSet.getInt("dia_semana"));
				configuracao.setHoraInicio(resultSet.getTime("hora_inicio"));
				configuracao.setHoraFinal(resultSet.getTime("hora_final"));
				configuracao.setUsuarioId(resultSet.getLong("usuario_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return configuracao;
		
	}

	@Override
	public ConfiguracaoHorario cadastrar(ConfiguracaoHorario entity) {
		
		ConfiguracaoHorario configuracao = new ConfiguracaoHorario();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into configuracao_horario (criado, modificado, dia_semana, hora_inicio, hora_final, usuario_id) values (?,?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setInt(3, entity.getDiaSemana());
			ps.setTime(4, entity.getHoraInicio());
			ps.setTime(5, entity.getHoraFinal());
			ps.setLong(6, entity.getUsuarioId());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				configuracao.setId(resultSet.getLong(1));
			}
			
			configuracao = buscarPorId(configuracao.getId());
			
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

		return configuracao;
		
	}

	@Override
	public ConfiguracaoHorario buscarPorDiaSemana(int diaSemana, Long usuarioId) {
		
		ConfiguracaoHorario configuracao = new ConfiguracaoHorario();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from configuracao_horario where dia_semana=? and usuario_id=? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, diaSemana);
			preparedStatement.setLong(2, usuarioId);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				configuracao.setId(resultSet.getLong("id"));
				configuracao.setCriado(resultSet.getTimestamp("criado"));
				configuracao.setModificado(resultSet.getTimestamp("modificado"));
				configuracao.setDiaSemana(resultSet.getInt("dia_semana"));
				configuracao.setHoraInicio(resultSet.getTime("hora_inicio"));
				configuracao.setHoraFinal(resultSet.getTime("hora_final"));
				configuracao.setUsuarioId(resultSet.getLong("usuario_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return configuracao;
		
	}

	@Override
	public boolean alterar(ConfiguracaoHorario entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update configuracao_horario set modificado=?, dia_semana=?, hora_inicio=?, hora_final=? where id=? and usuario_id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setInt(2, entity.getDiaSemana());
			ps.setTime(3, entity.getHoraInicio());
			ps.setTime(4, entity.getHoraFinal());
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

		String sql = "DELETE FROM configuracao_horario WHERE id = ?;";

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
