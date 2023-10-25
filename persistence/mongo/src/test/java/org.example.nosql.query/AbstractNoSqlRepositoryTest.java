package org.example.nosql.query;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.nosql.api.NoSqlRepository;
import org.example.jwt.nosql.query.QueryParameter;
import org.example.nosql.query.model.GeoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractNoSqlRepositoryTest extends AbstractCrudRepositoryTest {
    protected static NoSqlRepository repo;

    @BeforeEach
    public void setup() {
        super.setup();
        repo = (NoSqlRepository) crudRepo;
    }

    @Test
    public void testFindAll() {
        List<GeoEntity> entities = repo.findAll(GeoEntity.class);
        assertNotNull(entities);
        assertEquals(4, entities.size());
    }

    @Test
    public void testOrderBy() {
        List<GeoEntity> entities = repo.findAll(GeoEntity.class, QueryParameter.builder().orderBy("id", true, false).build());
        assertTrue(entities.size() > 1, "size should be > 1");
        assertEquals("id1", entities.get(0).getId());
        entities = repo.findAll(GeoEntity.class, QueryParameter.builder().whereNotMissing("numberString").orderBy("numberString", false, false).limit(1).build());
        assertEquals(1, entities.size());
        assertEquals("300", entities.get(0).getNumberString());

        entities = repo.findAll(GeoEntity.class, QueryParameter.builder().whereNotMissing("nestedObject.id").orderBy("nestedObject.id", false, false).build());
        assertFalse(CollectionUtils.isEmpty(entities));
    }

    @Test
    public void testEqual() {
        GeoEntity entity1 = createEntity("id1", "name1", 10d, 10d, 10, 100d, BigDecimal.ONE, "100");
        GeoEntity entity2 = createEntity("id2", "name2", 20d, 20d, 20, 20d, BigDecimal.TEN, "100");
        GeoEntity entity3 = createEntity("id3", "name3", 30d, 30d, 30, 3d, BigDecimal.valueOf(9.9), "100");
        List<GeoEntity> test1 = repo.findAll(GeoEntity.class, QueryParameter.emptyBuilder().or().whereEqual("name", "name2").build());
        log.info("test 1 = {}", test1);
        assertNotNull(test1);
        assertEquals(1, test1.size());
        assertEquals(entity2.getId(), test1.get(0).getId());
        List<GeoEntity> test2 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("intValue", 30).build());
        log.info("test 2 = {}", test2);
        assertNotNull(test2);
        assertEquals(1, test2.size());
        assertEquals(entity3.getId(), test2.get(0).getId());
        List<GeoEntity> test3 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("decimalValue", BigDecimal.ONE).build());
        log.info("test 3 = {}", test3);
        assertNotNull(test3);
        assertEquals(1, test3.size());
        assertEquals(entity1.getId(), test3.get(0).getId());
        List<GeoEntity> test4 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("decimalValue", BigDecimal.TEN)
                .or().whereEqual("decimalValue", BigDecimal.valueOf(9.9)).build());
        log.info("test 4 = {}", test4);
        assertNotNull(test4);
        assertEquals(2, test4.size());
        Set<String> set4 = test4.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set4.contains(entity2.getId()));
        assertTrue(set4.contains(entity3.getId()));
        List<GeoEntity> test5 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("decimalValue", BigDecimal.TEN)
                .whereNotEqual("doubleValue", 3d).whereEqual("intValue", 20).build());
        log.info("test 5 = {}", test5);
        assertNotNull(test5);
        assertEquals(1, test5.size());
        assertEquals(entity2.getId(), test5.get(0).getId());
        List<GeoEntity> test6 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("decimalValue", BigDecimal.ONE)
                .or().whereEqual("intValue", 20).build());
        log.info("test 6 = {}", test6);
        assertNotNull(test6);
        assertEquals(2, test6.size());
        Set<String> set6 = test6.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set6.contains(entity1.getId()));
        assertTrue(set6.contains(entity2.getId()));
        List<GeoEntity> test7 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("name", 99).build());
        log.info("test 7 = {}", test7);
        assertNotNull(test7);
        assertEquals(0, test7.size());
        List<GeoEntity> test8 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereEqual("intValue", "99").build());
        log.info("test 8 = {}", test8);
        assertNotNull(test8);
        assertEquals(0, test8.size());
        // nested object
        List<GeoEntity> test11 = repo.findAll(GeoEntity.class, QueryParameter.emptyBuilder().or().whereEqual("nestedObject.name", "name3").build());
        log.info("test 11 = {}", test11);
        assertNotNull(test11);
        assertEquals(1, test11.size());
        assertEquals(entity3.getId(), test11.get(0).getId());
    }

    @Test
    public void testBetween() {
        GeoEntity entity1 = createEntity("id1", "name1", 10d, 10d, 10, 100d, BigDecimal.ONE, "100");
        GeoEntity entity2 = createEntity("id2", "name2", 20d, 20d, 20, 20d, BigDecimal.TEN, "-500");
        GeoEntity entity3 = createEntity("id3", "name3", 30d, 30d, 30, 3d, BigDecimal.valueOf(9.9), "-100");
        List<GeoEntity> test1 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("name", "name1", "name3", QueryParameter.NO_CONVERSION)
                .whereNotBetween("decimalValue", 9, 11, QueryParameter.NO_CONVERSION).build());
        log.info("test 1 = {}", test1);
        assertNotNull(test1);
        assertEquals(1, test1.size());
        assertEquals(entity1.getId(), test1.get(0).getId());
        List<GeoEntity> test2 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("intValue", 20, 30, QueryParameter.NO_CONVERSION).build());
        log.info("test 2 = {}", test2);
        assertNotNull(test2);
        assertEquals(2, test2.size());
        Set<String> set2 = test2.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set2.contains(entity2.getId()));
        assertTrue(set2.contains(entity3.getId()));
        List<GeoEntity> test3 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("doubleValue", 30d, 299.99, QueryParameter.NO_CONVERSION).build());
        log.info("test 3 = {}", test3);
        assertNotNull(test3);
        assertEquals(1, test3.size());
        assertEquals(entity3.getId(), test3.get(0).getId());
        List<GeoEntity> test6 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("decimalValue", BigDecimal.ZERO, BigDecimal.ONE, QueryParameter.NO_CONVERSION)
                .or().whereBetween("intValue", 30, 31, QueryParameter.NO_CONVERSION).build());
        log.info("test 6 = {}", test6);
        assertNotNull(test6);
        assertEquals(2, test6.size());
        Set<String> set6 = test6.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set6.contains(entity1.getId()));
        assertTrue(set6.contains(entity3.getId()));
        List<GeoEntity> test9 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("intValue", -50, -50, QueryParameter.NO_CONVERSION).build());
        log.info("test 9 = {}", test9);
        assertNotNull(test9);
        assertEquals(0, test9.size());
        List<GeoEntity> test11 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereBetween("nestedObject.name", "name1", "name3", QueryParameter.NO_CONVERSION)
                .whereNotBetween("decimalValue", 9, 11, QueryParameter.NO_CONVERSION).build());
        log.info("test 11 = {}", test1);
        assertNotNull(test11);
        assertEquals(1, test11.size());
        assertEquals(entity1.getId(), test11.get(0).getId());
    }

    @Test
    public void testIn() {
        GeoEntity entity1 = createEntity("id1", "name1", 10d, 10d, 10, 100d, BigDecimal.ONE, "100");
        GeoEntity entity2 = createEntity("id2", "name2", 20d, 20d, 20, 20d, BigDecimal.TEN, "100");
        GeoEntity entity3 = createEntity("id3", "name3", 30d, 30d, 30, 3d, BigDecimal.TEN, "100");
        List<GeoEntity> test1 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereIn("name", List.of("name2", "name3")).build());
        log.info("test 1 = {}", test1);
        assertNotNull(test1);
        assertEquals(2, test1.size());
        Set<String> set1 = test1.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set1.contains(entity3.getId()) && set1.contains(entity2.getId()));
        List<GeoEntity> test2 = repo.findAll(GeoEntity.class, QueryParameter.builder()
                .whereIn("doubleValue", List.of(10, 20)).build());
        log.info("test 2 = {}", test2);
        assertNotNull(test2);
        assertEquals(2, test2.size());
        Set<String> set2 = test2.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set2.contains(entity1.getId()) && set2.contains(entity2.getId()));
        List<GeoEntity> test3 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereIn("name", List.of(2, 3)).build());
        assertEquals(0, test3.size());
        // nested
        List<GeoEntity> test11 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereIn("nestedObject.name", List.of("name2", "name3")).build());
        log.info("test 11 = {}", test11);
        assertNotNull(test11);
        assertEquals(2, test11.size());
        Set<String> set11 = test11.stream().map(GeoEntity::getId).collect(Collectors.toSet());
        assertTrue(set11.contains(entity3.getId()) && set1.contains(entity2.getId()));
        // empty in
        List<GeoEntity> test12 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereIn("name", List.of()).build());
        assertTrue(test12.isEmpty());
    }

    @Test
    public void testLike() {
        List<GeoEntity> test1 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereLike("name", "1").build());
        assertNotNull(test1);
        assertEquals(2, test1.size());
        List<GeoEntity> test2 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereNotLike("name", "1").build());
        assertNotNull(test2);
        assertEquals(2, test2.size());
        List<GeoEntity> test11 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereLike("name", "1").whereLike("numberString", "0").build());
        assertNotNull(test11);
        assertEquals(1, test11.size());
        List<GeoEntity> test12 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereLike("name", "1").or().whereNotLike("numberString", "5").build());
        assertNotNull(test12);
        assertEquals(3, test12.size());
    }

    @Test
    public void testMissing() {
        List<GeoEntity> test1 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereMissing("numberString").build());
        assertNotNull(test1);
        assertEquals(1, test1.size());
        assertEquals("id4", test1.get(0).getId());
        List<GeoEntity> test2 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereMissing("numberString").build());
        assertNotNull(test2);
        assertEquals(1, test2.size());
        assertEquals("id4", test2.get(0).getId());
        List<GeoEntity> test3 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereNotMissing("numberString").build());
        assertNotNull(test3);
        assertEquals(3, test3.size());
        List<GeoEntity> test4 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereNotMissing("numberString").build());
        assertNotNull(test4);
        assertEquals(3, test4.size());
        List<GeoEntity> test7 = repo.findAll(GeoEntity.class, QueryParameter.builder().whereMissing("randomColumn").build());
        log.info("test 7 = {}", test7);
        assertNotNull(test7);
        assertEquals(4, test7.size());
    }
}
