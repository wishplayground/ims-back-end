package lk.ijse.dep11.ims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Null
    private Integer id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotNull(groups = Create.class, message = "Duration should not be empty")
//    TODO : This validations has to done
//    @Pattern(regexp = "^[0-9]+$", message = "Error value")
    private Integer durationInMonths;

    public interface Update extends Default {}
    public interface Create extends Default{}


}
