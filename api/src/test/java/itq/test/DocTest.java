package itq.test;

import itq.test.entities.enums.DocumentStatus;
import itq.test.repositories.DocumentRepository;
import itq.test.services.DocumentService;
import itq.test.services.UpdateStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

@SpringBootTest
public class DocTest {

    public static final String TEST_DOC_PLEASE_IGNORE = "TEST_DOC_PLEASE_IGNORE";
    public static final long NON_KEY = -525223523L;
    @Autowired
    private DocumentRepository repository;

    @Autowired
    private DocumentService service;

    @Test
    public void test() {
        //В истории не сохраняется имя изменившего т.к. в тз не было указана таблица для пользователей или система авторизации
        var random = new Random();
        var docName = String.format("%s_%s", TEST_DOC_PLEASE_IGNORE, random.nextInt(1, 999999));
        service.createDocument(docName, "author@mail.com");
        var doc = repository.findByTitle(docName);
        var results = service.submitDocuments(List.of(doc.getId(), NON_KEY));
        Assert.isTrue(results.get(doc.getId()) == UpdateStatus.SUCCESS, "submit failed");
        Assert.isTrue(results.get(NON_KEY) == UpdateStatus.NOT_FOUND, "non key should be empty");
        var result = service.approveDocument(doc.getId());
        doc = repository.findByTitle(docName);
        Assert.isTrue(result == UpdateStatus.SUCCESS, "approve failed");
        Assert.isTrue(doc.getStatus() == DocumentStatus.APPROVED, "approve status change failed");
        //При ошибке транзакция просто не пройдет, в регистр не запишется
    }

}
