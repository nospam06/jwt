package org.example.nosql.query;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.nosql.api.NoSqlRepository;
import org.example.jwt.nosql.query.MockNoSqlRepository;

@Slf4j
public class MockNoSqlRepositoryTest extends AbstractNoSqlRepositoryTest {

	/**
	 * mock repo for key value operations. No N1QL support
	 *
	 * @return mock repo
	 */
	protected NoSqlRepository createContainerRepo() {
		return new MockNoSqlRepository(objectMapper);
	}
}
