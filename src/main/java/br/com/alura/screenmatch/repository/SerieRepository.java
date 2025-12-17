package br.com.alura.screenmatch.repository;


import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findFirstByTituloContainsIgnoreCase(String nomeSerie);
    List<Serie> findByAtoresContainsIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao);
    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    // Buscar séries com temporadas menor que o parametro e avaliacao maior que o parametro
    List<Serie> findByTotalTemporadasLessThanAndAvaliacaoGreaterThanEqual(Integer numDeTemp, double avaliacao);

    //mesma coisa que o metodo de cima so que usando JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> epiosdiosPorTrecho(String trecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    //busca os episodios a partir do ano do parametro
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodioPorSerieEAno(Serie serie, int anoLancamento);

    List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();

}
