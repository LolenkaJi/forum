package telran.java41.security.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.java41.accounting.dao.UserAccountRepository;

@Service
@Order(11)
public class OwnerFilter implements Filter {
	@Autowired
	UserAccountRepository repository;

	@Override	
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {	
			String login = request.getUserPrincipal().getName();			
			String name = request.getServletPath();
			Pattern pattern = Pattern.compile(login);
			Matcher matcher = pattern.matcher(name);
			
			if(!matcher.find()) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return ((path.matches("/account/user/\\w+/?") && method.matches("PUT")))
				|| (path.matches("/forum/post/\\w+/?") && method.matches("POST"))
				|| (path.matches("/forum/post/\\w+/comment/\\w+/?") && method.matches("PUT"));
	}

}
