package br.dogs.com.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.dogs.com.database.connection.ConnectionFactory;
import br.dogs.com.database.dao.HorarioDao;

@Repository
public class HorarioDaoImpl implements HorarioDao {
	
	Logger logger = LoggerFactory.getLogger(HorarioDaoImpl.class);
	
	@Override
	public boolean verificarDisponibilidade(String datahora, Long usuarioId) {
				
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			
			datahora = datahora.replace("T", " ");
						
			connection = ConnectionFactory.getConnection();
						
			 String sql = " select "
					+ "	case "
					+ "		when count(ch.id) > 0 then true else false "
					+ "	end atende_no_dia, "
					+ "	( "
					+ "		select  "
					+ "			case  "
					+ "				when count(p.id) > 0 then true else false "
					+ "			end "
					+ "		from passeio p "
					+ "		where 1=1  "
					+ "		and p.previsao_termino > '%s' and '%s' < p.previsao_termino "
					+ "		and p.dogwalker_id = %d "
					+ "		and p.status <> 'Finalizado' and p.status <> 'Recusado' "
					+ "	) as existe_passeio_marcado "
					+ "from configuracao_horario ch "
					+ "where 1=1  "
					+ "and ch.usuario_id = %d  "
					+ "and ch.dia_semana = DATE_PART('dow', date('%s')) "
					+ "and (select '%s'::timestamp::time) between ch.hora_inicio and ch.hora_final ";
			 
			String sqlFormatted = String.format(sql, datahora,datahora,usuarioId,usuarioId,datahora,datahora);
			 
			preparedStatement = connection.prepareStatement(sqlFormatted);

			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				
				boolean atendeNoDia = resultSet.getBoolean("atende_no_dia");
				boolean existePasseioMarcado = resultSet.getBoolean("existe_passeio_marcado");
				
				if(atendeNoDia == true && existePasseioMarcado == false) {
					return true;
				}
				
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			
			ConnectionFactory.close(resultSet, preparedStatement, connection);
			
		}
		
		return false;
		
	}

}
