package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Config;
import hu.bsstudio.raktr.dal.value.ConfigKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, ConfigKey> {
}
