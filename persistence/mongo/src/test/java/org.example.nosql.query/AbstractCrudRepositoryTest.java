package org.example.nosql.query;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.nosql.api.CrudRepository;
import org.example.nosql.query.model.GeoEntity;
import org.example.nosql.query.model.NumericEntity;
import org.example.nosql.query.model.StringEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public abstract class AbstractCrudRepositoryTest extends JsonBaseTest {
    protected static CrudRepository crudRepo;
    protected static GenericContainer<?> container;
    protected int maxWait = 0;

    @BeforeAll
    public static void logLevel() {
        ((Logger) LoggerFactory.getLogger("root")).setLevel(Level.INFO);
    }

    @BeforeEach
    public void setup() {
        if (crudRepo == null) {
            crudRepo = createContainerRepo();
            crudRepo.save(createEntity("id3", "name3", 30d, 30d, 30, 299.9d, BigDecimal.valueOf(9.9), "-100"));
            crudRepo.save(
                    List.of(createEntity("id1", "name1", 10d, 10d, 10, 10d, BigDecimal.ONE, "300"),
                            createEntity("id2", "name2", 20d, 20d, 20, 20d, BigDecimal.TEN, "-500"),
                            createEntity("id4", "name1", -10d, -10d, 10, 1000d, null, null)));
            Set<String> expected = Set.of("id1", "id2", "id3", "id4");
            Set<String> actual = crudRepo.findAllByIds(GeoEntity.class, expected).stream().map(GeoEntity::getId).collect(Collectors.toSet());
            int check = 50;
            while (!expected.equals(actual) && (maxWait -= check) > 0) {
                waitForDb(check);
                actual = crudRepo.findAllByIds(GeoEntity.class, expected).stream().map(GeoEntity::getId).collect(Collectors.toSet());
                log.info("making sure data is persisted. fetching from db {}", actual);
            }
        }
    }

    protected abstract CrudRepository createContainerRepo();

    @Test
    public void crudTest() {
        Long numericId = System.currentTimeMillis();
        Long numericId2 = numericId * 2;
        String id = "text-" + numericId;
        log.info("testing with id {}", id);
        //
        StringEntity stringEntity = new StringEntity();
        stringEntity.setId(id);
        stringEntity.setName("My id is a string");
        boolean insert = crudRepo.insert(stringEntity);
        assertTrue(insert);

        crudRepo.save(stringEntity);
        //
        NumericEntity numericEntity = new NumericEntity();
        numericEntity.setNid(numericId);
        //numericEntity.setId(numericEntity.getId());
        numericEntity.setName("My value is 7");
        numericEntity.setValue(7);
        //
        NumericEntity numericEntity2 = new NumericEntity();
        numericEntity2.setNid(numericId2);
        //numericEntity.setId(numericEntity.getId());
        numericEntity2.setName("I am a big number guy");
        numericEntity2.setValue(123456789.87654321);

        crudRepo.save(List.of(numericEntity, numericEntity2));
        waitForDb(maxWait);
        insert = crudRepo.insert(stringEntity);
        assertFalse(insert, "Should have Document exists exception");
        //
        Optional<StringEntity> stringResult = crudRepo.findById(StringEntity.class, id);
        assertTrue(stringResult.isPresent(), "String entity not found");
        assertEquals(stringResult.get().getId(), id, "result is not " + id);
        assertEquals("My id is a string", stringResult.get().getName(), "result is not 'My id is a string'");
        //
        Optional<NumericEntity> numericResult = crudRepo.findById(NumericEntity.class, numericId);
        assertTrue(numericResult.isPresent(), "Numeric entity not found");
        assertEquals(numericId, numericResult.get().getNid(), "result is not " + numericId);
        assertEquals("My value is 7", numericResult.get().getName(), "result is not 'My value is 7'");
        assertEquals(7d, numericResult.get().getValue(), .01, "result is not 7");
        //
        Optional<NumericEntity> numericResult2 = crudRepo.findById(NumericEntity.class, numericId2);
        assertTrue(numericResult2.isPresent(), "Numeric entity not found");
        assertEquals(Long.toString(numericId2), numericResult2.get().getId(), "result is not " + numericId2);
        assertEquals("I am a big number guy", numericResult2.get().getName(), "result is not 'I am a big number guy'");
        assertEquals(123456789.87654321, numericResult2.get().getValue(), .00001, "result is not 123456789.87654321");
        try {
            crudRepo.findById(StringEntity.class, " bad id").orElseThrow(() -> new RuntimeException("bad id not found"));
        } catch (RuntimeException rnfe) {
            log.info("Pass negative test");
        } catch (Exception e) {
            fail();
        }
        List<String> input = List.of(numericId.toString(), numericId2.toString());
        List<NumericEntity> numericEntityList = crudRepo.findAllByIds(NumericEntity.class, input);
        assertEquals(2, numericEntityList.size(), "result size should be 2");
        List<String> stringList = numericEntityList.stream().map(NumericEntity::getId).collect(Collectors.toList());
        stringList.removeAll(input);
        assertTrue(stringList.isEmpty(), "some entities are not found");
        //
        crudRepo.remove(StringEntity.class, "12345");
        crudRepo.remove(StringEntity.class, id);
        crudRepo.remove(NumericEntity.class, input);
        crudRepo.remove(StringEntity.class, List.of("1234567", "abcdefg"));
        waitForDb(maxWait);
        Optional<StringEntity> stringResult3 = crudRepo.findById(StringEntity.class, id);
        assertFalse(stringResult3.isPresent(), "remove failed for " + stringEntity.getId());
        List<NumericEntity> numericEntityList2 = crudRepo.findAllByIds(NumericEntity.class, input);
        assertTrue(numericEntityList2.isEmpty(), "some entities are not removed");
    }

    @SneakyThrows
    protected void waitForDb(int timeout) {
        TimeUnit.MILLISECONDS.sleep(timeout);
    }

    @AfterAll
    static void cleanup() {
        if (container != null) {
            container.stop();
        }
    }
}
