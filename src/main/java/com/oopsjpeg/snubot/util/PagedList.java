package com.oopsjpeg.snubot.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PagedList<T> extends LinkedList<T>
{
    private int resultsPerPage;

    public PagedList(Collection<? extends T> collection, int resultsPerPage)
    {
        super(collection);
        this.resultsPerPage = resultsPerPage;
    }

    public LinkedList<T> page(int page)
    {
        int fromIndex = page * resultsPerPage;
        int toIndex = Math.min(size(), fromIndex + resultsPerPage - 1);
        return new LinkedList<>(subList(fromIndex, toIndex));
    }

    public String format(int page, Function<T, String> toString)
    {
        return page(page).stream().map(toString).collect(Collectors.joining("\n"));
    }

    public int pages()
    {
        return (int) Math.ceil((double) size() / resultsPerPage);
    }

    public int getResultsPerPage()
    {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage)
    {
        this.resultsPerPage = resultsPerPage;
    }
}
