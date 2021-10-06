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
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Usuario;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

	Logger logger = LoggerFactory.getLogger(UsuarioDaoImpl.class);

	@Override
	public UsuarioAutenticado registrar(UsuarioRegistro entity) {

		UsuarioAutenticado response = new UsuarioAutenticado();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = "insert into usuario (criado, modificado, nome, email, senha, tipo, cidade_id, estado_id,foto_url)";
		sql += " VALUES (?,?,?,?,?,?,?,?,?); ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getNome());
			ps.setString(4, entity.getEmail().toLowerCase());
			ps.setString(5, entity.getSenha());
			ps.setString(6, entity.getTipo().toUpperCase());
			ps.setInt(7, 2225);
			ps.setInt(8, 13);
			ps.setString(9, "");

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				response.setId(resultSet.getLong(1));
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

		return response;

	}
	
	@Override
	public Usuario buscarPorId(Long id) {

		Usuario usuario = new Usuario();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		try {
			connection = ConnectionFactory.getConnection();

			String sql = " select "
					+ " u.*,"
					+ " est.nome as est_nome, est.ativo as est_ativo, est.sigla as est_sigla,"
					+ " cid.nome as cid_nome, cid.ativo as cid_ativo "
					+ " from usuario u "
					+ " join cidade cid on cid.id = u.cidade_id"
					+ " join estado est on est.id = u.estado_id "
					+ " where u.id = ? ";

			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {
				
				Estado estado = new Estado();
				Cidade cidade = new Cidade();
				
				usuario.setId(resultSet.getLong("id"));
				usuario.setCriado(resultSet.getTimestamp("criado"));
				usuario.setModificado(resultSet.getTimestamp("modificado"));
				usuario.setNome(resultSet.getString("nome"));
				usuario.setEmail(resultSet.getString("email"));
				usuario.setTelefone(resultSet.getString("telefone"));
				usuario.setRua(resultSet.getString("rua"));
				usuario.setBairro(resultSet.getString("bairro"));
				usuario.setNumero(resultSet.getString("numero"));
				usuario.setAvaliacao(resultSet.getDouble("avaliacao"));
				usuario.setTelefone(resultSet.getString("telefone"));
				usuario.setTipo(resultSet.getString("tipo"));
				usuario.setCep(resultSet.getString("cep"));
				usuario.setQtdeTicketDisponivel(resultSet.getInt("qtde_ticket_disponivel"));
				usuario.setFotoUrl(resultSet.getString("foto_url"));
				
				estado.setId(resultSet.getLong("estado_id"));
				estado.setNome(resultSet.getString("est_nome"));
				estado.setSigla(resultSet.getString("est_sigla"));
				estado.setAtivo(resultSet.getBoolean("est_ativo"));
				
				cidade.setId(resultSet.getLong("cidade_id"));
				cidade.setNome(resultSet.getString("cid_nome"));
				cidade.setAtivo(resultSet.getBoolean("cid_ativo"));
				
				usuario.setEstado(estado);
				usuario.setCidade(cidade);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, ps, connection);
		}
		
		return usuario;
		
	}
	
	@Override
	public Usuario buscarPorEmail(String email) {

		Usuario usuario = new Usuario();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		try {
			connection = ConnectionFactory.getConnection();

			String sql = " select id,nome,email,senha,tipo from usuario where email = ? ";

			ps = connection.prepareStatement(sql);
			ps.setString(1, email);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {

				usuario.setId(resultSet.getLong("id"));
				usuario.setNome(resultSet.getString("nome"));
				usuario.setEmail(resultSet.getString("email"));
				usuario.setSenha(resultSet.getString("senha"));
				usuario.setTipo(resultSet.getString("tipo"));

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, ps, connection);
		}

		return usuario;

	}

	@Override
	public List<Usuario> buscarDogwalkers() {
		return this.usuarioByTipo("DOGWALKER");
	}

	@Override
	public List<Usuario> buscarTutores() {
		return this.usuarioByTipo("TUTOR");
	}
	
	@Override
	public List<Usuario> buscarTodos() {
		return this.usuarioByTipo(null);
	}
	
	private List<Usuario> usuarioByTipo(String tipo) {
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sqlWhere = " 1=1 ";
			
			if(tipo != null && !tipo.isEmpty()) {
				sqlWhere += " and u.tipo = ?";
			}
			
			String order = "";
			if(tipo == "DOGWALKER") {
				order += " order by u.avaliacao desc ";
			}
			
			String sql = " select "
					+ "	u.*,"
					+ "	cid.id as cid_id,"
					+ "	cid.nome as cid_nome,"
					+ "	est.id as est_id,"
					+ "	est.nome as est_nome,"
					+ "	est.sigla as est_sigla"
					+ "	from usuario u "
					+ "	join cidade cid on cid.id = u.cidade_id "
					+ "	join estado est on est.id = u.estado_id "
					+ "	where " + sqlWhere + order;

			preparedStatement = connection.prepareStatement(sql);
			
			if(sqlWhere != " 1=1 ") {
				preparedStatement.setString(1, tipo);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Usuario usuario = new Usuario();
				Cidade cidade = new Cidade();
				Estado estado = new Estado();
				
				usuario.setId(resultSet.getLong("id"));
				usuario.setCriado(resultSet.getTimestamp("criado"));
				usuario.setModificado(resultSet.getTimestamp("modificado"));
				usuario.setNome(resultSet.getString("nome"));
				usuario.setEmail(resultSet.getString("email"));
				usuario.setTelefone(resultSet.getString("telefone"));
				usuario.setRua(resultSet.getString("rua"));
				usuario.setBairro(resultSet.getString("bairro"));
				usuario.setNumero(resultSet.getString("numero"));
				usuario.setAvaliacao(resultSet.getDouble("avaliacao"));
				usuario.setTipo(resultSet.getString("tipo"));
				usuario.setCep(resultSet.getString("cep"));
				usuario.setQtdeTicketDisponivel(resultSet.getInt("qtde_ticket_disponivel"));
				usuario.setFotoUrl(resultSet.getString("foto_url"));
				
				estado.setId(resultSet.getLong("est_id"));
				estado.setNome(resultSet.getString("est_nome"));
				estado.setSigla(resultSet.getString("est_sigla"));
				
				cidade.setId(resultSet.getLong("cid_id"));
				cidade.setNome(resultSet.getString("cid_nome"));
				
				usuario.setEstado(estado);
				usuario.setCidade(cidade);
				
				usuarios.add(usuario);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return usuarios;

	}

	@Override
	public boolean alterarDados(UsuarioAlterarDados dados) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update usuario set modificado =?, nome=?, telefone=?, rua=?, bairro=?,numero=?, estado_id=?, cidade_id=?,cep=? where id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, dados.getNome());
			ps.setString(3, dados.getTelefone());
			ps.setString(4, dados.getRua());
			ps.setString(5, dados.getBairro());
			ps.setString(6, dados.getNumero());
			ps.setLong(7, dados.getEstadoId());
			ps.setLong(8, dados.getCidadeId());
			ps.setString(9, dados.getCep());
			ps.setLong(10, dados.getUsuarioId());

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
	public Usuario buscarPorEmailAndSocialId(String email, String socialId) {
		
		Usuario usuario = new Usuario();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		try {
			connection = ConnectionFactory.getConnection();

			String sql = " select id,nome,email,senha,tipo from usuario where email = ? and social_id = ?";

			ps = connection.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, socialId);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {

				usuario.setId(resultSet.getLong("id"));
				usuario.setNome(resultSet.getString("nome"));
				usuario.setEmail(resultSet.getString("email"));
				usuario.setSenha(resultSet.getString("senha"));
				usuario.setTipo(resultSet.getString("tipo"));

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, ps, connection);
		}

		return usuario;
		
	}

	@Override
	public UsuarioAutenticado registrarSocialLogin(UsuarioSocialLogin entity) {
		
		UsuarioAutenticado response = new UsuarioAutenticado();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = "insert into usuario (criado, modificado, nome, email, tipo, foto_url, social_id, cidade_id, estado_id)";
		sql += " VALUES (?,?,?,?,?,?,?,?,?); ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, entity.getNome());
			ps.setString(4, entity.getEmail().toLowerCase());
			ps.setString(5, entity.getTipo().toUpperCase());
			ps.setString(6, entity.getFotoUrl());
			ps.setString(7, entity.getSocialId());
			ps.setInt(8, 2225);
			ps.setInt(9, 13);

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				response.setId(resultSet.getLong(1));
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

		return response;
		
	}

	@Override
	public boolean atualizarFoto(Long id, String url) {
		
		Connection connection = null;
		PreparedStatement ps = null;

		String sql = " update usuario set modificado=?, foto_url=? where id=? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, url);
			ps.setLong(3, id);

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
	public Long retornaQtdePasseiosEfetuados(Long id) {
		
		Long quantidade = (long) 0;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		try {
			connection = ConnectionFactory.getConnection();

			String sql = " select count(*) as qtde from passeio where dogwalker_id = ? and status = 'Finalizado' ";

			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {
				
				quantidade = resultSet.getLong("qtde");
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, ps, connection);
		}
		
		return quantidade;
		
	}
	
}
