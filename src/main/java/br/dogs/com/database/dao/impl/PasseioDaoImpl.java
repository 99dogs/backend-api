package br.dogs.com.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.CachorroDao;
import br.dogs.com.database.dao.PasseioDao;
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;

@Repository
public class PasseioDaoImpl implements PasseioDao {
	
	Logger logger = LoggerFactory.getLogger(PasseioDaoImpl.class);
	
	@Autowired
	private CachorroDao cachorroDao;
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public Passeio solicitar(Passeio entity) {
				
		Passeio passeio = new Passeio();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into passeio (criado, modificado, datahora, previsao_termino, status, dogwalker_id, tutor_id) values (?,?,?,?,?,?,?) ";
				
		try {
			
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(new Date(Timestamp.valueOf(entity.getDatahora()).getTime()));
		    calendar.add(Calendar.HOUR, 1);
		    Timestamp previsaoTermino = new Timestamp(calendar.getTimeInMillis());
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(3, Timestamp.valueOf(entity.getDatahora()));
			ps.setTimestamp(4, previsaoTermino);
			ps.setString(5, entity.getStatus());
			ps.setLong(6, entity.getDogwalkerId());
			ps.setLong(7, entity.getTutorId());
			
			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				passeio.setId(resultSet.getLong(1));
				this.inserirCachorroPasseio(passeio.getId(), entity.getCachorrosIds());
			}
			
			passeio = buscarPorId(passeio.getId());
			
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

		return passeio;
		
	}
	
