package ch4;

import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
public abstract class GenericController<T, K, S> {
    private S service;

    public void add(T entity){}

    public void update(T entity){}

    public T view(K id){return null;}

    public void delete(Long id){};

    public List<T> list() {return null;}
}
