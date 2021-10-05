package br.dogs.com.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.SaldoDao;
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.entities.Saldo;
import br.dogs.com.model.entities.Usuario;

@Repository
public class SaldoDaoImpl implements SaldoDao {
	
	Logger logger = LoggerFactory.getLogger(SaldoDaoImpl.class);
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public boolean creditarSaldo(Saldo saldo) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " insert into saldo (criado,modificado,unitario,depositado,deposito_id,passeio_id,usuario_id) values (?,?,?,?,?,?,?); ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setDouble(3, saldo.getUnitario());
			ps.setBoolean(4, false);
			ps.setLong(5, 0);
			ps.setLong(6, saldo.getPasseioId());
			ps.setLong(7, saldo.getUsuarioId());

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
	public double retornaUltimoSaldo(Long usuarioId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Saldo> buscarTodos(Long usuarioId) {
		
		List<Saldo> saldos = new ArrayList<Saldo>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			Usuario usuario = usuarioDao.buscarPorId(usuarioId);
			
			String sqlConditions = "";
			if(usuario.getTipo() != null) {
				if(!usuario.getTipo().equals("ADMIN")) {
					sqlConditions = " and usuario_id = ? ";
				}
			}
			
			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from saldo where depositado = false  " + sqlConditions;
			
			preparedStatement = connection.prepareStatement(sql);
			if(!sqlConditions.isEmpty()) {
				preparedStatement.setLong(1, usuarioId);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Saldo saldo = new Saldo();
				
				saldo.setId(resultSet.getLong("id"));
				saldo.setCriado(resultSet.getTimestamp("criado"));
				saldo.setModificado(resultSet.getTimestamp("modificado"));
				saldo.setDepositado(resultSet.getBoolean("depositado"));
				saldo.setDepositoId(resultSet.getLong("deposito_id"));
				saldo.setUnitario(resultSet.getDouble("unitario"));
				saldo.setUsuarioId(resultSet.getLong("usuario_id"));
				saldo.setPasseioId(resultSet.getLong("passeio_id"));
				
				saldos.add(saldo);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return saldos;
		
	}

	@Override
	public List<Saldo> buscarPorDeposito(Long depositoId) {
		
		List<Saldo> saldos = new ArrayList<Saldo>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from saldo where deposito_id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, depositoId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Saldo saldo = new Saldo();
				
				saldo.setId(resultSet.getLong("id"));
				saldo.setCriado(resultSet.getTimestamp("criado"));
				saldo.setModificado(resultSet.getTimestamp("modificado"));
				saldo.setDepositado(resultSet.getBoolean("depositado"));
				saldo.setDepositoId(resultSet.getLong("deposito_id"));
				saldo.setUnitario(resultSet.getDouble("unitario"));
				saldo.setUsuarioId(resultSet.getLong("usuario_id"));
				saldo.setPasseioId(resultSet.getLong("passeio_id"));
				
				saldos.add(saldo);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return saldos;
		
	}

}
