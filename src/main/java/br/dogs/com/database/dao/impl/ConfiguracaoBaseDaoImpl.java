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
import br.dogs.com.database.dao.ConfiguracaoBaseDao;
import br.dogs.com.model.entities.ConfiguracaoBase;

@Repository
public class ConfiguracaoBaseDaoImpl implements ConfiguracaoBaseDao {
	
	Logger logger = LoggerFactory.getLogger(ConfiguracaoBaseDaoImpl.class);
	
	@Override
	public List<ConfiguracaoBase> buscarTodos() {
		
		List<ConfiguracaoBase> configuracoes = new ArrayList<ConfiguracaoBase>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from configuracao_base ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				ConfiguracaoBase configuracao = new ConfiguracaoBase();
				
				configuracao.setId(resultSet.getLong("id"));
				configuracao.setCriado(resultSet.getTimestamp("criado"));
				configuracao.setModificado(resultSet.getTimestamp("modificado"));
				configuracao.setValorTicket(resultSet.getDouble("valor_ticket"));
				configuracao.setTaxaPlataforma(resultSet.getDouble("taxa_plataforma"));
				configuracao.setValorMinimoDeposito(resultSet.getDouble("valor_minimo_deposito"));
				configuracao.setTokenGateway(resultSet.getString("token_gateway"));
				configuracao.setTempoPasseio(resultSet.getInt("tempo_passeio"));
				
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
	public ConfiguracaoBase cadastrar(ConfiguracaoBase entity) {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into configuracao_base (criado, modificado, valor_ticket, taxa_plataforma, valor_minimo_deposito, token_gateway, tempo_passeio) values (?,?,?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setDouble(3, entity.getValorTicket());
			ps.setDouble(4, entity.getTaxaPlataforma());
			ps.setDouble(5, entity.getValorMinimoDeposito());
			ps.setString(6, entity.getTokenGateway());
			ps.setInt(7, entity.getTempoPasseio());

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
	public ConfiguracaoBase buscarPorId(Long id) {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from configuracao_base where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				configuracao.setId(resultSet.getLong("id"));
				configuracao.setCriado(resultSet.getTimestamp("criado"));
				configuracao.setModificado(resultSet.getTimestamp("modificado"));
				configuracao.setValorTicket(resultSet.getDouble("valor_ticket"));
				configuracao.setTaxaPlataforma(resultSet.getDouble("taxa_plataforma"));
				configuracao.setValorMinimoDeposito(resultSet.getDouble("valor_minimo_deposito"));
				configuracao.setTokenGateway(resultSet.getString("token_gateway"));
				configuracao.setTempoPasseio(resultSet.getInt("tempo_passeio"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return configuracao;
		
	}

	@Override
	public boolean alterar(ConfiguracaoBase entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update configuracao_base set modificado=?, valor_ticket=?, taxa_plataforma=?, valor_minimo_deposito=?, token_gateway=?, tempo_passeio=? where id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setDouble(2, entity.getValorTicket());
			ps.setDouble(3, entity.getTaxaPlataforma());
			ps.setDouble(4, entity.getValorMinimoDeposito());
			ps.setString(5, entity.getTokenGateway());
			ps.setInt(6, entity.getTempoPasseio());
			ps.setLong(7, entity.getId());

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

		String sql = "DELETE FROM configuracao_base WHERE id = ?;";

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
