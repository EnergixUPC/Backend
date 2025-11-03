package com.backendsems.shared.infrastructure.persistence.jpa.configuration.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.regex.Pattern;

public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    private static final Pattern CAMEL_CASE = Pattern.compile("([a-z])([A-Z])");

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        if (identifier == null) return null;
        
        String tableName = identifier.getText();
        String snakeCaseName = camelCaseToSnakeCase(tableName);
        String pluralizedName = pluralize(snakeCaseName);
        
        return Identifier.toIdentifier(pluralizedName);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        if (identifier == null) return null;
        return Identifier.toIdentifier(camelCaseToSnakeCase(identifier.getText()));
    }

    private String camelCaseToSnakeCase(String camelCase) {
        return CAMEL_CASE.matcher(camelCase).replaceAll("$1_$2").toLowerCase();
    }

    private String pluralize(String word) {
        if (word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        } else if (word.endsWith("s") || word.endsWith("x") || word.endsWith("z") || 
                   word.endsWith("ch") || word.endsWith("sh")) {
            return word + "es";
        } else {
            return word + "s";
        }
    }
}