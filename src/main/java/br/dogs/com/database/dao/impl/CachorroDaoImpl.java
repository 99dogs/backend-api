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
import br.dogs.com.database.dao.CachorroDao;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Porte;
import br.dogs.com.model.entities.Raca;

@Repository
public class CachorroDaoImpl implements CachorroDao {
	
	Logger logger = LoggerFactory.getLogger(CachorroDaoImpl.class);
	
	@Override
	public Cachorro cadastrar(Cachorro entity) {
		
		Cachorro cachorro = new Cachorro();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into cachorro (criado, modificado, nome, data_nascimento, comportamento, raca_id, porte_id, usuario_id) values (?,?,?,?,?,?,?,?) ";
				
		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getNome());
			ps.setDate(4, entity.getDataNascimento());
			ps.setString(5, entity.getComportamento());
			ps.setLong(6, entity.getRacaId());
			ps.setLong(7, entity.getPorteId());
			ps.setLong(8, entity.getUsuarioId());
			
			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				cachorro.setId(resultSet.getLong(1));
			}
			
			cachorro = buscarPorId(cachorro.getId());
			
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

		return cachorro;
		
	}

	@Override
	public Cachorro buscarPorId(Long id) {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select "
					+ " c.id as c_id, "
					+ " c.criado as c_criado,"
					+ " c.modificado as c_modificado, "
					+ " c.nome as c_nome, "
					+ " c.comportamento as c_comportamento, "
					+ " c.data_nascimento as c_data_nascimento,"
					+ " c.usuario_id as c_usuario_id,"
					+ " p.id as p_id,"
					+ " p.criado as p_criado,"
					+ " p.modificado as p_modificado,"
					+ " p.nome as p_nome,"
					+ " r.id as r_id,"
					+ "	r.criado as r_criado,"
					+ " r.modificado as r_modificado,"
					+ " r.nome as r_nome "
					+ " from cachorro c "
					+ "join raca r on r.id = c.raca_id "
					+ "join porte p on p.id = c.porte_id "
					+ "where c.id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				cachorro.setId(resultSet.getLong("c_id"));
				cachorro.setCriado(resultSet.getTimestamp("c_criado"));
				cachorro.setModificado(resultSet.getTimestamp("c_modificado"));
				cachorro.setDataNascimento(resultSet.getDate("c_data_nascimento"));
				cachorro.setNome(resultSet.getString("c_nome"));
				cachorro.setComportamento(resultSet.getString("c_comportamento"));
				cachorro.setUsuarioId(resultSet.getLong("c_usuario_id"));
				cachorro.setRacaId(resultSet.getLong("p_id"));
				cachorro.setPorteId(resultSet.getLong("r_id"));
				
				raca.setId(resultSet.getLong("r_id"));
				raca.setCriado(resultSet.getTimestamp("r_criado"));
				raca.setModificado(resultSet.getTimestamp("r_modificado"));
				raca.setNome(resultSet.getString("r_nome"));
				
				porte.setId(resultSet.getLong("p_id"));
				porte.setCriado(resultSet.getTimestamp("p_criado"));
				porte.setModificado(resultSet.getTimestamp("p_modificado"));
				porte.setNome(resultSet.getString("p_nome"));
				
				cachorro.setRaca(raca);
				cachorro.setPorte(porte);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return cachorro;
		
	}

	@Override
	public List<Cachorro> buscarTodos(Long usuarioId) {
		
		List<Cachorro> cachorros = new ArrayList<Cachorro>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			String sqlWhere = "";
			if(usuarioId != null && usuarioId != 0) {
				sqlWhere += " and c.usuario_id = ? ";
			}
			
			String sql = " select "
					+ " c.id as c_id, "
					+ " c.criado as c_criado,"
					+ " c.modificado as c_modificado, "
					+ " c.nome as c_nome, "
					+ " c.comportamento as c_comportamento, "
					+ " c.data_nascimento as c_data_nascimento,"
					+ " c.usuario_id as c_usuario_id,"
					+ " p.id as p_id,"
					+ " p.criado as p_criado,"
					+ " p.modificado as p_modificado,"
					+ " p.nome as p_nome,"
					+ " r.id as r_id,"
					+ "	r.criado as r_criado,"
					+ " r.modificado as r_modificado,"
					+ " r.nome as r_nome "
					+ " from cachorro c "
					+ "join raca r on r.id = c.raca_id "
					+ "join porte p on p.id = c.porte_id "
					+ "where 1=1 " + sqlWhere;
			
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			
			if(usuarioId != null && usuarioId != 0) {
				preparedStatement.setLong(1, usuarioId);
			}
			
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Cachorro cachorro = new Cachorro();
				Porte porte = new Porte();
				Raca raca = new Raca();
				
				cachorro.setId(resultSet.getLong("c_id"));
				cachorro.setCriado(resultSet.getTimestamp("c_criado"));
				cachorro.setModificado(resultSet.getTimestamp("c_modificado"));
				cachorro.setDataNascimento(resultSet.getDate("c_data_nascimento"));
				cachorro.setNome(resultSet.getString("c_nome"));
				cachorro.setComportamento(resultSet.getString("c_comportamento"));
				cachorro.setUsuarioId(resultSet.getLong("c_usuario_id"));
				cachorro.setRacaId(resultSet.getLong("p_id"));
				cachorro.setPorteId(resultSet.getLong("r_id"));
				
				raca.setId(resultSet.getLong("r_id"));
				raca.setCriado(resultSet.getTimestamp("r_criado"));
				raca.setModificado(resultSet.getTimestamp("r_modificado"));
				raca.setNome(resultSet.getString("r_nome"));
				
				porte.setId(resultSet.getLong("p_id"));
				porte.setCriado(resultSet.getTimestamp("p_criado"));
				porte.setModificado(resultSet.getTimestamp("p_modificado"));
				porte.setNome(resultSet.getString("p_nome"));
				
				cachorro.setRaca(raca);
				cachorro.setPorte(porte);
				
				cachorros.add(cachorro);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return cachorros;
		
	}

	@Override
	public boolean alterar(Cachorro entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update cachorro set modificado = ?, nome = ?, data_nascimento = ?, comportamento = ?, raca_id = ?, porte_id = ? where id = ? and usuario_id = ? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, entity.getNome());
			ps.setDate(3, entity.getDataNascimento());
			ps.setString(4, entity.getComportamento());
			ps.setLong(5, entity.getRacaId());
			ps.setLong(6, entity.getPorteId());
			ps.setLong(7, entity.getId());
			ps.setLong(8, entity.getUsuarioId());

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

		String sql = "DELETE FROM cachorro WHERE id = ?;";

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
