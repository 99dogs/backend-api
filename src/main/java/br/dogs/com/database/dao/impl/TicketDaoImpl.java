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
import br.dogs.com.database.dao.TicketDao;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.model.entities.Ticket;

@Repository
public class TicketDaoImpl implements TicketDao {
	
	Logger logger = LoggerFactory.getLogger(TicketDaoImpl.class);
	
	@Override
	public List<Ticket> buscarTodos(Long usuarioId) {
		
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			String sqlWhere = "";
			if(usuarioId != null && usuarioId != 0) {
				sqlWhere += " and t.usuario_id = ? ";
			}
			
			String sql = " select "
					+ " t.id as t_id,"
					+ " t.criado as t_criado,"
					+ " t.modificado as t_modificado,"
					+ " t.quantidade as t_quantidade,"
					+ " t.unitario as t_unitario,"
					+ " t.total as t_total,"
					+ " t.pendente as t_pendente,"
					+ " t.cancelado as t_cancelado,"
					+ " t.pago as t_pago,"
					+ " t.fatura_id as t_fatura_id,"
					+ " t.fatura_url as t_fatura_url,"
					+ " t.forma_de_pagamento_id as t_forma_de_pagamento_id,"
					+ " t.usuario_id as t_usuario_id,"
					+ " fp.nome as fp_nome,"
					+ " fp.tipo as fp_tipo "
					+ " from ticket t join forma_de_pagamento fp on fp.id = t.forma_de_pagamento_id where 1=1 " + sqlWhere;
			
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			
			if(usuarioId != null && usuarioId != 0) {
				preparedStatement.setLong(1, usuarioId);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Ticket ticket = new Ticket();
				FormaDePagamento formaDePagamento = new FormaDePagamento();
				
				ticket.setId(resultSet.getLong("t_id"));
				ticket.setCriado(resultSet.getTimestamp("t_criado"));
				ticket.setModificado(resultSet.getTimestamp("t_modificado"));
				ticket.setQuantidade(resultSet.getInt("t_quantidade"));
				ticket.setUnitario(resultSet.getDouble("t_unitario"));
				ticket.setTotal(resultSet.getDouble("t_total"));
				ticket.setPendente(resultSet.getBoolean("t_pendente"));
				ticket.setCancelado(resultSet.getBoolean("t_cancelado"));
				ticket.setPago(resultSet.getBoolean("t_pago"));
				ticket.setFaturaId(resultSet.getString("t_fatura_id"));
				ticket.setFaturaUrl(resultSet.getString("t_fatura_url"));
				ticket.setFormaDePagamentoId(resultSet.getLong("t_forma_de_pagamento_id"));
				ticket.setUsuarioId(resultSet.getLong("t_usuario_id"));
				
				formaDePagamento.setNome(resultSet.getString("fp_nome"));
				formaDePagamento.setTipo(resultSet.getString("fp_tipo"));
				
				ticket.setFormaDePagamento(formaDePagamento);
				
				tickets.add(ticket);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return tickets;
		
	}
	
	@Override
	public Ticket buscarPorId(Long id) {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaDePagamento = new FormaDePagamento();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
						
			String sql = " select "
					+ " t.id as t_id,"
					+ " t.criado as t_criado,"
					+ " t.modificado as t_modificado,"
					+ " t.quantidade as t_quantidade,"
					+ " t.unitario as t_unitario,"
					+ " t.total as t_total,"
					+ " t.pendente as t_pendente,"
					+ " t.cancelado as t_cancelado,"
					+ " t.pago as t_pago,"
					+ " t.fatura_id as t_fatura_id,"
					+ " t.fatura_url as t_fatura_url,"
					+ " t.forma_de_pagamento_id as t_forma_de_pagamento_id,"
					+ " t.usuario_id as t_usuario_id,"
					+ " fp.criado as fp_criado,"
					+ " fp.modificado as fp_modificado,"
					+ " fp.nome as fp_nome,"
					+ " fp.tipo as fp_tipo,"
					+ " fp.ativo as fp_ativo "
					+ " from ticket t join forma_de_pagamento fp on fp.id = t.forma_de_pagamento_id where 1=1 and t.id = ? ";
			
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				ticket.setId(resultSet.getLong("t_id"));
				ticket.setCriado(resultSet.getTimestamp("t_criado"));
				ticket.setModificado(resultSet.getTimestamp("t_modificado"));
				ticket.setQuantidade(resultSet.getInt("t_quantidade"));
				ticket.setUnitario(resultSet.getDouble("t_unitario"));
				ticket.setTotal(resultSet.getDouble("t_total"));
				ticket.setPendente(resultSet.getBoolean("t_pendente"));
				ticket.setCancelado(resultSet.getBoolean("t_cancelado"));
				ticket.setPago(resultSet.getBoolean("t_pago"));
				ticket.setFaturaId(resultSet.getString("t_fatura_id"));
				ticket.setFaturaUrl(resultSet.getString("t_fatura_url"));
				ticket.setFormaDePagamentoId(resultSet.getLong("t_forma_de_pagamento_id"));
				ticket.setUsuarioId(resultSet.getLong("t_usuario_id"));
				
				formaDePagamento.setId(resultSet.getLong("t_forma_de_pagamento_id"));
				formaDePagamento.setCriado(resultSet.getTimestamp("fp_criado"));
				formaDePagamento.setModificado(resultSet.getTimestamp("fp_modificado"));
				formaDePagamento.setNome(resultSet.getString("fp_nome"));
				formaDePagamento.setTipo(resultSet.getString("fp_tipo"));
				formaDePagamento.setAtivo(resultSet.getBoolean("fp_ativo"));
				
				ticket.setFormaDePagamento(formaDePagamento);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return ticket;
		
	}
	
	@Override
	public Ticket cadastrar(Ticket entity) {
		
		Ticket ticket = new Ticket();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into ticket (criado,modificado,quantidade,unitario,total,pendente,cancelado,pago,fatura_id,fatura_url,forma_de_pagamento_id,usuario_id) "
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?) ";
				
		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setInt(3, entity.getQuantidade());
			ps.setDouble(4, entity.getUnitario());
			ps.setDouble(5, entity.getTotal());
			ps.setBoolean(6, entity.isPendente());
			ps.setBoolean(7, entity.isCancelado());
			ps.setBoolean(8, entity.isPago());
			ps.setString(9, entity.getFaturaId());
			ps.setString(10, entity.getFaturaUrl());
			ps.setLong(11, entity.getFormaDePagamentoId());
			ps.setLong(12, entity.getUsuarioId());
			
			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				ticket.setId(resultSet.getLong(1));
			}
			
