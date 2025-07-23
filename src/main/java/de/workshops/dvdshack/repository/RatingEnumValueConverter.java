package de.workshops.dvdshack.repository;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingEnumValueConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(Rating rating) {
        if (rating == null) {
            return null;
        }
        return switch (rating) {
            case G -> "G";
            case PG -> "PG";
            case PG_13 -> "PG-13";
            case R -> "R";
            case NC_17 -> "NC-17";
        };
    }

    @Override
    public Rating convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return switch (dbValue) {
            case "G" -> Rating.G;
            case "PG" -> Rating.PG;
            case "PG-13" -> Rating.PG_13;
            case "R" -> Rating.R;
            case "NC-17" -> Rating.NC_17;
            default -> throw new IllegalStateException("Unexpected value: " + dbValue);
        };
    }

    public Rating toDomainValue(String relationalForm) {
        return convertToEntityAttribute(relationalForm);
    }

    public String toRelationalValue(Rating domainForm) {
        return convertToDatabaseColumn(domainForm);
    }
}
