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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.DepositoDao;
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.entities.Deposito;
import br.dogs.com.model.entities.Usuario;

@Repository
public class DepositoDaoImpl implements DepositoDao {
	
	Logger logger = LoggerFactory.getLogger(DepositoDaoImpl.class);
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public Deposito solicitar(Long dogwalkerId) {
		
		Deposito deposito = new Deposito();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into deposito (criado,modificado,valor,pendente,concluido,usuario_id) "
				+ " values (?,?,(select sum(s.unitario) from saldo s where s.usuario_id = ? and s.depositado = false),true,false,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setLong(3, dogwalkerId);
			ps.setLong(4, dogwalkerId);

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				deposito.setId(resultSet.getLong(1));
			}
			
			if(deposito.getId() != null && deposito.getId() != 0) {
				
				deposito = buscarPorId(deposito.getId());
				atualizarSaldo(dogwalkerId, deposito.getId());
				
			}
			
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
		
		return deposito;
		
	}
	
	private boolean atualizarSaldo(Long dogwalkerId, Long depositoId) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update saldo set deposito_id = ?, depositado = true where usuario_id = ? and depositado = false and deposito_id = 0 ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setLong(1, depositoId);
			ps.setLong(2, dogwalkerId);

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
	public Deposito buscarPorId(Long id) {
		
		Deposito deposito = new Deposito();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from deposito where id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				deposito.setId(resultSet.getLong("id"));
				deposito.setCriado(resultSet.getTimestamp("criado"));
				deposito.setModificado(resultSet.getTimestamp("modificado"));
				deposito.setValor(resultSet.getDouble("valor"));
				deposito.setPendente(resultSet.getBoolean("pendente"));
				deposito.setConcluido(resultSet.getBoolean("concluido"));
				deposito.setUsuarioId(resultSet.getLong("usuario_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}
		
		return deposito;
		
	}

	@Override
	public List<Deposito> buscarTodos(Long usuarioId) {
		
		List<Deposito> depositos = new ArrayList<Deposito>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			Usuario usuario = usuarioDao.buscarPorId(usuarioId);
			
			String sqlConditions = "";
			if(!usuario.getTipo().equals(TipoUsuario.ADMIN.toString())) {
				sqlConditions = " and usuario_id = ? ";
			}
			
			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from deposito where 1=1 " + sqlConditions;
			
			preparedStatement = connection.prepareStatement(sql);
			if(!usuario.getTipo().equals(TipoUsuario.ADMIN.toString())) {
				preparedStatement.setLong(1, usuarioId);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Deposito deposito = new Deposito();
				
				deposito.setId(resultSet.getLong("id"));
				deposito.setCriado(resultSet.getTimestamp("criado"));
				deposito.setModificado(resultSet.getTimestamp("modificado"));
				deposito.setValor(resultSet.getDouble("valor"));
				deposito.setConcluido(resultSet.getBoolean("concluido"));
				deposito.setPendente(resultSet.getBoolean("pendente"));
				deposito.setUsuarioId(resultSet.getLong("usuario_id"));
				
				depositos.add(deposito);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return depositos;
		
	}
	
}
