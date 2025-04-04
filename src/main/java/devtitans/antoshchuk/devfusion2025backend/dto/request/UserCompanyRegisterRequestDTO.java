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
public class UserCompanyRegisterRequestDTO extends UserRegisterRequestDTO{
    private String name;
    private String businessStreamName;
    private String companyDescription;
    private String companyWebsiteUrl;

    @Override
    public String toString() {
        return super.toString() + "UserCompanyRegisterRequestDTO{" +
                "name='" + name + '\'' +
                ", businessStreamName='" + businessStreamName + '\'' +
                ", companyDescription='" + companyDescription + '\'' +
                ", companyWebsiteUrl='" + companyWebsiteUrl + '\'' +
                '}';
    }
}
