import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class AuthTest {
    static RegistrationDto registrationDtoActive;
    static RegistrationDto registrationDtoBlocked;

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {
        registrationDtoActive = RegistrationDto.generate("active");
        given()
                .spec(requestSpec)
                .body(registrationDtoActive)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
        registrationDtoBlocked = RegistrationDto.generate("blocked");

        given()
                .spec(requestSpec)
                .body(registrationDtoBlocked)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldRegistatedActive() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue(registrationDtoActive.getLogin());
        form.$("[data-test-id=password] input").setValue(registrationDtoActive.getPassword());
        form.$(".button").click();
        $$(".heading").find(exactText("Личный кабинет")).shouldBe(exist);
    }

    @Test
    void shouldRegistatedBlocked() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue(registrationDtoBlocked.getLogin());
        form.$("[data-test-id=password] input").setValue(registrationDtoBlocked.getPassword());
        form.$(".button").click();
        $(withText("Пользователь заблокирован")).shouldBe(exist);
    }

    @Test
    void shouldRegistatedInvalidLogin() {
        Faker faker = new Faker();
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue(faker.name().firstName());
        form.$("[data-test-id=password] input").setValue(registrationDtoActive.getPassword());
        form.$(".button").click();
        $(withText("Неверно указан логин или пароль")).shouldBe(exist);
    }

    @Test
    void shouldRegistatedInvalidPassword() {
        Faker faker = new Faker();
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue(registrationDtoActive.getLogin());
        form.$("[data-test-id=password] input").setValue(faker.internet().password());
        form.$(".button").click();
        $(withText("Неверно указан логин или пароль")).shouldBe(exist);
    }
}
