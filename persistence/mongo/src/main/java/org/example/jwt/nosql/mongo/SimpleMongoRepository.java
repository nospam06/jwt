package org.example.jwt.nosql.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertOneResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.example.jwt.model.AutoExpire;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;
import org.example.jwt.nosql.api.NoSqlRepository;
import org.example.jwt.nosql.query.QueryParameter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimpleMongoRepository implements NoSqlRepository {
    private static final String MONGODB_ID = "_id";
    private static final String ID = "id";
    private static final String TYPE = "_type";
    private static final String NE = "$ne";
    private static final String IN = "$in";
    private static final String GTE = "$gte";
    private static final String LTE = "$lte";
    private static final String GT = "$gt";
    private static final String LT = "$lt";
    private static final String NOT = "$not";
    private static final String AND = "$and";
    private static final String OR = "$or";
    private static final String REGEX = "$regex";
    private static final String EXISTS = "$exists";
    private static final String EXPR = "$expr";
    private static final String TO_DOUBLE = "$toDouble";
    private static final String DOLLAR = "$";

    private final MongoProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public <T extends BaseEntity, U extends Serializable> Optional<T> findById(Class<T> clazz, U id) {
        Document filter = eqFilter(id);
        List<T> results = selectQuery(clazz, filter, new Document(), new Document(), 0);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public <T extends BaseEntity, U extends Serializable> List<T> findAllByIds(Class<T> clazz, Collection<U> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Document filter = inFilter(ids);
        return selectQuery(clazz, filter, new Document(), new Document(), 0);
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
        return findAll(clazz, QueryParameter.builder().build());
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> clazz, QueryParameter parameter) {
        // fields
        Document fields = new Document();
        parameter.getColumns().forEach(col -> fields.put(col, 1));
        List<Document> allWhereClauses = new ArrayList<>();
        for (QueryParameter.WhereClause where : parameter.getWhereClauses()) {
            List<Document> ands = new ArrayList<>();
            equals(where.getEquals(), ands);
            ins(where.getIns(), ands);
            betweens(where.getBetweens(), ands);
            likes(where.getLikes(), ands);
            missings(where.getMissings(), ands);
            if (!ands.isEmpty()) {
                Document document = new Document(AND, ands);
                allWhereClauses.add(document);
            }
        }
        Document whereFilter = allWhereClauses.isEmpty() ? new Document() : new Document(OR, allWhereClauses);
        Document orderByFilter = new Document();
        for (QueryParameter.OrderBy orderBy : parameter.getOrderBy()) {
            orderByFilter.put(orderBy.getColumn(), orderBy.isAscending() ? 1 : -1);
        }
        return selectQuery(clazz, whereFilter, fields, orderByFilter, Optional.ofNullable(parameter.getLimit()).orElse(0));
    }

    private static void missings(List<QueryParameter.WhereMissing> missings, List<Document> ands) {
        missings.forEach(missing -> {
            Document doc = new Document();
            ands.add(doc);
            doc.put(missing.getColumn(), Map.of(EXISTS, missing.isNot()));
        });
    }

    private static void likes(List<QueryParameter.WhereLike> likes, List<Document> ands) {
        likes.forEach(like -> {
            Document doc = new Document();
            ands.add(doc);
            Map<String, Object> regex = Map.of(REGEX, Pattern.compile(like.getValue(), Pattern.CASE_INSENSITIVE));
            if (like.isNot()) {
                regex = Map.of(NOT, regex, EXISTS, true);
            }
            doc.put(like.getColumn(), regex);
        });
    }

    private static void betweens(List<QueryParameter.WhereBetween> betweens, List<Document> ands) {
        betweens.forEach(between -> {
            Document doc = new Document();
            ands.add(doc);
            Map<String, Object> range = new HashMap<>();
            if (between.isToNumber()) {
                if (between.isNot()) {
                    doc.put(between.getColumn(), Map.of(EXISTS, true));
                    doc.put(OR, List.of(
                            Map.of(EXPR, Map.of(LT, List.of(Map.of(TO_DOUBLE, DOLLAR + between.getColumn()), between.getFrom()))),
                            Map.of(EXPR, Map.of(GT, List.of(Map.of(TO_DOUBLE, DOLLAR + between.getColumn()), between.getTo())))));
                } else {
                    doc.put(AND, List.of(
                            Map.of(EXPR, Map.of(GTE, List.of(Map.of(TO_DOUBLE, DOLLAR + between.getColumn()), between.getFrom()))),
                            Map.of(EXPR, Map.of(LTE, List.of(Map.of(TO_DOUBLE, DOLLAR + between.getColumn()), between.getTo())))));
                }
            } else {
                if (between.isNot()) {
                    Map<String, Object> not = new HashMap<>();
                    not.put(NOT, range);
                    not.put(EXISTS, true);
                    range.put(GTE, between.getFrom());
                    range.put(LTE, between.getTo());
                    doc.put(between.getColumn(), not);
                } else {
                    doc.put(between.getColumn(), Map.of(GTE, between.getFrom(), LTE, between.getTo()));
                }
            }
        });
    }

    private static void ins(List<QueryParameter.WhereIn> ins, List<Document> ands) {
        ins.forEach(in -> {
            Document doc = new Document();
            ands.add(doc);
            doc.put(in.getColumn(), Map.of(IN, in.getIn()));
        });
    }

    private static void equals(List<QueryParameter.WhereEqual> equals, List<Document> ands) {
        equals.forEach(equal -> {
            Document doc = new Document();
            ands.add(doc);
            if (equal.isNot()) {
                doc.put(equal.getColumn(), Map.of(NE, equal.getValue()));
            } else {
                doc.put(equal.getColumn(), equal.getValue());
            }
        });
    }

    public <T extends BaseEntity> List<T> selectQuery(Class<T> clazz, Document filter, Document fields, Document orderByFilter, int limit) {
        List<T> results = new ArrayList<>();
        String type = clazz.getAnnotation(NoSqlEntity.class).type();
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(type);
            FindIterable<Document> foundDocuments = collection.find(filter).projection(fields).sort(orderByFilter).limit(limit);
            foundDocuments.forEach(document -> {
                Object pk = document.get(MONGODB_ID);
                document.put(ID, pk);
                document.put(TYPE, type);
                T result = objectMapper.convertValue(document, clazz);
                results.add(result);
            });
        }
        return results;
    }

    @Override
    public <T extends BaseEntity> boolean insert(T entity) {
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(entity.getType());
            Document document = convertEntityToDocument(entity);
            InsertOneResult result = collection.insertOne(document);
            return result.wasAcknowledged();
        } catch (MongoWriteException mwe) {
            log.trace("insert failed " + entity.getClass() + " " + entity.getId(), mwe);
            return false;
        }
    }

    @Override
    public <T extends BaseEntity> void save(T entity) {
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(entity.getType());
            FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
            options.upsert(true);
            Document document = convertEntityToDocument(entity);
            collection.findOneAndReplace(eqFilter(entity.getId()), document, options);
        }
    }

    @Override
    public <T extends BaseEntity> void save(Collection<T> entities) {
        if (entities.isEmpty()) {
            return;
        }
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(entities.iterator().next().getType());
            List<ReplaceOneModel<Document>> operations = entities.stream().map(entity -> {
                Document eqFilter = eqFilter(entity.getId());
                Document document = convertEntityToDocument(entity);
                ReplaceOptions options = new ReplaceOptions();
                options.upsert(true);
                return new ReplaceOneModel<>(eqFilter, document, options);
            }).collect(Collectors.toList());
            BulkWriteResult bulkWriteResult = collection.bulkWrite(operations);
            log.info("Inserted {}, Updated {} documents", bulkWriteResult.getInsertedCount(), bulkWriteResult.getModifiedCount());
        }
    }

    @Override
    public <T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, U id) {
        String type = clazz.getAnnotation(NoSqlEntity.class).type();
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(type);
            collection.deleteOne(eqFilter(id));
        }
    }

    @Override
    public <T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, Collection<U> ids) {
        if (ids.isEmpty()) {
            return;
        }
        String type = clazz.getAnnotation(NoSqlEntity.class).type();
        try (MongoClient mongoClient = mongoClient()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(properties.getDatabase());
            MongoCollection<Document> collection = mongoDatabase.getCollection(type);
            List<DeleteOneModel<Document>> operations = ids.stream()
                    .map(id -> new DeleteOneModel<Document>(eqFilter(id)))
                    .collect(Collectors.toList());
            BulkWriteResult bulkWriteResult = collection.bulkWrite(operations);
            log.info("Deleted {} documents", bulkWriteResult.getDeletedCount());
        }
    }

    private <T extends BaseEntity> Document convertEntityToDocument(T entity) {
        Map<String, Object> map = objectMapper.convertValue(entity, new TypeReference<>() {
        });
        Document document = new Document(map);
        document.put(MONGODB_ID, entity.getId());
        document.remove(TYPE);
        if (entity instanceof AutoExpire && ((AutoExpire) entity).getExpiry() > 0) {
            document.put("expireDateTime", Instant.now());
        }
        return document;
    }

    private <U extends Serializable> Document eqFilter(U id) {
        return new Document(MONGODB_ID, id.toString());
    }

    private <U extends Serializable> Document inFilter(Collection<U> ids) {
        return new Document(MONGODB_ID, Map.of(IN, ids));
    }

    private MongoClient mongoClient() {
        return MongoClients.create(properties.getUri());
    }
}
