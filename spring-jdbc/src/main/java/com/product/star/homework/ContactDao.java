package com.product.star.homework;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactDao {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final String GET_ALL_CONTACTS = "SELECT * FROM CONTACT";
    private static final String GET_CONTACT = "SELECT * FROM CONTACT WHERE ID = :id";
    private static final String INSERT_CONTACT = "INSERT INTO CONTACT(NAME, SURNAME, EMAIL, PHONE_NUMBER) " +
            "VALUES(:name, :surname, :email, :phoneNumber)";
    private static final String UPDATE_PHONE_NUMBER = "UPDATE CONTACT SET PHONE_NUMBER = :phoneNumber WHERE ID = :id";
    private static final String UPDATE_EMAIL = "UPDATE CONTACT SET EMAIL = :email WHERE ID = :id";
    private static final String DELETE_CONTACT = "DELETE FROM CONTACT WHERE ID = :id";

    private static final ResultSetExtractor<List<Contact>> RS_EXTRACTOR = rs -> {
        List<Contact> contactList = new ArrayList<>();
        while (rs.next()) {
            Contact contact = new Contact(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("PHONE_NUMBER")
            );
            contactList.add(contact);
        }
        return contactList;
    };
    private static final RowMapper<Contact> CONTACT_ROW_MAPPER = (rs, id) ->
            new Contact(rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("PHONE_NUMBER"));

    public ContactDao(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public List<Contact> getAllContacts() {
        return namedJdbcTemplate.query(GET_ALL_CONTACTS, RS_EXTRACTOR);
    }

    public Contact getContact(long contactId) {
        return namedJdbcTemplate.queryForObject(GET_CONTACT,
                new MapSqlParameterSource("id", contactId),
                CONTACT_ROW_MAPPER);
    }

    public long addContact(Contact contact) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedJdbcTemplate.update(INSERT_CONTACT, new MapSqlParameterSource()
                        .addValue("name", contact.getName())
                        .addValue("surname", contact.getSurname())
                        .addValue("email", contact.getEmail())
                        .addValue("phoneNumber", contact.getPhone()),
                keyHolder, new String[]{"id"});

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void updatePhoneNumber(long contactId, String phoneNumber) {
        namedJdbcTemplate.update(UPDATE_PHONE_NUMBER, new MapSqlParameterSource()
                .addValue("id", contactId)
                .addValue("phoneNumber", phoneNumber));
    }

    public void updateEmail(long contactId, String email) {
        namedJdbcTemplate.update(UPDATE_EMAIL, new MapSqlParameterSource()
                .addValue("id", contactId)
                .addValue("email", email));
    }

    public void deleteContact(long contactId) {
        namedJdbcTemplate.update(DELETE_CONTACT, new MapSqlParameterSource("id", contactId));
    }
}
