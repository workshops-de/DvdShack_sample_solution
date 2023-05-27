package de.workshops.dvdshack.repository;

import org.hibernate.type.EnumType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.java.EnumJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.ObjectJdbcType;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.Properties;

public class RatingType extends EnumType<Rating> {
    public RatingType() {
        super(Rating.class,
                new RatingEnumValueConverter(
                        new EnumJavaType<>(Rating.class),
                        new ObjectJdbcType(SqlTypes.OTHER)),
                new TypeConfiguration());
    }

    @Override
    public void setParameterValues(Properties parameters) {
        // nothing to do here
    }

    @Override
    public JdbcType getJdbcType(TypeConfiguration typeConfiguration) {
        return new ObjectJdbcType(SqlTypes.OTHER);
    }
}