			ticket = buscarPorId(ticket.getId());
			
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

		return ticket;
		
	}

	@Override
	public boolean deletarPorId(Long id) {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = "DELETE FROM ticket WHERE id = ?;";

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

	@Override
	public boolean alterar(Ticket entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update ticket set modificado=?,quantidade=?,unitario=?,total=?,pendente=?,cancelado=?,pago=?,fatura_id=?,fatura_url=?,forma_de_pagamento_id=? where id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setDouble(2, entity.getQuantidade());
			ps.setDouble(3, entity.getUnitario());
			ps.setDouble(4, entity.getTotal());
			ps.setBoolean(5, entity.isPendente());
			ps.setBoolean(6, entity.isCancelado());
			ps.setBoolean(7, entity.isPago());
			ps.setString(8, entity.getFaturaId());
			ps.setString(9, entity.getFaturaUrl());
			ps.setLong(10, entity.getFormaDePagamentoId());
			ps.setLong(11, entity.getId());

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
	public Ticket buscarPorFaturaId(String faturaId) {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaDePagamento = new FormaDePagamento();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
						
			String sql = " select "
					+ " t.id as t_id,"
					+ " t.criado as t_criado,"
					+ " t.modificado as t_modificado,"
					+ " t.quantidade as t_quantidade,"
					+ " t.unitario as t_unitario,"
					+ " t.total as t_total,"
					+ " t.pendente as t_pendente,"
					+ " t.cancelado as t_cancelado,"
					+ " t.pago as t_pago,"
					+ " t.fatura_id as t_fatura_id,"
					+ " t.fatura_url as t_fatura_url,"
					+ " t.forma_de_pagamento_id as t_forma_de_pagamento_id,"
					+ " t.usuario_id as t_usuario_id,"
					+ " fp.criado as fp_criado,"
					+ " fp.modificado as fp_modificado,"
					+ " fp.nome as fp_nome,"
					+ " fp.tipo as fp_tipo,"
					+ " fp.ativo as fp_ativo "
					+ " from ticket t join forma_de_pagamento fp on fp.id = t.forma_de_pagamento_id where 1=1 and t.fatura_id = ? ";
			
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, faturaId);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				ticket.setId(resultSet.getLong("t_id"));
				ticket.setCriado(resultSet.getTimestamp("t_criado"));
				ticket.setModificado(resultSet.getTimestamp("t_modificado"));
				ticket.setQuantidade(resultSet.getInt("t_quantidade"));
				ticket.setUnitario(resultSet.getDouble("t_unitario"));
				ticket.setTotal(resultSet.getDouble("t_total"));
				ticket.setPendente(resultSet.getBoolean("t_pendente"));
				ticket.setCancelado(resultSet.getBoolean("t_cancelado"));
				ticket.setPago(resultSet.getBoolean("t_pago"));
				ticket.setFaturaId(resultSet.getString("t_fatura_id"));
				ticket.setFaturaUrl(resultSet.getString("t_fatura_url"));
				ticket.setFormaDePagamentoId(resultSet.getLong("t_forma_de_pagamento_id"));
				ticket.setUsuarioId(resultSet.getLong("t_usuario_id"));
				
				formaDePagamento.setId(resultSet.getLong("t_forma_de_pagamento_id"));
				formaDePagamento.setCriado(resultSet.getTimestamp("fp_criado"));
				formaDePagamento.setModificado(resultSet.getTimestamp("fp_modificado"));
				formaDePagamento.setNome(resultSet.getString("fp_nome"));
				formaDePagamento.setTipo(resultSet.getString("fp_tipo"));
				formaDePagamento.setAtivo(resultSet.getBoolean("fp_ativo"));
				
				ticket.setFormaDePagamento(formaDePagamento);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return ticket;
		
	}

	@Override
	public boolean setarComoPendente(Long id) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update ticket set modificado=?,pendente=?,pago=?,cancelado=? where id=? ";

		try {
			
			boolean pendente = true;
			boolean pago = false;
			boolean cancelado = false;
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setBoolean(2, pendente);
			ps.setBoolean(3, pago);
			ps.setBoolean(4, cancelado);
			ps.setLong(5, id);
			
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
	public boolean setarComoPago(Long id) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update ticket set modificado=?,pendente=?,pago=?,cancelado=? where id=? ";

		try {
			
			boolean pendente = false;
			boolean pago = true;
			boolean cancelado = false;
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setBoolean(2, pendente);
			ps.setBoolean(3, pago);
			ps.setBoolean(4, cancelado);
			ps.setLong(5, id);
			
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
	public boolean setarComoCancelado(Long id) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update ticket set modificado=?,pendente=?,pago=?,cancelado=? where id=? ";

		try {
			
			boolean pendente = false;
			boolean pago = false;
			boolean cancelado = true;
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setBoolean(2, pendente);
			ps.setBoolean(3, pago);
			ps.setBoolean(4, cancelado);
			ps.setLong(5, id);
			
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
	public boolean creditarComprador(Long id) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update usuario set qtde_ticket_disponivel = "
				+ "	( "
				+ "		qtde_ticket_disponivel + "
				+ "		( "
				+ "			select t.quantidade from ticket t "
				+ "			join usuario u on u.id = t.usuario_id "
				+ "			where t.id = ? and t.usuario_id = ? "
				+ "		) "
				+ "	) where id = ?; ";

		try {
			
			Ticket ticket = buscarPorId(id);
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setLong(1, id);
			ps.setLong(2, ticket.getUsuarioId());
			ps.setLong(3, ticket.getUsuarioId());
			
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
	public boolean debitarComprador(Long tutorId) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update usuario set qtde_ticket_disponivel = (select (qtde_ticket_disponivel)-1 from usuario where id = ?) where id = ? ";

		try {
						
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setLong(1, tutorId);
			ps.setLong(2, tutorId);
			
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
	
}
