package com.javadeveloperzone.service.SequenceGeneratorService;

import com.javadeveloperzone.models.EmployeeModels.EmployeeDatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class EmployeeSequenceGeneratorService {

    private final MongoOperations mongoOperations;

    @Autowired
    public EmployeeSequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {
        EmployeeDatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                EmployeeDatabaseSequence.class);
        return counter != null ? counter.getSeq() : 1;
    }
}
