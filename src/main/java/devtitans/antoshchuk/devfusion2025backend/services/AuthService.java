package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserAccountDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.repositiories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserMapper;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeekerRepository seekerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserAccount registerUser(UserAccountDTO userAccount, UserTypes userType) {
        UserAccount registeredAccount = modelMapper.map(userAccount, UserAccount.class);
        registeredAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        registeredAccount.setUserType(userTypeService.getUserTypeByName(userType));
        return userAccountRepository.save(registeredAccount);
    }

    public void registerSeeker(UserRegisterRequestDTO userRegisterRequestDTO) {
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.SEEKER);
        Seeker seeker = modelMapper.map(userRegisterRequestDTO.getSeeker(), Seeker.class);
        seeker.setUserAccount(userAccountRepository.save(userAccount));
        seekerRepository.save(seeker);
    }
    public void registerCompany(UserRegisterRequestDTO userRegisterRequestDTO) {
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.COMPANY);
        Company company = modelMapper.map(userRegisterRequestDTO.getCompany(), Company.class);
        company.setUser(userAccountRepository.save(userAccount));
        companyRepository.save(company);
    }
}
