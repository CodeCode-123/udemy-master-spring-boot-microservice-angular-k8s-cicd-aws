package com.codecode.order.service;

import com.codecode.order.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGenerator {
    private final MongoOperations mongoOperations;

    @Autowired
    public SequenceGenerator(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public int generateNextOrderId() {
        //findAndModify: find a document and modify it in a single atomic operation
        Sequence counter = mongoOperations.findAndModify(
                //query criteria, search for a document with the _id equal to "sequence"
                query(where("_id").is("sequence")),
                //create a new Update object, specifies the field to update and the increment value
                new Update().inc("sequence", 1),
                //the modified document should be returned as the result.
                //upsert(true) specifies that if the document is not found, a new document should be created
                options().returnNew(true).upsert(true),
                //the class type of the document
                Sequence.class);
        assert counter != null;
        return counter.getSequence();
    }
}
