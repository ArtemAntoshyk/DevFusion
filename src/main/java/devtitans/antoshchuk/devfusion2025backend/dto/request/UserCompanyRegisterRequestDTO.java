package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCompanyRegisterRequestDTO{
    private String name;
    private String businessStreamName;
    private String companyDescription;

    @Override
    public String toString() {
        return super.toString() + "UserCompanyRegisterRequestDTO{" +
                "name='" + name + '\'' +
                ", businessStreamName='" + businessStreamName + '\'' +
                ", companyDescription='" + companyDescription + '\'' +
                '}';
    }
}
