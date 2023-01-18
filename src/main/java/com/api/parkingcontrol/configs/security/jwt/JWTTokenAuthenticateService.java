package com.api.parkingcontrol.configs.security.jwt;

import com.api.parkingcontrol.configs.ApplicationContextLoad;
import com.api.parkingcontrol.models.UserModel;
import com.api.parkingcontrol.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/* Criar a autenticação */
@Service
@Component
public class JWTTokenAuthenticateService {

    public static final long EXPIRATION_TIME = 600_000;
    public static final String SECRET = "120912091xjsjnsadjkasd0ghfjjjjjjjhjfyuytytfyt980239810923821sakjdsakjd1029389102381290ksadjas"; //String.valueOf(Keys.secretKeyFor(SignatureAlgorithm.HS512));
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";


    //Gerar o token
    public void addAuthentication(HttpServletResponse response, String username) throws Exception{

        String JWT = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();

        String token = TOKEN_PREFIX + JWT;

        //Retorna para o cliente
        response.addHeader(HEADER_STRING, token);
        response.getWriter().write("{\"Authorization\": \"" +token+ "\"}");
    }

    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response){

        String token = request.getHeader(HEADER_STRING);
        if (token == null){
            return null;
        }

        String cleanToken = token.replace(TOKEN_PREFIX, "").trim();
        String user = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(cleanToken)
                .getBody().getSubject();

        if (user == null){
            return null;
        }

        Optional<UserModel> userModel = ApplicationContextLoad.getApplicationContext()
                .getBean(UserRepository.class).findByUsername(user);

        if (userModel == null){
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userModel.get().getUsername(),
                userModel.get().getPassword(), userModel.get().getAuthorities());
    }
}
