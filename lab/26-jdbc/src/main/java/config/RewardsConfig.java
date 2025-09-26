package config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

@Configuration
public class RewardsConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public RewardNetwork rewardNetwork(JdbcTemplate jdbcTemplate) {
        return new RewardNetworkImpl(
            accountRepository(jdbcTemplate),
            restaurantRepository(jdbcTemplate),
            rewardRepository(jdbcTemplate));
    }

    @Bean
    public AccountRepository accountRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcAccountRepository(jdbcTemplate);
    }

    @Bean
    public RestaurantRepository restaurantRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRestaurantRepository(jdbcTemplate);
    }

    @Bean
    public RewardRepository rewardRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRewardRepository(jdbcTemplate);
    }
}
