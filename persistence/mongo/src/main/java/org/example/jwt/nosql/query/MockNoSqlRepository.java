package org.example.jwt.nosql.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.nosql.api.NoSqlRepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * support query for nosql search
 */
@AllArgsConstructor
@Slf4j
public class MockNoSqlRepository implements NoSqlRepository {
    private final ObjectMapper objectMapper;
    private final Map<String, Map<Serializable, Object>> entityByClassMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity, U extends Serializable> Optional<T> findById(Class<T> clazz, U id) {
        Map<Serializable, Object> objectMap = fetchByClass(clazz.getName());
        return Optional.ofNullable((T) objectMap.get(id.toString()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity, U extends Serializable> List<T> findAllByIds(Class<T> clazz, Collection<U> ids) {
        Map<Serializable, Object> objectMap = fetchByClass(clazz.getName());
        return ids.stream().map(id -> (T) objectMap.get(id.toString())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
        return findAll(clazz, QueryParameter.builder().build());
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> clazz, QueryParameter parameter) {
        Map<Serializable, Object> objectMap = fetchByClass(clazz.getName());
        final ConcurrentSkipListMap<Comparable<?>, T> treeMap = new ConcurrentSkipListMap<>();
        List<T> list = objectMap.values().stream().filter(v -> {
            Map<String, Object> fieldValueMap = objectMapper.convertValue(v, TypeFactory.defaultInstance().constructParametricType(Map.class, String.class, Object.class));
            boolean condition = true;
            for (QueryParameter.WhereClause whereClause : parameter.getWhereClauses()) {
                condition = handleEqual(fieldValueMap, whereClause.getEquals())
                        && handleBetween(fieldValueMap, whereClause.getBetweens())
                        && handleIn(fieldValueMap, whereClause.getIns())
                        && handleMissing(fieldValueMap, whereClause.getMissings())
                        && handleLike(fieldValueMap, whereClause.getLikes());
                if (condition) {
                    break;
                }
            }
            if (condition) {
                handleOrderBy(parameter.getOrderBy(), treeMap, (T) v, fieldValueMap);
            }
            return condition;
        }).map(o -> (T) o).collect(Collectors.toList());
        parameter.getOrderBy().stream().findFirst()
                .ifPresent(orderBy -> {
                    list.clear();
                    if (orderBy.isAscending()) {
                        list.addAll(treeMap.values());
                    } else {
                        list.addAll(treeMap.descendingMap().values());
                    }
                });
        Optional.ofNullable(parameter.getLimit()).filter(limit -> list.size() > limit)
                .ifPresent(limit -> {
                    List<T> list1 = list.subList(0, limit);
                    List<T> list2 = new ArrayList<>(list1);
                    list.clear();
                    list.addAll(list2);
                });
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> void handleOrderBy(List<QueryParameter.OrderBy> orderBys, ConcurrentSkipListMap<Comparable<?>, T> treeMap, T v, Map<String, Object> fieldValueMap) {
        if (!orderBys.isEmpty()) {
            // only support 1 level for mock
            QueryParameter.OrderBy orderBy = orderBys.get(0);
            String[] columns = orderBy.getColumn().split("\\.");
            Comparable<?> value = "";
            if (columns.length == 1) {
                value = (Comparable<?>) fieldValueMap.get(columns[0]);
            } else {
                // nested column in object
                Map<String, Object> temp = fieldValueMap;
                for (String column : columns) {
                    Object map = temp.get(column);
                    if (map instanceof Map) {
                        temp = (Map<String, Object>) map;
                        continue;
                    }
                    value = (Comparable<?>) map;
                }
            }
            treeMap.put(orderBy.isConvertToNumber() ? new BigDecimal(value.toString()) : value, v);
        }
    }

    private boolean handleLike(Map<String, Object> fieldValueMap, List<QueryParameter.WhereLike> likes) {
        boolean condition = true;
        for (QueryParameter.WhereLike like : likes) {
            String columnString = like.getColumn();
            String value = (String) findColumnValue(fieldValueMap, columnString);
            if (value == null) {
                condition = false;
            } else {
                condition = value.toLowerCase().contains(like.getValue().toLowerCase());
                condition = like.isNot() != condition;
            }
            if (!condition) {
                break;
            }
        }
        return condition;
    }

    private boolean handleIn(Map<String, Object> fieldValueMap, List<QueryParameter.WhereIn> ins) {
        boolean condition = true;
        for (QueryParameter.WhereIn in : ins) {
            condition = false;
            String columnString = in.getColumn();
            Object value = findColumnValue(fieldValueMap, columnString);
            for (Object obj : in.getIn()) {
                if (obj instanceof Number && value instanceof Number) {
                    condition = new BigDecimal(obj.toString()).compareTo(new BigDecimal(value.toString())) == 0;
                } else {
                    condition = obj.equals(value);
                }
                if (condition) {
                    break;
                }
            }
            if (!condition) {
                break;
            }
        }
        return condition;
    }

    private boolean handleEqual(Map<String, Object> fieldValueMap, List<QueryParameter.WhereEqual> equals) {
        boolean condition = true;
        for (QueryParameter.WhereEqual equal : equals) {
            String columnString = equal.getColumn();
            Object value = findColumnValue(fieldValueMap, columnString);
            if (value == null) {
                condition = false;
            } else {
                if (equal.getValue() instanceof Number && value instanceof Number) {
                    condition = new BigDecimal(equal.getValue().toString()).compareTo(new BigDecimal(value.toString())) == 0;
                } else {
                    condition = equal.getValue().equals(value);
                }
                condition = equal.isNot() != condition;
            }
            if (!condition) {
                break;
            }
        }
        return condition;
    }

    private boolean handleBetween(Map<String, Object> fieldValueMap, List<QueryParameter.WhereBetween> betweens) {
        boolean condition = true;
        for (QueryParameter.WhereBetween between : betweens) {
            String columnString = between.getColumn();
            Object value = findColumnValue(fieldValueMap, columnString);
            condition = Optional.ofNullable(value).map(v -> {
                boolean trueOrFalse;
                if (between.isToNumber()) {
                    v = new BigDecimal(v.toString());
                }
                if (v instanceof Number
                        && between.getFrom() instanceof Number
                        && between.getTo() instanceof Number) {
                    BigDecimal decimal = new BigDecimal(v.toString());
                    trueOrFalse = new BigDecimal(between.getFrom().toString()).compareTo(decimal) <= 0
                            && new BigDecimal(between.getTo().toString()).compareTo(decimal) >= 0;
                } else {
                    String string = v.toString();
                    trueOrFalse = between.getFrom().toString().compareTo(string) <= 0
                            && between.getTo().toString().compareTo(string) >= 0;
                }
                trueOrFalse = between.isNot() != trueOrFalse;
                return trueOrFalse;
            }).orElse(false);
            if (!condition) {
                break;
            }
        }
        return condition;
    }

    private boolean handleMissing(Map<String, Object> fieldValueMap, List<QueryParameter.WhereMissing> missings) {
        boolean condition = true;
        for (QueryParameter.WhereMissing missing : missings) {
            String columnString = missing.getColumn();
            Object value = findColumnValue(fieldValueMap, columnString);
            // should not find column
            condition = missing.isNot() == (value != null);
            if (!condition) {
                break;
            }
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    private Object findColumnValue(Map<String, Object> tempMap, String columnString) {
        Object value = "";
        String[] columns = columnString.split("\\.");
        for (String column : columns) {
            Object temp = tempMap.get(column);
            if (temp == null) {
                return null;
            }
            if (temp instanceof Map) {
                tempMap = (Map<String, Object>) temp;
            } else {
                value = temp;
            }
        }
        return value;
    }

    @Override
    public <T extends BaseEntity> boolean insert(T entity) {
        Map<Serializable, Object> objectMap = fetchByClass(entity.getClass().getName());
        if (objectMap.containsKey(entity.getId())) {
            return false;
        }
        objectMap.put(entity.getId(), entity);
        return true;
    }

    @Override
    public <T extends BaseEntity> void save(T entity) {
        save(entity, entity.getId());
    }

    @Override
    public <T extends BaseEntity> void save(Collection<T> entities) {
        entities.forEach(entity -> save(entity, entity.getId()));
    }

    private <T extends BaseEntity> void save(T entity, String id) {
        Map<Serializable, Object> objectMap = fetchByClass(entity.getClass().getName());
        objectMap.put(id, entity);
    }

    @Override
    public <T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, U id) {
        Map<Serializable, Object> objectMap = fetchByClass(clazz.getName());
        objectMap.remove(id);
    }

    @Override
    public <T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, Collection<U> ids) {
        Map<Serializable, Object> objectMap = fetchByClass(clazz.getName());
        objectMap.keySet().removeAll(ids);
    }

    private Map<Serializable, Object> fetchByClass(String fullClassName) {
        return entityByClassMap.computeIfAbsent(fullClassName, k -> new HashMap<>());
    }
}
