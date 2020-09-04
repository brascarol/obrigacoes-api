package br.com.sistemaobrigacoes.servicos;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.sistemaobrigacoes.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	public String gerarToken(Authentication authenticate){
		Usuario logado = (Usuario) authenticate.getPrincipal();
		Date hoje =  new Date();
		Date dataExpiracao = new Date(hoje.getTime() + 50000);
		return Jwts.builder()
				.setIssuer("nome da aplicacao")
				.setSubject(logado.getUsuarioId().toString())
				.claim("usuario",logado )
				.setIssuedAt(hoje)
				.setExpiration(dataExpiracao)
				.signWith(SignatureAlgorithm.HS256, "x1b1c4").compact();
	}

	public boolean isValido(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey("x1b1c4").parseClaimsJws(token);
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	public Integer getIdUsuario(String token) {
		Claims claims = Jwts.parser().setSigningKey("x1b1c4").parseClaimsJws(token).getBody();
		return Integer.parseInt(claims.getSubject());
	}

}
