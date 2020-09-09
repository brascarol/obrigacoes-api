package br.com.sistemaobrigacoes.security.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.sistemaobrigacoes.modelo.Usuario;
import br.com.sistemaobrigacoes.repositorios.UsuarioRepository;
import br.com.sistemaobrigacoes.servicos.TokenService;

public class AuthenticationFilter extends OncePerRequestFilter{
	
	private TokenService tokenService;
	private UsuarioRepository usuarioRepository;
	
	public AuthenticationFilter(TokenService tokenService,UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository =  usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		String token =  recuperarToken(req);	
		//System.out.println("token recuperado");
		boolean valido =  tokenService.isValido(token);
		if(valido) {
			//System.out.println("token é válido");
			autenticarCliente(token);
		}
		chain.doFilter(req, resp);
	}
	
	private void autenticarCliente(String token) {
		//System.out.println("token recebido: "+token);
		Integer idUsuario = tokenService.getIdUsuario(token);
		//System.out.println("id usuario: "+idUsuario);
		//essa busca é desnecessária,basta recuperar a claim referente ao usuário
		//a claim está em formato json, usar objectmapper e converter em usuário
		Optional<Usuario> optional = usuarioRepository.findById(idUsuario);
		Usuario usuario = null;
		if(optional.isPresent()) {
			//System.out.println("usuário presente");
			usuario =  optional.get();
		}
		UsernamePasswordAuthenticationToken  authentication =  new 
				UsernamePasswordAuthenticationToken(usuario,null, usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String recuperarToken(HttpServletRequest req) {
		String token  = req.getHeader("Authorization");
		if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7,token.length());
	}

}
