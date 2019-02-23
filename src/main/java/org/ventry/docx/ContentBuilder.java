package org.ventry.docx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * file: PACKAGE_NAME.net.xuele.common.docx.Content
 * author: jojo
 * create: 2018/7/28 10:52
 * description:
 */

class ContentBuilder {
    private final List<CompletableFuture<CharSequence>> futures;
    private final Executor executor;

    ContentBuilder(Executor executor) {
        this.futures = new ArrayList<>();
        this.executor = executor;
    }

    ContentBuilder append(CharSequence cs) {
        futures.add(CompletableFuture.supplyAsync(() -> cs));
        return this;
    }

    ContentBuilder append(Supplier<CharSequence> func) {
        futures.add(CompletableFuture.supplyAsync(func, executor));
        return this;
    }

    String build() {
        return futures.stream()
                .map(CompletableFuture:: join)
                .collect(Collectors.joining());
    }
}