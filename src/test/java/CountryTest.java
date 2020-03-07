import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.restassured.http.ContentType;
import model.Country;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.BaseTest;

import java.util.Collection;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CountryTest extends BaseTest {

    private String apiPath = "/school-service/api/bank-accounts";
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @BeforeClass
    public void init() {
        MongoClient mongoClient = MongoClients.create("mongodb://techno:ee4CvCRPhor5@185.97.114.201:27118/?authSource=cloud-school");
        database = mongoClient.getDatabase("cloud-school");
        collection = database.getCollection("school_country");
    }


    @Test
    public void getBasePath() {
        given()
                .when()
                .log().body()
                .get()
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }

    @Test
    public void getCountries() {
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .get( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }


    @Test
    public void createCountry() {
        Country country = new Country();
        country.setName( name );
        country.setCode( code );

        long DefaultCountry = collection.countDocuments(eq("deleted", false));
        System.out.println(DefaultCountry);

        // creating country
        String countryId = given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );


        long CreateCountry = collection.countDocuments(eq("deleted", false));
        System.out.println(CreateCountry);

        Assert.assertEquals(DefaultCountry+1,CreateCountry);

        Document entity = collection.find(eq("_id",new ObjectId(countryId))).first();
        Assert.assertNotNull( entity );
        Assert.assertEquals( entity.get( "name" ), country.getName() );
        Assert.assertEquals( entity.get( "code" ), country.getCode() );


        // deleting country
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( "/school-service/api/countries/" + countryId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;


        long DeletedCountry = collection.countDocuments(eq("deleted", false));
        System.out.println(DeletedCountry);
        Assert.assertEquals(DefaultCountry,DeletedCountry);

        Document entityAfterDeletion = collection.find(
                and(
                        eq( "_id", new ObjectId( countryId ) ),
                        eq( "deleted", false )
                )
        ).first();
        Assert.assertNull( entityAfterDeletion );
    }

    @Test
    public void editTest() {
        Country country = new Country();
        country.setName( name );
        country.setCode( code );

        // creating country
        String countryId = given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        // Editing country
        country.setId( countryId );
        country.setName( nameEdited );
        country.setCode( codeEdited );
        given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .put( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 200 )
                .body( "name", equalTo( country.getName() ) )
                .body( "code", equalTo( country.getCode() ) )
        ;

        // deleting country
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( "/school-service/api/countries/" + countryId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }

    @Test
    public void createCountryNegativeTest() {
        Country country = new Country();
        country.setName( name );
        country.setCode( code );

        // creating country
        String countryId = given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 400 );

        // deleting country
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( "/school-service/api/countries/" + countryId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;
    }

    @Test
    public void deleteCountryNegativeTest() {
        Country country = new Country();
        country.setName( name );
        country.setCode( code );

        // creating country
        String countryId = given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .log().body()
                .post( "/school-service/api/countries" )
                .then()
                .log().body()
                .statusCode( 201 )
                .extract().jsonPath().getString( "id" );

        // deleting country
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( "/school-service/api/countries/" + countryId )
                .then()
                .log().body()
                .statusCode( 200 )
        ;

        // deleting country again
        given()
                .cookies( cookies )
                .when()
                .log().body()
                .delete( "/school-service/api/countries/" + countryId )
                .then()
                .log().body()
                .statusCode( 404 )
        ;
    }

}