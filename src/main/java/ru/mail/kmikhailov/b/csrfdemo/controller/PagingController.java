package ru.mail.kmikhailov.b.csrfdemo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mail.kmikhailov.b.csrfdemo.dto.Item;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
public class PagingController {
    private List<Item> sampleData = IntStream.range(0, 1000)
            .mapToObj(i -> new Item(i, i % 2 == 0  ? "even" : "odd"))
            .collect(Collectors.toList());

    @RequestMapping("/paging-test")
    public Mono<Page<Item>> pagingTest(final Pageable pageable) {
        return Mono.just(new PageImpl(sort(sampleData, pageable.getSort())
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList()), pageable, sampleData.size()));
    }

    private Stream<Item> sort(List<Item> content, Sort sort) {
        return Optional.ofNullable(getComparator(sort))
                .map(comparator -> content.stream().sorted(comparator))
                .orElse(content.stream());
    }

    private Comparator<Item> getComparator(Sort sort) {
        Comparator<Item> comparator = null;
        if (sort.isSorted()) {
            var iterator = sort.iterator();
            if (iterator.hasNext()) {
                var order = iterator.next();
                switch (order.getProperty()) {
                    case "id" -> comparator = Comparator.comparingInt(Item::index);
                    case "name" -> comparator = Comparator.comparing(Item::name);
                }
                if (comparator != null && order.isDescending()) {
                    return comparator.reversed();
                }
            }
        }
        return comparator;
    }
}
