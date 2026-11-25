package br.com.elovital.repository;

import br.com.elovital.model.LembreteDiaSemana;
import br.com.elovital.model.LembreteDiaSemanaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LembreteDiaSemanaRepository extends JpaRepository<LembreteDiaSemana, LembreteDiaSemanaId> {

    List<LembreteDiaSemana> findByLembreteMedicamentoId(Long lembreteMedicamentoId);
}
