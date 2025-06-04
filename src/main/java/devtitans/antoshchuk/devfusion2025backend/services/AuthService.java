package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserAccountDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserLoginRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.AuthResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.UserAlreadyExistsException;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ValidationException;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserMapper;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final UserMapper userMapper;
    private final UserTypeService userTypeService;
    private final ModelMapper modelMapper;
    private final SeekerRepository seekerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private void validateUserAccount(UserAccountDTO userAccount) {
        if (userAccount == null) {
            throw new ValidationException("Дані користувача не можуть бути порожніми");
        }
        if (userAccount.getEmail() == null || userAccount.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email не може бути порожнім");
        }
        if (userAccount.getPassword() == null || userAccount.getPassword().trim().isEmpty()) {
            throw new ValidationException("Пароль не може бути порожнім");
        }
        if (userAccount.getPassword().length() < 6) {
            throw new ValidationException("Пароль повинен містити щонайменше 6 символів");
        }
        if (!userAccount.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Невірний формат email");
        }
    }

    private void validateSeeker(UserRegisterRequestDTO userRegisterRequestDTO) {
        if (userRegisterRequestDTO.getSeeker() == null) {
            throw new ValidationException("Дані шукача роботи не можуть бути порожніми");
        }
        if (userRegisterRequestDTO.getSeeker().getFirstName() == null || 
            userRegisterRequestDTO.getSeeker().getFirstName().trim().isEmpty()) {
            throw new ValidationException("Ім'я не може бути порожнім");
        }
        if (userRegisterRequestDTO.getSeeker().getLastName() == null || 
            userRegisterRequestDTO.getSeeker().getLastName().trim().isEmpty()) {
            throw new ValidationException("Прізвище не може бути порожнім");
        }
    }

    private void validateCompany(UserRegisterRequestDTO userRegisterRequestDTO) {
        if (userRegisterRequestDTO.getCompany() == null) {
            throw new ValidationException("Дані компанії не можуть бути порожніми");
        }
        if (userRegisterRequestDTO.getCompany().getName() == null || 
            userRegisterRequestDTO.getCompany().getName().trim().isEmpty()) {
            throw new ValidationException("Назва компанії не може бути порожньою");
        }
    }

    @Transactional
    UserAccount registerUser(UserAccountDTO userAccount, UserTypes userType) {
        validateUserAccount(userAccount);
        
        // Перевірка на існуючий email
        if (userAccountRepository.findByEmail(userAccount.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Користувач з таким email вже існує");
        }

        UserAccount registeredAccount = modelMapper.map(userAccount, UserAccount.class);
        registeredAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        registeredAccount.setUserType(userTypeService.getUserTypeByName(userType));
        return userAccountRepository.save(registeredAccount);
    }

    @Transactional
    public UserAccount registerSeeker(UserRegisterRequestDTO userRegisterRequestDTO) {
        validateSeeker(userRegisterRequestDTO);
        
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.SEEKER);
        Seeker seeker = modelMapper.map(userRegisterRequestDTO.getSeeker(), Seeker.class);
        
        seeker.setUserAccount(userAccount);
        userAccount.setSeeker(seeker);
        
        seekerRepository.save(seeker);
        
        return userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount registerCompany(UserRegisterRequestDTO userRegisterRequestDTO) {
        validateCompany(userRegisterRequestDTO);
        
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.COMPANY);
        Company company = modelMapper.map(userRegisterRequestDTO.getCompany(), Company.class);
        company.setUser(userAccount);
        companyRepository.save(company);
        return userAccount;
    }

    public AuthResponseDTO register(UserRegisterRequestDTO request) {
        if (userAccountRepository.findByEmail(request.getUser().getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(request.getUser().getEmail());
        userAccount.setPassword(passwordEncoder.encode(request.getUser().getPassword()));
        userAccount.setContactNumber(request.getUser().getContactNumber());
        
        // Determine user type based on request
        if (request.getSeeker() != null) {
            userAccount.setUserType(userTypeService.getUserTypeByName(UserTypes.SEEKER));
            UserAccount savedUser = userAccountRepository.save(userAccount);
            Seeker seeker = modelMapper.map(request.getSeeker(), Seeker.class);
            seeker.setUserAccount(savedUser);
            savedUser.setSeeker(seeker);
            seekerRepository.save(seeker);
            userAccount = userAccountRepository.save(savedUser);
        } else if (request.getCompany() != null) {
            userAccount.setUserType(userTypeService.getUserTypeByName(UserTypes.COMPANY));
            UserAccount savedUser = userAccountRepository.save(userAccount);
            Company company = modelMapper.map(request.getCompany(), Company.class);
            company.setUser(savedUser);
            companyRepository.save(company);
            userAccount = savedUser;
        } else {
            throw new ValidationException("User type must be specified (seeker or company)");
        }

        // Create authentication token
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUser().getEmail(),
                request.getUser().getPassword()
            )
        );

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setRole(userAccount.getUserType().getName());
        response.setUserId(Long.valueOf(userAccount.getId()));
        return response;
    }

    public AuthResponseDTO authenticate(UserLoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserAccount user = userAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            String token = jwtTokenProvider.generateToken(authentication);

            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            response.setRole(user.getUserType().getName());
            response.setUserId(Long.valueOf(user.getId()));
            return response;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}