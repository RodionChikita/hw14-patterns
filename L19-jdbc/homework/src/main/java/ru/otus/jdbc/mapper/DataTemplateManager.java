package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.crm.model.Manager;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class DataTemplateManager implements DataTemplate<Manager> {
    @Override
    public Optional<Manager> findById(Connection connection, long id) {
        return Optional.empty();
    }

    @Override
    public List<Manager> findAll(Connection connection) {
        return null;
    }

    @Override
    public long insert(Connection connection, Manager object) {
        return 0;
    }

    @Override
    public void update(Connection connection, Manager object) {

    }
}
