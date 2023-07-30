package com.example.demo.appuser;

import com.example.demo.login.LoginDTO;
import com.example.demo.login.LoginResponse;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format(USER_NOT_FOUND,email)));
    }

    public String signUpUser(AppUser appUser){
        boolean userExist = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if(userExist){
//           TODO check of attributes are the same and
//           TODO if email not confirmed send confirmation email.
            throw new IllegalStateException("email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser

        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO: SEND EMAIL

        return token;
    }


    public LoginResponse loginUser(LoginDTO loginDTO){

        AppUser appUser = appUserRepository.findUserByEmail(loginDTO.getEmail());

        if(appUser != null){
            String msg = "";
            String password = loginDTO.getPassword();
            String encodedPassword = appUser.getPassword();
            boolean isPwdRight = bCryptPasswordEncoder.matches(password, encodedPassword);
            if(isPwdRight){
                Optional<AppUser> user = appUserRepository.findOneByEmailAndPassword(loginDTO.getEmail(), encodedPassword);
                if(user.isPresent()){
                    return new LoginResponse("Login Success", true);
                }else {
                    return new LoginResponse("Login Failed", false);
                }
            }else {
                return new LoginResponse("password Not Match", false);
            }
        }else {
            return new LoginResponse("email not exits", false);
        }
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
