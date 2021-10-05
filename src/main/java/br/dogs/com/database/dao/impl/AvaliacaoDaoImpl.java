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
import br.dogs.com.database.dao.AvaliacaoDao;
import br.dogs.com.model.entities.Avaliacao;

@Repository
public class AvaliacaoDaoImpl implements AvaliacaoDao {

	Logger logger = LoggerFactory.getLogger(AvaliacaoDaoImpl.class);

	@Override
	public Avaliacao cadastrar(Avaliacao entity) {

		Avaliacao avaliacao = new Avaliacao();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = " insert into avaliacao (criado, modificado, nota, descricao, passeio_id) values (?,?,?,?,?) ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setDouble(3, entity.getNota());
			ps.setString(4, entity.getDescricao());
			ps.setLong(5, entity.getPasseioId());

			ps.execute();

			connection.commit();

			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				avaliacao.setId(resultSet.getLong(1));
			}

			avaliacao = buscarPorId(avaliacao.getId());

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

		return avaliacao;

	}

	@Override
	public Avaliacao buscarPorId(Long id) {

		Avaliacao avaliacao = new Avaliacao();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();

			String sql = " select * from avaliacao where id = ? ";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {

				avaliacao.setId(resultSet.getLong("id"));
				avaliacao.setCriado(resultSet.getTimestamp("criado"));
				avaliacao.setModificado(resultSet.getTimestamp("modificado"));
				avaliacao.setNota(resultSet.getDouble("nota"));
				avaliacao.setDescricao(resultSet.getString("descricao"));
				avaliacao.setPasseioId(resultSet.getLong("passeio_id"));

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return avaliacao;

	}

	@Override
	public List<Avaliacao> buscarTodosPorDogwalker(Long id) {

		List<Avaliacao> avaliacoes = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();

			String sql = " select av.* from avaliacao av " + " join passeio p on p.id = av.passeio_id "
					+ " where p.dogwalker_id = ? order by av.id desc ";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Avaliacao avaliacao = new Avaliacao();

				avaliacao.setId(resultSet.getLong("id"));
				avaliacao.setCriado(resultSet.getTimestamp("criado"));
				avaliacao.setModificado(resultSet.getTimestamp("modificado"));
				avaliacao.setNota(resultSet.getDouble("nota"));
				avaliacao.setDescricao(resultSet.getString("descricao"));
				avaliacao.setPasseioId(resultSet.getLong("passeio_id"));

				avaliacoes.add(avaliacao);

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return avaliacoes;

	}

	@Override
	public boolean deletar(Long id) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = "delete from avaliacao where id = ?";

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
	public boolean atualizarMediaAvaliacao(Long dogwalkerId) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String sql = " update usuario set avaliacao = (select ROUND(AVG(av.nota),1) from avaliacao av join passeio p on p.id = av.passeio_id where p.dogwalker_id = ?) where id = ? ";

		try {

			connection = ConnectionFactory.getConnection();
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, dogwalkerId);
			preparedStatement.setLong(2, dogwalkerId);
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
	public Avaliacao buscarPorPasseioId(Long id) {

		Avaliacao avaliacao = new Avaliacao();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = ConnectionFactory.getConnection();

			String sql = " select * from avaliacao where passeio_id = ? ";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {

				avaliacao.setId(resultSet.getLong("id"));
				avaliacao.setCriado(resultSet.getTimestamp("criado"));
				avaliacao.setModificado(resultSet.getTimestamp("modificado"));
				avaliacao.setNota(resultSet.getDouble("nota"));
				avaliacao.setDescricao(resultSet.getString("descricao"));
				avaliacao.setPasseioId(resultSet.getLong("passeio_id"));

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet, preparedStatement, connection);
		}

		return avaliacao;

	}

}
