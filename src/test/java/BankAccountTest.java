import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.restassured.http.ContentType;
import model.BankAccount;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BankAccountTest extends BaseTest {
    private String apiPath = "/school-service/api/bank-accounts";
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @BeforeClass
    public void init() {
        MongoClient mongoClient = MongoClients.create("mongodb://techno:ee4CvCRPhor5@185.97.114.201:27118/?authSource=cloud-school");
        database = mongoClient.getDatabase("cloud-school");
    }


    @Test
    public void createTest() {

        collection = database.getCollection("school_bank_account");
        long initialCount = collection.countDocuments();
        System.out.println(collection.countDocuments());

        BankAccount model = getBody();

        // creating entity
        String entityId = given()
                .cookies( cookies )
                .body( model )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( apiPath )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        Document entity = getEntityById( entityId );
        System.out.println(entity);
        Assert.assertNotNull(entity);
        // compare the fields
        Assert.assertEquals(entity.get( "name" ), model.getName());
        Assert.assertEquals(entity.get( "integrationCode" ), model.getIntegrationCode());
        Assert.assertEquals(entity.get( "iban" ), model.getIban());
        Assert.assertEquals(entity.get( "currency" ), model.getCurrency());
        Assert.assertEquals(entity.get( "currency" ), model.getCurrency());

        collection = database.getCollection("school_bank_account");
        long afterCreationCount = collection.countDocuments();
        System.out.println(collection.countDocuments());

        Assert.assertEquals(afterCreationCount - 1,initialCount);
        Assert.assertEquals(initialCount + 1,afterCreationCount);




        // deleting entity
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( apiPath + "/" + entityId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }

    private BankAccount getBody() {
        BankAccount model = new BankAccount();
        model.setName( name );
        model.setIban( code );
        model.setIntegrationCode( "code" );
        model.setCurrency( "KZT" );
        model.setSchoolId( schoolId );
        return model;
    }

    @Test
    public void editTest() {

        BankAccount model = getBody();

        // creating model
        String entityId = given()
                .cookies( cookies )
                .body( model )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( apiPath )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        // Editing model
        model.setId( entityId );
        model.setName( nameEdited );
        model.setIban( codeEdited );
        given()
                .cookies( cookies )
                .body( model )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .put( apiPath )
                .then()
                .log().body()
                .statusCode( 200 )
                .body( "name", equalTo( model.getName() ) )
                .body( "iban", equalTo( model.getIban() ) )
        ;
        // get model from db by id
        Document entity = getEntityById( entityId );

        // check that name and code is edited correctly
        Assert.assertEquals(entity.get( "name" ), model.getName());
        Assert.assertEquals(entity.get( "iban" ), model.getIban());

        // deleting model
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( apiPath + "/" + entityId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }

    @Test
    public void createNegativeTest() {
        BankAccount entity = getBody();

        collection = database.getCollection("school_bank_account");
        long DefaultCount = collection.countDocuments();
        System.out.println(collection.countDocuments());

        // creating entity
        String entityId = given()
                .cookies( cookies )
                .body( entity )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( apiPath )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        collection = database.getCollection("school_bank_account");
        long CreationCount = collection.countDocuments();
        System.out.println(collection.countDocuments());



        // entity creation negative test
        given()
                .cookies( cookies )
                .body( entity )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( apiPath )
                .then()
                .log().body()
                .statusCode( 400 )
        ;

        collection = database.getCollection("school_bank_account");
        long afterNegativeCreationCount = collection.countDocuments();
        System.out.println(collection.countDocuments());

        Assert.assertEquals(afterNegativeCreationCount ,CreationCount);

        // deleting entity
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( apiPath + "/" + entityId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;

        collection = database.getCollection("school_bank_account");
        long afterDeletingNegativeCreationCount = collection.countDocuments();
        System.out.println(collection.countDocuments());

        Assert.assertEquals(DefaultCount-1 ,afterDeletingNegativeCreationCount);


    }

    @Test
    public void deleteNegativeTest() {
        BankAccount entity = getBody();

        // creating entity
        String entityId = given()
                .cookies( cookies )
                .body( entity )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( apiPath )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        // deleting entity
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( apiPath + "/" + entityId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;

        // deleting entity again
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( apiPath + "/" + entityId )
                .then()
                .log().body()
                .statusCode( 404 )
        ;

    }

    private Document getEntityById(String entityId) {
        collection = database.getCollection( "school_bank_account" );
        return collection.find(
                new Document( "_id", new ObjectId( entityId ) )
        ).first();
    }

}