package itq.test.services;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ParallelDocumentService {

    @Autowired
    DocumentService documentService;

    public List<UpdateStatus> submitDoc(long id, int threads, int attempts) {

        try(ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            var tasks = new ArrayList<Callable<ArrayList<UpdateStatus>>>();
            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    var results = new ArrayList<UpdateStatus>();
                    for (int j = 0; j < attempts; j++) {
                        results.add(documentService.approveDocument(id));
                    }
                    return results;
                });
            }
            var futures = executor.invokeAll(tasks);
            return futures.stream().map(Future::resultNow).flatMap(Collection::stream).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
