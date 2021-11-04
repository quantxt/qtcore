package com.quantxt.types;

import java.util.*;

/**
 * Created by matin on 3/21/17.
 */
public class MapSort {

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, Comparator.comparing(Map.Entry::getValue));

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortdescByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, (o1, o2) -> -1 * ( o1.getValue() ).compareTo( o2.getValue() ));

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
