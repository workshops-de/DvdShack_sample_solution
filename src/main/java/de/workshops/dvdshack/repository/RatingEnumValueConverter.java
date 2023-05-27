package de.workshops.dvdshack.repository;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.converter.spi.EnumValueConverter;
import org.hibernate.type.descriptor.java.EnumJavaType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class RatingEnumValueConverter implements EnumValueConverter<Rating, Object>, Serializable {
    private final EnumJavaType<Rating> enumJavaType;
    private final JdbcType jdbcType;

    public RatingEnumValueConverter(EnumJavaType<Rating> enumJavaType, JdbcType jdbcType) {
        this.enumJavaType = enumJavaType;
        this.jdbcType = jdbcType;
    }

    public EnumJavaType<Rating> getDomainJavaType() {
        return this.enumJavaType;
    }

    public JavaType<Object> getRelationalJavaType() {
        return new ObjectJavaType();
    }

    public Rating toDomainValue(Object relationalForm) {
        return switch ((String)relationalForm) {
            case "G" -> Rating.G;
            case "PG" -> Rating.PG;
            case "PG-13" -> Rating.PG_13;
            case "R" -> Rating.R;
            case "NC-17" -> Rating.NC_17;
            default -> throw new IllegalStateException("Unexpected value: " + relationalForm);
        };
    }

    public String toRelationalValue(Rating domainForm) {
        return switch (domainForm) {
            case G -> "G";
            case PG -> "PG";
            case PG_13 -> "PG-13";
            case R -> "R";
            case NC_17 -> "NC-17";
        };
    }

    public int getJdbcTypeCode() {
        return this.jdbcType.getJdbcTypeCode();
    }

    public String toSqlLiteral(Object value) {
        return String.format(Locale.ROOT, "'%s'", ((Rating)value).name());
    }

    public void writeValue(PreparedStatement statement, Rating value, int position, SharedSessionContractImplementor session) throws SQLException {
        // nothing to do here
    }
}
