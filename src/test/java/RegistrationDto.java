import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Locale;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RegistrationDto {
    private String login;
    private String password;
    private String status;

    public static RegistrationDto generate (String status) {
        Faker faker = new Faker(new Locale("ru"));
        return new RegistrationDto(
                faker.name().firstName(),
                faker.internet().password(),
                status
        );
    }
}
