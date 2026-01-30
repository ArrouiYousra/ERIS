package Fourth_Argument.eris.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Fourth_Argument.eris.api.model.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Override
    Optional<Channel> findById(Long id);

    void deleteById(Long id);

}
