package br.com.elovital.repository;

import br.com.elovital.model.RegistroAgua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAguaRepository extends JpaRepository<RegistroAgua, Long> {

    List<RegistroAgua> findByIdosoId(Long idosoId);
    List<RegistroAgua> findByIdosoIdAndDataHoraBetween(Long idosoId, Instant inicio, Instant fim);
    Optional<RegistroAgua> findByIdAndIdosoId(Long id, Long idosoId);
}
