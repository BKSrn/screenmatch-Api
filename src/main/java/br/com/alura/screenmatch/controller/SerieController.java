package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    SerieService service;

    @GetMapping
    public List<SerieDTO> getSeries() {
        return service.getTodasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> getTop5Series() {
        return service.getTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> getLancamentos() {
        return service.getLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO getSerieById(@PathVariable Long id) {
        return service.getSerieById(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> getTodasTemporadas(@PathVariable Long id) {
        return service.getTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numeroTemporada}")
    public List<EpisodioDTO> getTemporadasPorNumero(@PathVariable Long id, @PathVariable int numeroTemporada){
        return service.getEpisodiosPorTemporada(id, numeroTemporada);
    }

    @GetMapping("/{id}/temporadas/top")
    public  List<EpisodioDTO> getTop5Episodios(@PathVariable Long id){
        return service.getTop5Episodios(id);
    }

    @GetMapping("/categoria/{categoriaEscolhida}")
    public List<SerieDTO> getSeriesPorCategoria(@PathVariable String categoriaEscolhida){
        return service.getSeriesPorCategoria(categoriaEscolhida);
    }

    @PostMapping("/add/{nomeSerie}")
    public SerieDTO adicionarSerie(@PathVariable String nomeSerie){
        return service.salvarSerieOmdb(nomeSerie);
    }

    @PostMapping("/add/episodios/{nomeSerie}")
    public List<EpisodioDTO> adicionarEpisodios(@PathVariable String nomeSerie){
        return service.salvarEpisodiosOmdb(nomeSerie);
    }
}
