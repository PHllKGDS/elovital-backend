package br.com.elovital.repository;

import br.com.elovital.model.RegistroMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroMedicamentoRepository extends JpaRepository<RegistroMedicamento, Long> {

    List<RegistroMedicamento> findByMedicamentoId(Long medicamentoId);
    List<RegistroMedicamento> findByMedicamentoIdosoId(Long idosoId);
    List<RegistroMedicamento> findByMedicamentoIdosoIdAndDataHoraBetween(Long idosoId, Instant inicio, Instant fim);
    Optional<RegistroMedicamento> findByIdAndMedicamentoIdosoId(Long id, Long idosoId);
}
