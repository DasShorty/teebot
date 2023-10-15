package de.dasshorty.teebot.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Paginator<O extends Object>(List<O> content) {

    public HashMap<Integer, ArrayList<O>> maxSizePerPage(int size) {

        HashMap<Integer, ArrayList<O>> map = new HashMap<>();

        int step = 0;
        for (O position : content) {

            // if nothing exists
            if (!map.containsKey(step)) {
                map.put(step, new ArrayList<>(List.of(position)));

                continue;
            }

            var list = map.get(step);

            // if the list is "full" by the limit provided in header
            if (list.size() == size) {

                step++;

                map.put(step, new ArrayList<>(List.of(position)));

                continue;
            }

            list.add(position);
            map.put(step, list);

        }

        return map;
    }

}
