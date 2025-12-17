package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> getTodasSeries() {
        List<Serie> series = serieRepository.findAll();

        return converterListParaDTO(series);
    }

    public List<SerieDTO> getTop5Series() {
        List<Serie> seriesTop5 = serieRepository.findTop5ByOrderByAvaliacaoDesc();

        return converterListParaDTO(seriesTop5);
    }

    public List<SerieDTO> getLancamentos() {
        List<Serie> seriesLancamentos = serieRepository.findTop5ByOrderByEpisodiosDataLancamentoDesc();

        return converterListParaDTO(seriesLancamentos);
    }

    public SerieDTO getSerieById(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return converterSerieParaDto(s);
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> getTodasTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }

    }

    public List<EpisodioDTO> getEpisodiosPorTemporada(Long id, int numeroTemporada) {
        Optional<Serie> serieEncontrada = serieRepository.findById(id);

        if (serieEncontrada.isPresent()){
            Serie serie = serieEncontrada.get();

            return serie.getEpisodios().stream()
                    .filter(e -> e.getTemporada() == numeroTemporada)
                    .map(e -> new EpisodioDTO(e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada()))
                    .sorted(Comparator.comparing(EpisodioDTO::numeroEpisodio))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public List<EpisodioDTO> getTop5Episodios(Long id) {
        Optional<Serie> serieEncontrada = serieRepository.findById(id);

        if (serieEncontrada.isPresent()){
            Serie serie = serieEncontrada.get();

            return serie.getEpisodios().stream()
                    .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed())
                    .limit(5)
                    .map(e -> new EpisodioDTO(e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada()))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public List<SerieDTO> getSeriesPorCategoria(String categoriaEscolhida) {
        List<Serie> seriesPorGenero = serieRepository.findByGenero(Categoria.fromString(categoriaEscolhida));

        return converterListParaDTO(seriesPorGenero);
    }

    public SerieDTO salvarSerieOmdb(String nomeSerie) {
        ConsumoApi consumoApi = new ConsumoApi();
        ConverteDados converteDados = new ConverteDados();

        String json = consumoApi.getJson("https://www.omdbapi.com/?t=" + nomeSerie.replace(" ", "+") +"&apikey=a4a34e18");
        DadosSerie dadosSerie = converteDados.getDados(json, DadosSerie.class);
        Serie serie = new Serie(dadosSerie);

        serieRepository.save(serie);
        return converterSerieParaDto(serie);
    }

    public List<EpisodioDTO> salvarEpisodiosOmdb(String nomeSerie) {
        ConsumoApi consumoApi = new ConsumoApi();
        ConverteDados converteDados = new ConverteDados();
        List<DadosTemporada> temporadas = new ArrayList<>();

        Optional<Serie> serieEncontrada = serieRepository.findFirstByTituloContainsIgnoreCase(nomeSerie);

        for (int i = 1; i < serieEncontrada.get().getTotalTemporadas(); i++) {
            String json = consumoApi.getJson("https://www.omdbapi.com/?t=" + serieEncontrada.get().getTitulo().replace(" ", "+")
                    +"&season=" +i+ "&apikey=a4a34e18");
            DadosTemporada dadosTemporada = converteDados.getDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numeroTemp(), e)))
                .collect(Collectors.toList());

        serieEncontrada.get().setEpisodios(episodios);
        serieRepository.save(serieEncontrada.get());

        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada()))
                .collect(Collectors.toList());
    }

    private List<SerieDTO> converterListParaDTO(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getSinopse(), s.getPoster()))
                .collect(Collectors.toList());
    }

    private SerieDTO converterSerieParaDto(Serie s){
        return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getSinopse(), s.getPoster());
    }


}