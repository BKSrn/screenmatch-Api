package br.com.alura.screenmatch.model;

public enum Categoria {

    ACAO("Action", "Ação"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    ROMANCE("Romance", "Romance"),
    ANIMACAO("Animation", "Animação") ,
    AVENTURA("Adventure", "Aventura");

    private String categoriaOmdb;
    private String categoriaPortugues;

    Categoria (String categoriaOmdb, String categoriaPortugues){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }
    public static Categoria fromString(String texto){
            for (Categoria categoria : Categoria.values()){
                if (categoria.categoriaOmdb.equalsIgnoreCase(texto) || categoria.categoriaPortugues.equalsIgnoreCase(texto)){
                    return categoria;
                }
            }
            throw new IllegalArgumentException("Não foi possível achar essa categoria");
    }
}
