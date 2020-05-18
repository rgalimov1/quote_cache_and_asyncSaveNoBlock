package com.luxoft.quote.dao;

import com.luxoft.quote.domain.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class QuoteRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void QuoteRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public int addQuote(Quote quote) {
        return this.jdbcTemplate.update(
                "insert into Quote (isin, ask, bid) values (?, ?, ?)",
                quote.getIsin(), quote.getAsk(), quote.getBid());
    }

    private static final class QuoteMapper implements RowMapper<Quote> {

        public Quote mapRow(ResultSet rs, int rowNum) throws SQLException {
            Quote quote = new Quote();
            quote.setIsin(rs.getString("isin"));
            quote.setAsk(rs.getBigDecimal("ask"));
            quote.setBid(rs.getBigDecimal("bid"));
            return quote;
        }
    }
}


