package az.ubank.msauth.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String customerPin;
    private String name;
    private String surname;
    private String email;
    private String password;
}
