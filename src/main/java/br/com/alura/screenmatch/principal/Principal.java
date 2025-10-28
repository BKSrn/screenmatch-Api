package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class Principal {
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=a4a34e18";
    private final String MENU = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar series buscadas
                4 - Buscar serie por titulo
                5 - Buscar series por ator 
                6 - Buscar TOP 5 Series
                7 - Buscar serie por categoria
                8 - Filtrar series 
                9 - Busca episodio por trecho 
                10 - Buscar TOP 5 Episodios por Serie
                11 - Filtrar episodios por ano 
               \s
                0 - Sair                                \s
               \s""";

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository serieRepository;
    private List<Serie> series;


    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }


    public void exibeMenu() {
        int opcao;
        do {
            System.out.println(MENU);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTopSeries();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeries();
                    break;
                case 9:
                    buscarEpisodioPorTreco();
                    break;
                case 10:
                    buscarTopEpisodiosPorSerie();
                    break;
                case 11:
                    filtrarEpisodiosPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }while (opcao != 0);
    }

    private void listarSeriesBuscadas(){
        //Busca todos os cadastros da entidade Serie
        series = serieRepository.findAll();
        series.forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //chamando o repositorio para salvar no DB
        serieRepository.save(serie);
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.getJson(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.getDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma serie pelo nome: ");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie = serieRepository.findFirstByTituloContainsIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String json = consumo.getJson(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.getDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numeroTemp(), e)))
                    .toList();

            serieEncontrada.setEpisodios(episodios);
            episodios.forEach(System.out::println);
            serieRepository.save(serieEncontrada);
        }else {
            System.out.println("Serie nao encontrada!");
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Coloque um trecho do titulo: ");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findFirstByTituloContainsIgnoreCase(nomeSerie);
        if (serieBuscada.isPresent()){
            System.out.println("Dados da serie: " + serieBuscada.get().toString());
        }else {
            System.out.println("Serie nao encontrada ");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Informe o nome do ator: ");
        String nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor: ");
        double avaliacao = leitura.nextDouble();

        List<Serie> seriesDoAtor = serieRepository.findByAtoresContainsIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        seriesDoAtor.forEach(s ->
                System.out.println(s.getTitulo() + " Avaliação: " + s.getAvaliacao() + " Colegas: " + s.getAtores()));
    }

    private void buscarTopSeries() {
        List<Serie> topSeries = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s ->
                System.out.println(s.getTitulo() + " Avaliação :" + s.getAvaliacao()));
    }


    private void buscarSeriePorCategoria() {
        System.out.println("Qual categoria deseja buscar: ");
        String nomeCategoria = leitura.nextLine();

        Categoria categoria = Categoria.fromString(nomeCategoria);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);

        seriesPorCategoria.forEach(s ->
                System.out.println("Genero: " +s.getGenero() +" Titulo: "+ s.getTitulo()));
    }

    private void filtrarSeries() {
        System.out.println("Quantas temporadas a serie pode ter: ");
        Integer temporadas = leitura.nextInt();
        System.out.println("Avaliações a partir de qual valor: ");
        double avaliacao = leitura.nextDouble();

        List<Serie> seriesPorTemporada = serieRepository.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);
        seriesPorTemporada.forEach(System.out::println);
    }

    private void buscarEpisodioPorTreco() {
        System.out.println("Informe um trecho do episodio: ");
        String trecho = leitura.nextLine();
        List<Episodio> episodiosEncotrados = serieRepository.epiosdiosPorTrecho(trecho);

        episodiosEncotrados.forEach(e -> System.out.println(
                e.getSerie().getTitulo() +
                " Temporada: " + e.getTemporada() +
                " Episodio: " + e.getTitulo() +
                " Avaliação: " + e.getAvaliacao()));
    }

    private void buscarTopEpisodiosPorSerie() {
        System.out.println("Escolha a serie: ");
        String nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = serieRepository.findFirstByTituloContainsIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.println(
                    e.getSerie().getTitulo() +
                    " Temporada: " + e.getTemporada() +
                    " Episodio: " + e.getTitulo() +
                    " Avaliação: " + e.getAvaliacao()));
        }
    }


    private void filtrarEpisodiosPorData() {
        System.out.println("Escolha a serie: ");
        String nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = serieRepository.findFirstByTituloContainsIgnoreCase(nomeSerie);


        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            System.out.println("A partir de qual ano deseja ver: ");
            int anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosFiltrados = serieRepository.episodioPorSerieEAno(serie, anoLancamento);
            episodiosFiltrados.forEach(System.out::println);
        }
    }

}