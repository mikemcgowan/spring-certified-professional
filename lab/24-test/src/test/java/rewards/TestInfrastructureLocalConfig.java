package rewards;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/* TODO-05: Update this configuration class so that its
 *          beans are members of the "local" profile.
 */
@Configuration
public class TestInfrastructureLocalConfig {

    /**
     * Creates an in-memory "rewards" database populated
     * with test data for fast testing
     */
    @Bean
    @Profile("local")
    public DataSource dataSource() {
        return
            (new EmbeddedDatabaseBuilder())
                .addScript("classpath:rewards/testdb/schema.sql")
                .addScript("classpath:rewards/testdb/data.sql")
                .build();
    }
}