	private boolean inserirCachorroPasseio(Long passeioId, List<Long> cachorroIds) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		String sql = "";
		for (int i = 0; i < cachorroIds.size(); i++) {
			Long cachorroId = cachorroIds.get(i);
			sql += " insert into passeio_cachorro (passeio_id, cachorro_id) values ("+passeioId+","+cachorroId+"); ";
		}
		
		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);
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
	public Passeio buscarPorId(Long id) {
		
		Passeio passeio = new Passeio();
		List<Cachorro> cachorros = new ArrayList<>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select  "
					+ "	p.id as p_id, "
					+ "	p.criado as p_criado,"
					+ "	p.modificado as p_modificado,"
					+ "	p.datahora as p_datahora,"
					+ "	p.status as p_status,"
					+ "	p.datahorafinalizacao as p_datahorafinalizacao,"
					+ "	p.dogwalker_id as p_dogwalker_id,"
					+ "	p.tutor_id as p_tutor_id,"
					+ "	dg.criado as dg_criado,"
					+ "	dg.modificado as dg_modificado,"
					+ "	dg.nome as dg_nome,"
					+ "	dg.email as dg_email,"
					+ "	dg.telefone as dg_telefone,"
					+ "	dg.rua as dg_rua,"
					+ "	dg.bairro as dg_bairro,"
					+ "	dg.numero as dg_numero,"
					+ "	dg.cidade_id as dg_cidade_id,"
					+ "	dg.estado_id as dg_estado_id,"
					+ "	dg.avaliacao as dg_avaliacao,"
					+ "	dg.foto_url as dg_foto_url,"
					+ "	tt.criado as tt_criado,"
					+ "	tt.modificado as tt_modificado,"
					+ "	tt.nome as tt_nome,"
					+ "	tt.email as tt_email,"
					+ "	tt.telefone as tt_telefone,"
					+ "	tt.rua as tt_rua,"
					+ "	tt.bairro as tt_bairro,"
					+ "	tt.numero as tt_numero,"
					+ "	tt.cidade_id as tt_cidade_id,"
					+ "	tt.estado_id as tt_estado_id,"
					+ "	tt.avaliacao as tt_avaliacao,"
					+ "	tt.foto_url as tt_foto_url,"
					+ "	est_tt.id as est_tt_id,"
					+ "	est_tt.nome as est_tt_nome,"
					+ "	est_tt.sigla as est_tt_sigla,"
					+ "	est_tt.ativo as est_tt_ativo,"
					+ "	cid_tt.id as cid_tt_id,"
					+ "	cid_tt.nome as cid_tt_nome,"
					+ "	cid_tt.ativo as cid_tt_ativo,"
					+ "	est_dg.id as est_dg_id,"
					+ "	est_dg.nome as est_dg_nome,"
					+ "	est_dg.sigla as est_dg_sigla,"
					+ "	est_dg.ativo as est_dg_ativo,"
					+ "	cid_dg.id as cid_dg_id,"
					+ "	cid_dg.nome as cid_dg_nome,"
					+ "	cid_dg.ativo as cid_dg_ativo "
					+ "from passeio p "
					+ " join usuario dg on dg.id = p.dogwalker_id "
					+ " join usuario tt on tt.id = p.tutor_id "
					+ " join cidade cid_dg on cid_dg.id = dg.cidade_id "
					+ " join estado est_dg on est_dg.id = dg.estado_id "
					+ " join cidade cid_tt on cid_tt.id = tt.cidade_id "
					+ " join estado est_tt on est_tt.id = tt.estado_id "
					+ " where p.id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				Usuario dogwalker = new Usuario();
				Cidade cidadeDogwalker = new Cidade();
				Estado estadoDogwalker = new Estado();
				
				Usuario tutor = new Usuario();
				Cidade cidadeTutor = new Cidade();
				Estado estadoTutor = new Estado();
				
				passeio.setId(resultSet.getLong("p_id"));
				passeio.setCriado(resultSet.getTimestamp("p_criado"));
				passeio.setModificado(resultSet.getTimestamp("p_modificado"));
				passeio.setDatahora(resultSet.getTimestamp("p_datahora").toLocalDateTime());
				passeio.setStatus(resultSet.getString("p_status"));
				passeio.setDatahorafinalizacao(resultSet.getTimestamp("p_datahorafinalizacao"));
				passeio.setDogwalkerId(resultSet.getLong("p_dogwalker_id"));
				passeio.setTutorId(resultSet.getLong("p_tutor_id"));
				
				dogwalker.setId(resultSet.getLong("p_dogwalker_id"));
				dogwalker.setCriado(resultSet.getTimestamp("dg_criado"));
				dogwalker.setModificado(resultSet.getTimestamp("dg_modificado"));
				dogwalker.setNome(resultSet.getString("dg_nome"));
				dogwalker.setEmail(resultSet.getString("dg_email"));
				dogwalker.setTelefone(resultSet.getString("dg_telefone"));
				dogwalker.setRua(resultSet.getString("dg_rua"));
				dogwalker.setBairro(resultSet.getString("dg_bairro"));
				dogwalker.setNumero(resultSet.getString("dg_numero"));
				dogwalker.setFotoUrl(resultSet.getString("dg_foto_url"));
				
				estadoDogwalker.setId(resultSet.getLong("est_dg_id"));
				estadoDogwalker.setNome(resultSet.getString("est_dg_nome"));
				estadoDogwalker.setSigla(resultSet.getString("est_dg_sigla"));
				estadoDogwalker.setAtivo(resultSet.getBoolean("est_dg_ativo"));
				
				cidadeDogwalker.setId(resultSet.getLong("cid_dg_id"));
				cidadeDogwalker.setNome(resultSet.getString("cid_dg_nome"));
				cidadeDogwalker.setAtivo(resultSet.getBoolean("cid_dg_ativo"));
				
				dogwalker.setEstado(estadoDogwalker);
				dogwalker.setCidade(cidadeDogwalker);
				
				passeio.setDogwalker(dogwalker);
				
				tutor.setId(resultSet.getLong("p_tutor_id"));
				tutor.setCriado(resultSet.getTimestamp("tt_criado"));
				tutor.setModificado(resultSet.getTimestamp("tt_modificado"));
				tutor.setNome(resultSet.getString("tt_nome"));
				tutor.setEmail(resultSet.getString("tt_email"));
				tutor.setTelefone(resultSet.getString("tt_telefone"));
				tutor.setRua(resultSet.getString("tt_rua"));
				tutor.setBairro(resultSet.getString("tt_bairro"));
				tutor.setNumero(resultSet.getString("tt_numero"));
				tutor.setFotoUrl(resultSet.getString("tt_foto_url"));
				
				estadoTutor.setId(resultSet.getLong("est_tt_id"));
				estadoTutor.setNome(resultSet.getString("est_tt_nome"));
				estadoTutor.setSigla(resultSet.getString("est_tt_sigla"));
				estadoTutor.setAtivo(resultSet.getBoolean("est_tt_ativo"));
				
				cidadeTutor.setId(resultSet.getLong("cid_tt_id"));
				cidadeTutor.setNome(resultSet.getString("cid_tt_nome"));
				cidadeTutor.setAtivo(resultSet.getBoolean("cid_tt_ativo"));
				
				tutor.setEstado(estadoTutor);
				tutor.setCidade(cidadeTutor);
				
				passeio.setTutor(tutor);
				
				cachorros = buscarCachorroPasseio(id);
				passeio.setCachorros(cachorros);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return passeio;
		
	}
	
	private List<Cachorro> buscarCachorroPasseio(Long passeioId) {
		
		List<Cachorro> cachorros = new ArrayList<>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from passeio_cachorro where passeio_id = ? ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, passeioId);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Cachorro cachorro = cachorroDao.buscarPorId(resultSet.getLong("cachorro_id"));
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
	public boolean alterarStatus(Long id, String status) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		Timestamp datahorafinalizacao;
		
		try {
			
			if(status.equals("Finalizado")) {
				datahorafinalizacao = new Timestamp(System.currentTimeMillis());
			}else {
				datahorafinalizacao = null;
			}
			
			String sql = " update passeio set modificado=?, datahorafinalizacao=?, status=?  where id=? ";
			
			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);
			
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, datahorafinalizacao);
			ps.setString(3, status);
			ps.setLong(4, id);
			
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
	public List<Passeio> buscarTodos(Long usuarioId) {
		
		Usuario usuario = usuarioDao.buscarPorId(usuarioId);
		List<Passeio> passeios = new ArrayList<>();
		List<Cachorro> cachorros = new ArrayList<>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			String sqlWhere = " 1=1 ";
			
			if(usuario.getTipo().equals(TipoUsuario.DOGWALKER.toString())) {
				sqlWhere += " and p.dogwalker_id = ? ";
			}else if(usuario.getTipo().equals(TipoUsuario.TUTOR.toString())) {
				sqlWhere += " and p.tutor_id = ? ";
			}
			
			connection = ConnectionFactory.getConnection();
			
			String sql = " select  "
					+ "	p.id as p_id, "
					+ "	p.criado as p_criado,"
					+ "	p.modificado as p_modificado,"
					+ "	p.datahora as p_datahora,"
					+ "	p.status as p_status,"
					+ "	p.datahorafinalizacao as p_datahorafinalizacao,"
					+ "	p.dogwalker_id as p_dogwalker_id,"
					+ "	p.tutor_id as p_tutor_id,"
					+ "	dg.criado as dg_criado,"
					+ "	dg.modificado as dg_modificado,"
					+ "	dg.nome as dg_nome,"
					+ "	dg.email as dg_email,"
					+ "	dg.telefone as dg_telefone,"
					+ "	dg.rua as dg_rua,"
					+ "	dg.bairro as dg_bairro,"
					+ "	dg.numero as dg_numero,"
					+ "	dg.cidade_id as dg_cidade_id,"
					+ "	dg.estado_id as dg_estado_id,"
					+ "	dg.avaliacao as dg_avaliacao,"
					+ "	dg.foto_url as dg_foto_url,"
					+ "	tt.criado as tt_criado,"
					+ "	tt.modificado as tt_modificado,"
					+ "	tt.nome as tt_nome,"
					+ "	tt.email as tt_email,"
					+ "	tt.telefone as tt_telefone,"
					+ "	tt.rua as tt_rua,"
					+ "	tt.bairro as tt_bairro,"
					+ "	tt.numero as tt_numero,"
					+ "	tt.cidade_id as tt_cidade_id,"
					+ "	tt.estado_id as tt_estado_id,"
					+ "	tt.avaliacao as tt_avaliacao,"
					+ "	tt.foto_url as tt_foto_url,"
					+ "	est_tt.id as est_tt_id,"
					+ "	est_tt.nome as est_tt_nome,"
					+ "	est_tt.sigla as est_tt_sigla,"
					+ "	est_tt.ativo as est_tt_ativo,"
					+ "	cid_tt.id as cid_tt_id,"
					+ "	cid_tt.nome as cid_tt_nome,"
					+ "	cid_tt.ativo as cid_tt_ativo,"
					+ "	est_dg.id as est_dg_id,"
					+ "	est_dg.nome as est_dg_nome,"
					+ "	est_dg.sigla as est_dg_sigla,"
					+ "	est_dg.ativo as est_dg_ativo,"
					+ "	cid_dg.id as cid_dg_id,"
					+ "	cid_dg.nome as cid_dg_nome,"
					+ "	cid_dg.ativo as cid_dg_ativo "
					+ "from passeio p "
					+ " join usuario dg on dg.id = p.dogwalker_id "
					+ " join usuario tt on tt.id = p.tutor_id "
					+ " join cidade cid_dg on cid_dg.id = dg.cidade_id "
					+ " join estado est_dg on est_dg.id = dg.estado_id "
					+ " join cidade cid_tt on cid_tt.id = tt.cidade_id "
					+ " join estado est_tt on est_tt.id = tt.estado_id "
					+ " where " + sqlWhere + " order by p.id desc ";
			
			preparedStatement = connection.prepareStatement(sql);
			
			if(!usuario.getTipo().equals(TipoUsuario.ADMIN.toString())) {
				preparedStatement.setLong(1, usuarioId);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Passeio passeio = new Passeio();
				Usuario dogwalker = new Usuario();
				Cidade cidadeDogwalker = new Cidade();
				Estado estadoDogwalker = new Estado();
				
				Usuario tutor = new Usuario();
				Cidade cidadeTutor = new Cidade();
				Estado estadoTutor = new Estado();
				
				passeio.setId(resultSet.getLong("p_id"));
				passeio.setCriado(resultSet.getTimestamp("p_criado"));
				passeio.setModificado(resultSet.getTimestamp("p_modificado"));
				passeio.setDatahora(resultSet.getTimestamp("p_datahora").toLocalDateTime());
				passeio.setStatus(resultSet.getString("p_status"));
				passeio.setDatahorafinalizacao(resultSet.getTimestamp("p_datahorafinalizacao"));
				passeio.setDogwalkerId(resultSet.getLong("p_dogwalker_id"));
				passeio.setTutorId(resultSet.getLong("p_tutor_id"));
				
				dogwalker.setId(resultSet.getLong("p_dogwalker_id"));
				dogwalker.setCriado(resultSet.getTimestamp("dg_criado"));
				dogwalker.setModificado(resultSet.getTimestamp("dg_modificado"));
				dogwalker.setNome(resultSet.getString("dg_nome"));
				dogwalker.setEmail(resultSet.getString("dg_email"));
				dogwalker.setTelefone(resultSet.getString("dg_telefone"));
				dogwalker.setRua(resultSet.getString("dg_rua"));
				dogwalker.setBairro(resultSet.getString("dg_bairro"));
				dogwalker.setNumero(resultSet.getString("dg_numero"));
				dogwalker.setFotoUrl(resultSet.getString("dg_foto_url"));
				
				estadoDogwalker.setId(resultSet.getLong("est_dg_id"));
				estadoDogwalker.setNome(resultSet.getString("est_dg_nome"));
				estadoDogwalker.setSigla(resultSet.getString("est_dg_sigla"));
				estadoDogwalker.setAtivo(resultSet.getBoolean("est_dg_ativo"));
				
				cidadeDogwalker.setId(resultSet.getLong("cid_dg_id"));
				cidadeDogwalker.setNome(resultSet.getString("cid_dg_nome"));
				cidadeDogwalker.setAtivo(resultSet.getBoolean("cid_dg_ativo"));
				
				dogwalker.setEstado(estadoDogwalker);
				dogwalker.setCidade(cidadeDogwalker);
				
				passeio.setDogwalker(dogwalker);
				
				tutor.setId(resultSet.getLong("p_tutor_id"));
				tutor.setCriado(resultSet.getTimestamp("tt_criado"));
				tutor.setModificado(resultSet.getTimestamp("tt_modificado"));
				tutor.setNome(resultSet.getString("tt_nome"));
				tutor.setEmail(resultSet.getString("tt_email"));
				tutor.setTelefone(resultSet.getString("tt_telefone"));
				tutor.setRua(resultSet.getString("tt_rua"));
				tutor.setBairro(resultSet.getString("tt_bairro"));
				tutor.setNumero(resultSet.getString("tt_numero"));
				tutor.setFotoUrl(resultSet.getString("tt_foto_url"));
				
				estadoTutor.setId(resultSet.getLong("est_tt_id"));
				estadoTutor.setNome(resultSet.getString("est_tt_nome"));
				estadoTutor.setSigla(resultSet.getString("est_tt_sigla"));
				estadoTutor.setAtivo(resultSet.getBoolean("est_tt_ativo"));
				
				cidadeTutor.setId(resultSet.getLong("cid_tt_id"));
				cidadeTutor.setNome(resultSet.getString("cid_tt_nome"));
				cidadeTutor.setAtivo(resultSet.getBoolean("cid_tt_ativo"));
				
				tutor.setEstado(estadoTutor);
				tutor.setCidade(cidadeTutor);
				
				passeio.setTutor(tutor);
				
				cachorros = buscarCachorroPasseio(passeio.getId());
				passeio.setCachorros(cachorros);
				
				passeios.add(passeio);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return passeios;
		
	}

	@Override
	public boolean registrarLatLong(PasseioLatLong entity) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into passeio_lat_long (criado, modificado, passeio_id, latitude, longitude) values (?,?,?,?,?) ";
				
		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setLong(3, entity.getPasseioId());
			ps.setString(4, entity.getLatitude());
			ps.setString(5, entity.getLongitude());
			
			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				entity.setId(resultSet.getLong(1));
				return true;
			}
			
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

		return false;
		
	}

	@Override
	public PasseioLatLong posicaoAtual(Long id) {
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from passeio_lat_long where passeio_id = ? order by id desc limit 1 ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				
				latLong.setId(resultSet.getLong("id"));
				latLong.setCriado(resultSet.getTimestamp("criado"));
				latLong.setModificado(resultSet.getTimestamp("modificado"));
				latLong.setLatitude(resultSet.getString("latitude"));
				latLong.setLongitude(resultSet.getString("longitude"));
				latLong.setPasseioId(resultSet.getLong("passeio_id"));
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return latLong;
		
	}

	@Override
	public List<PasseioLatLong> posicaoCompleta(Long id) {
		
		List<PasseioLatLong> listLatLong = new ArrayList<>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();
			
			String sql = " select * from passeio_lat_long where passeio_id = ? order by id asc ";
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				PasseioLatLong latLong = new PasseioLatLong();
				
				latLong.setId(resultSet.getLong("id"));
				latLong.setCriado(resultSet.getTimestamp("criado"));
				latLong.setModificado(resultSet.getTimestamp("modificado"));
				latLong.setLatitude(resultSet.getString("latitude"));
				latLong.setLongitude(resultSet.getString("longitude"));
				latLong.setPasseioId(resultSet.getLong("passeio_id"));
				
				listLatLong.add(latLong);
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return listLatLong;
		
	}
	
}
