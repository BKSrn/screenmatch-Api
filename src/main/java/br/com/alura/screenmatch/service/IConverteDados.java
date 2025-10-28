package br.com.alura.screenmatch.service;

public interface IConverteDados {
    <T> T getDados(String json, Class<T> classe);
}
