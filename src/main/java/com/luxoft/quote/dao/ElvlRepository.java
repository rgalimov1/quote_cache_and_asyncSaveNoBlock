package com.luxoft.quote.dao;

import com.luxoft.quote.domain.Elvl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ElvlRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void ElvlRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public int updateElvl(Elvl elvl) {
        return this.jdbcTemplate.update(
                "merge into Elvl key(isin) values (?, ?)",
                elvl.getIsin(), elvl.getElvl());
    }

    private static final class ElvlMapper implements RowMapper<Elvl> {

        public Elvl mapRow(ResultSet rs, int rowNum) throws SQLException {
            Elvl elvl = new Elvl();
            elvl.setIsin(rs.getString("isin"));
            elvl.setElvl(rs.getBigDecimal("elvl"));
            return elvl;
        }
    }
}


