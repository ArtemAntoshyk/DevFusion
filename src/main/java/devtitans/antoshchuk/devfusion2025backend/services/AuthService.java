package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserCompanyRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserMapper;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTypeService userTypeService;
    public UserAccount registerUser(UserRegisterRequestDTO userAccount) {
        UserAccount registeredAccount = userMapper.convertUserRegDTOToUserAcc(userAccount);
        registeredAccount.setUserType(userTypeService.getUserTypeByName(UserTypes.SEEKER));
        System.out.println(registeredAccount);
        return userAccountRepository.save(registeredAccount);
    }
}
