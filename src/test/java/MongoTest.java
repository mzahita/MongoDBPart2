import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;

public class MongoTest {
    private MongoDatabase database;

    @BeforeClass
    public void init() {
        MongoClient mongoClient = MongoClients.create("mongodb://techno:ee4CvCRPhor5@185.97.114.201:27118/?authSource=cloud-school");
        database = mongoClient.getDatabase("cloud-school");
    }

    @Test
    public void test() {
        MongoCollection<Document> collection = database.getCollection("school_city");
        System.out.println(collection.countDocuments());
    }

    @Test
    public void findOne() {
        MongoCollection<Document> collection = database.getCollection("school_city");
        Document actual = collection.find().first();
        System.out.println(actual);
    }

    @Test
    public void findAll() {
        MongoCollection<Document> collection = database.getCollection("school_city");
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
        cursor.close();
    }

    @Test
    public void findOneUsingFilter() {
        MongoCollection<Document> collection = database.getCollection("school_city");
        Document actual = collection.find(eq("name", "Newyork")).first();
        System.out.println(actual);
    }

    @Test
    public void findAllUsingFilter() {
        MongoCollection<Document> collection = database.getCollection( "school_city" );
        MongoCursor<Document> cursor = collection.find(
                and(
                        eq( "name", "Newyork" ),
                        eq( "deleted", true )
                )
        ).iterator();
        while(cursor.hasNext()) {
            System.out.println( cursor.next() );
        }
        cursor.close();
    }

    @Test
    public void findStudentsInSection() {
        MongoCollection<Document> collection = database.getCollection( "school_student" );
        long count = collection.countDocuments(or(
                eq( "section", "1305000" ),
                eq("section", "0111000")
        ));
        System.out.println(count);
        ArrayList<String> sections = new ArrayList<String>();
        sections.add( "1305000" );
        sections.add( "0111000" );
        long alternativeCount = collection.countDocuments(in("section", sections));

        Assert.assertEquals( count, alternativeCount );
    }

    @Test
    public void findStudentById() {
        MongoCollection<Document> collection = database.getCollection( "school_student" );
        Document student = collection.find(
                new Document( "_id", new ObjectId( "5c6bf3fdead76c69888c98e9" ) )
        ).first();
        System.out.println(student);
        // get field
        System.out.println(student.get( "section" ));
    }

    @Test
    public void TestEditing(){
        MongoCollection<Document> collection = database.getCollection( "school_student" );
    }





}