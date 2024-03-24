package edu.java.repository.jpa;

import edu.java.domain.jpa.Links;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Links, Long> {
    Optional<Links> findByLinkUri(String linkUri);

    List<Links> findAllByLastModifyingBefore(OffsetDateTime time);

    void deleteById(@NotNull Long linkId);
}
