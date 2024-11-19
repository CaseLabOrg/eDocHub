package com.example.ecm.model.elasticsearch;

import com.example.ecm.deserializer.LocalDateTimeDeserializer;
import com.example.ecm.deserializer.OffsetDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Entity representing a document in the system.
 */
@Getter
@Setter
@Document(indexName = "documents")
public class DocumentElasticsearch {

    @Id
    @Field(name = "id", type = FieldType.Text)
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Long)
    private Long documentVersionId;

    @Field(type = FieldType.Long)
    private Long documentTypeId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long createdAt;

    @Field(type = FieldType.Object)
    private Map<String, String> values;

    @Field(type = FieldType.Boolean)
    private Boolean isAlive;
}
