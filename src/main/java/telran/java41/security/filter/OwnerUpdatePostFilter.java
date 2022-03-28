package telran.java41.security.filter;

import java.io.IOException;

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

import telran.java41.forum.dao.PostRepository;
import telran.java41.forum.model.Post;

@Service
@Order(12)
public class OwnerUpdatePostFilter implements Filter {
	
	@Autowired
	PostRepository repository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String login = request.getUserPrincipal().getName();
			String[] res = request.getServletPath().split("/");
			String idPost = res[3];
			Post post = repository.findById(idPost).orElse(null);
			if(!post.getAuthor().equals(login)) {
				response.sendError(403, "OwnerUpdateFilter12");
				return;
			}			
		}
		chain.doFilter(request, response);
	}	
	private boolean checkEndPoint(String method, String path) {
		return (path.matches("/forum/post/\\w+/?")&& method.matches("PUT"));
	}

}
