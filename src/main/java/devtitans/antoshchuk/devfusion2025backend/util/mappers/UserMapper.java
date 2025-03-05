package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserCompanyRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import devtitans.antoshchuk.devfusion2025backend.services.SeekerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private ModelMapper modelMapper;
    private CompanyService companyService;
    private SeekerService seekerService;
    @Autowired
    public UserMapper(ModelMapper modelMapper, CompanyService companyService, SeekerService seekerService) {
        this.modelMapper = modelMapper;
        this.companyService = companyService;
        this.seekerService = seekerService;
    }

    public UserAccount convertUserRegDTOToUserAcc(UserRegisterRequestDTO userRegDTO) {
        UserAccount userAcc = modelMapper.map(userRegDTO, UserAccount.class);
        if(userRegDTO instanceof UserSeekerRegisterRequestDTO){
            Seeker seeker = new Seeker();
            seeker.setFirstName(((UserSeekerRegisterRequestDTO) userRegDTO).getFirstName());
            seeker.setLastName(((UserSeekerRegisterRequestDTO) userRegDTO).getLastName());
            seeker.setDateOfBirth(((UserSeekerRegisterRequestDTO) userRegDTO).getDateOfBirth());
//            seeker.setUserAccount(userAcc);
            seekerService.createSeeker(seeker);
            userAcc.setSeeker(seeker);
        }
        else if(userRegDTO instanceof UserCompanyRegisterRequestDTO){
            Company company = new Company();
            company.setName(((UserCompanyRegisterRequestDTO) userRegDTO).getName());
            company.setBusinessStreamName(((UserCompanyRegisterRequestDTO) userRegDTO).getBusinessStreamName());
            company.setCompanyDescription(((UserCompanyRegisterRequestDTO) userRegDTO).getCompanyDescription());
            company.setCompanyWebsiteUrl(((UserCompanyRegisterRequestDTO) userRegDTO).getCompanyWebsiteUrl());
            company = companyService.createCompany(company);
            userAcc.setCompany(company);
            System.out.println("Comp user: " + userAcc);
        }
        return userAcc;
    }
}
